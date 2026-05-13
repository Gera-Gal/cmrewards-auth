package com.praxis.authentication.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//En tu LoginController o crear un nuevo controller
@Controller
public class LogoutController {
 
 @PostMapping("/api/logout/clear-session")
 public ResponseEntity<?> clearSession(HttpServletRequest request, HttpServletResponse response) {
     // Invalidar sesión
     HttpSession session = request.getSession(false);
     if (session != null) {
         session.invalidate();
     }
     
     // Limpiar la cookie JSESSIONID
     Cookie cookie = new Cookie("JSESSIONID", null);
     cookie.setPath("/");
     cookie.setHttpOnly(true);
     cookie.setMaxAge(0);
     cookie.setSecure(false); // HTTP
     response.addCookie(cookie);
     
     // También limpiar cualquier otra cookie de sesión
     Cookie sessionCookie = new Cookie("SESSION", null);
     sessionCookie.setPath("/");
     sessionCookie.setMaxAge(0);
     response.addCookie(sessionCookie);
     
     return ResponseEntity.ok().body(Map.of("success", true));
 }
}
