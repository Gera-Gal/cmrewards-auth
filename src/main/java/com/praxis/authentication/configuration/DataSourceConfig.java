package com.praxis.authentication.configuration;

import com.praxis.authentication.util.AESUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

	@Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String encryptedPassword;
    
    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Bean
    DataSource dataSource() throws Exception {

        // Desencriptar la contraseña
        String password = AESUtil.decrypt(encryptedPassword);        
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);

        // Opciones recomendadas
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("HikariPool-SQLServer");

        return new HikariDataSource(config);
    }
    
}
