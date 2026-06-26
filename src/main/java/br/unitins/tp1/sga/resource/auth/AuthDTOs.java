package br.unitins.tp1.sga.resource.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDTOs {

    public static class LoginRequest {
        @NotBlank public String login;
        @NotBlank public String senha;
    }

    public static class LoginAdminRequest {
        @NotBlank public String login;
        @NotBlank public String senhaAdministrativa;
    }

    public static class LoginResponse {
        public String token;
        public LoginResponse(String token) { this.token = token; }
    }

    public static class RecuperarSenhaRequest {
        @NotBlank @Email public String email;
    }

    public static class RedefinirSenhaRequest {
        @NotBlank @Email public String email;
        @NotBlank @Size(min = 6, max = 6) public String codigo;
        @NotBlank @Size(min = 6) public String novaSenha;
    }
}
