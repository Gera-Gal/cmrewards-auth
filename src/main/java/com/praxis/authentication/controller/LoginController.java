package com.praxis.authentication.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @GetMapping("/login")
    public String login(HttpServletRequest request, 
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        
        log.info("=== SOLICITUD A /login (GET) ===");
        log.info("Session ID: {}", request.getSession().getId());
        log.info("Request URI: {}", request.getRequestURI());
        log.info("Query String: {}", request.getQueryString());
        log.info("Headers - Referer: {}", request.getHeader("Referer"));
        log.info("Headers - User-Agent: {}", request.getHeader("User-Agent"));
        log.info("Accept: {}", request.getHeader("Accept"));
        log.info("Method: {}", request.getMethod());
        
        if (error != null) {
            log.error("❌ Error de autenticación en login");
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        
        if (logout != null) {
            log.info("✅ Logout exitoso");
            model.addAttribute("message", "Has cerrado sesión correctamente");
        }
        
        return "login";
    }
    
    @PostMapping("/login")
    public String processLogin(HttpServletRequest request,
                               @RequestParam("username") String username,
                               @RequestParam("password") String password) {
        
        log.info("=== SOLICITUD A /login (POST) ===");
        log.info("Username: {}", username);
        log.info("Password: [PROTECTED]");
        log.info("Session ID: {}", request.getSession().getId());
        
        // Spring Security manejará la autenticación automáticamente
        // Este método solo se ejecuta si la autenticación falla? No exactamente
        // Spring Security intercepta el POST antes de llegar aquí
        
        return "redirect:/";
    }
}