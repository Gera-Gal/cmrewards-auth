package com.praxis.authentication.business.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomPasswordEncoder implements PasswordEncoder{

	private final Logger log = LoggerFactory.getLogger(CustomPasswordEncoder.class);
	
	@Override
	public String encode(CharSequence rawPassword) {
		
		String password = "C0bR0".concat(rawPassword.toString()).concat("M0v1L");
		String sha1 = "";
		
		try{
			MessageDigest crypt = MessageDigest.getInstance("SHA-256");
			crypt.reset();
			crypt.update(password.getBytes("UTF-8"));
			byte [] cryptBytes = crypt.digest(); 
			
			Formatter formatter = new Formatter();
		    for (byte b : cryptBytes)
		    {
		        formatter.format("%02x", b);
		    }
		    sha1 = formatter.toString();
		    formatter.close();
	        
	    }catch(NoSuchAlgorithmException e){
	    	log.error("Error: "+ e.getMessage(), e);	    
	    }
	    catch(UnsupportedEncodingException e){
	    	log.error("Error: "+ e.getMessage(), e);
	    }
				
		return sha1;
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return encodedPassword.equals(encode(rawPassword));
	}

}