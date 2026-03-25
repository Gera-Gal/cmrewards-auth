package com.praxis.authentication.configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.praxis.authentication.business.security.CustomPasswordEncoder;
import com.praxis.authentication.business.security.UserDetailsImpl;
import com.praxis.authentication.business.security.UserDetailsServiceImpl;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.security.web.authentication.AuthenticationConverter;

@Configuration
@EnableWebSecurity
public class SecurityServerConfig {
	
	private static final Logger log = LoggerFactory.getLogger(SecurityServerConfig.class);

	@Autowired
	private Environment environment;
	
	@Autowired
    private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
    private CustomPasswordEncoder passwordEncoder;
	
	@Autowired
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
        	.passwordEncoder(passwordEncoder); // Configure a password encoder
    }
	
	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, RegisteredClientRepository registeredClientRepository) throws Exception {
		
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
			.clientAuthentication(authentication -> {
				authentication.authenticationConverter(new PublicClientRefreshTokenAuthenticationConverter());
				authentication.authenticationProvider(new PublicClientRefreshProvider(registeredClientRepository));
			})
			.tokenGenerator(tokenGenerator())
			.oidc(Customizer.withDefaults());

		http.exceptionHandling((exceptions) -> exceptions
			.defaultAuthenticationEntryPointFor(
				new LoginUrlAuthenticationEntryPoint("/login"),
				new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
			)
		)
		.oauth2ResourceServer((resourceServer) -> resourceServer
				.jwt(Customizer.withDefaults()));

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
				.requestMatchers("/login", "/images/**", "/css/**", "/js/**").permitAll()
				.anyRequest().authenticated()
			)
			.csrf(csrf -> csrf.disable())//deshabilitar token de formulario
			//.formLogin(Customizer.withDefaults());
			.formLogin(form -> form
		            .loginPage("/login")           // página personalizada
		            .loginProcessingUrl("/login") // POST
		            //.defaultSuccessUrl("/", true)
		            .failureUrl("/login?error")
		            .permitAll()
		        )
		        .logout(logout -> logout				//para logout web tradicional
		            .logoutSuccessUrl("/login?logout")
		        );

		return http.build();
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository() {		
		
		RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
			.clientId(environment.getRequiredProperty("spring.authorizationserver.client-id"))			//coincidir
			.clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
			.redirectUri(environment.getRequiredProperty("spring.authorizationserver.redirect-uri"))	//aplicacion cliente, coincidir
			.redirectUri(environment.getRequiredProperty("spring.authorizationserver.redirect-uri-aut"))
			.postLogoutRedirectUri(environment.getRequiredProperty("spring.authorizationserver.logout-uri"))//aplicacion cliente
			.scope("read")
			.scope("write")
			.scope(OidcScopes.OPENID)
			.scope(OidcScopes.PROFILE)
			.tokenSettings(TokenSettings.builder()
	                .accessTokenTimeToLive(Duration.ofMinutes(15))
	                .refreshTokenTimeToLive(Duration.ofHours(2))
	                //.reuseRefreshTokens(false)
	                .build())
			.clientSettings(ClientSettings.builder()
				.requireAuthorizationConsent(false)
				.requireProofKey(true)				
				.build())			
			.build();
		
		RegisteredClient clientCmpay = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId(environment.getRequiredProperty("spring.authorizationserver.client-idCmpay"))			//coincidir
				.clientSecret(environment.getRequiredProperty("spring.authorizationserver.client-secretCmpay"))
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.scope("read")																				
				.scope("write")
				.build();
		
		return new InMemoryRegisteredClientRepository(oidcClient, clientCmpay);
	}
	
	@Bean
	public JWKSource<SecurityContext> jwkSource() {
		KeyPair keyPair = generateRsaKey();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAKey rsaKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	private static KeyPair generateRsaKey() {
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}
	
	OAuth2TokenCustomizer<JwtEncodingContext> customizer(){
		return context -> {
			log.info("Generando token de acceso...");
			//if(context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
			if(context.getTokenType().getValue().equals("access_token") 
				&& !context.getAuthorizationGrantType().equals(AuthorizationGrantType.CLIENT_CREDENTIALS)) {
				Authentication principle = context.getPrincipal();
				
				log.info("Obteniendo UserId");
				
				UserDetailsImpl userDetail = (UserDetailsImpl) context.getPrincipal().getPrincipal();				
                context.getClaims().claim("user_id", userDetail.getUserId());
                context.getClaims().claim("email", userDetail.getEmail());
                
                if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
	                // Extraer roles de las autoridades
	                Set<String> roles = new HashSet<>();
	                
	                for (GrantedAuthority authority : principle.getAuthorities()) {
	                    String role = authority.getAuthority();
	                    if (role.startsWith("ROLE_")) {
	                        role = role.substring(5); // Quitar "ROLE_"
	                    }
	                    roles.add(role);
	                }
	                context.getClaims().claim("roles", roles);
	            }
                
                log.info("UserId Obtenido:"+ userDetail.getUserId());
			}
		};
	}
	
	@Bean
	OAuth2TokenGenerator<?> tokenGenerator(){
		JwtGenerator jwtGenerator = new JwtGenerator(new NimbusJwtEncoder(jwkSource()));
		jwtGenerator.setJwtCustomizer(customizer());		
		OAuth2TokenGenerator<OAuth2RefreshToken> refreshTokenOauth2TokenGenerator = new CustomOAuth2RefreshTokenGenerator();
		return new DelegatingOAuth2TokenGenerator(jwtGenerator, refreshTokenOauth2TokenGenerator);
	}
	
	public final class CustomOAuth2RefreshTokenGenerator implements OAuth2TokenGenerator<OAuth2RefreshToken> {

		//private final StringKeyGenerator refreshTokenGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);
		private final StringKeyGenerator refreshTokenGenerator = new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 512);

		public CustomOAuth2RefreshTokenGenerator() {			
		}
		
		@Nullable
		@Override
		public OAuth2RefreshToken generate(OAuth2TokenContext context) {
			if (!OAuth2TokenType.REFRESH_TOKEN.equals(context.getTokenType())) {
				return null;
			}
			Instant issuedAt = Instant.now();
			Instant expiresAt = issuedAt.plus(context.getRegisteredClient().getTokenSettings().getRefreshTokenTimeToLive());
			return new OAuth2RefreshToken(this.refreshTokenGenerator.generateKey(), issuedAt, expiresAt);
		}

	}
	
	private static final class PublicClientRefreshTokenAuthentication extends OAuth2ClientAuthenticationToken{

		public PublicClientRefreshTokenAuthentication(String clientId) {
			super(clientId, ClientAuthenticationMethod.NONE, null, null);
		}
		
		public PublicClientRefreshTokenAuthentication(RegisteredClient registeredClient) {
			super(registeredClient, ClientAuthenticationMethod.NONE, null);
		}
		
	}
	
	private static final class PublicClientRefreshTokenAuthenticationConverter implements AuthenticationConverter{

		@Override
		public Authentication convert(HttpServletRequest request) {
			String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
			if(!grantType.equals(AuthorizationGrantType.REFRESH_TOKEN.getValue())) {
				return null;
			}
			
			String clientId = request.getParameter(OAuth2ParameterNames.CLIENT_ID);
			if(!StringUtils.hasText(clientId)) {
				return null;
			}
			
			return new PublicClientRefreshTokenAuthentication(clientId);
		}		
	}
	
	private static final class PublicClientRefreshProvider implements AuthenticationProvider{

		private final RegisteredClientRepository registeredClientRepository;
		
		private PublicClientRefreshProvider(RegisteredClientRepository registeredClientRepository) {
			this.registeredClientRepository = registeredClientRepository;
		}
		
		@Override
		public Authentication authenticate(Authentication authentication) throws AuthenticationException {			
			
			PublicClientRefreshTokenAuthentication publicClientRefreshTokenAuthentication = (PublicClientRefreshTokenAuthentication) authentication;
			
			if(!ClientAuthenticationMethod.NONE.equals(publicClientRefreshTokenAuthentication.getClientAuthenticationMethod())) {
				return null;
			}
			
			String clientId = publicClientRefreshTokenAuthentication.getPrincipal().toString();
			RegisteredClient registeredClient = registeredClientRepository.findByClientId(clientId);
			
			if(registeredClient == null) {
				throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT, "client is not valid",null));
			}
			
			if(!registeredClient.getClientAuthenticationMethods().contains(publicClientRefreshTokenAuthentication.getClientAuthenticationMethod())){
				throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_CLIENT, "authentication_method is not register with client",null));
			}
			
			return new PublicClientRefreshTokenAuthentication(registeredClient);
			
		}

		@Override
		public boolean supports(Class<?> authentication) {
			return PublicClientRefreshTokenAuthentication.class.isAssignableFrom(authentication);
		}
		
	}

}