package com.smartlogix.bff.service;

import com.smartlogix.bff.model.Usuario;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public Usuario login(String user, String pass) {

        if (user.equals("admin") && pass.equals("1234")) {
            return new Usuario("admin", "1234", "ADMIN");
        }

        if (user.equals("cliente") && pass.equals("1234")) {
            return new Usuario("cliente", "1234", "CLIENTE");
        }

        return null;
    }
}