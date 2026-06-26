package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para POST /administrativo/v1/administradores.
 * Define as duas senhas: `senha` (login e-commerce, raramente usado por
 * admins) e `senhaAdministrativa` (usada em /administrativo/v1/auth/login).
 */
public class CadastroAdministradorDTO {

    @NotBlank(message = "Nome é obrigatório")
    public String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    public String email;

    public String telefone;

    @NotBlank(message = "Login é obrigatório")
    public String login;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    public String senha;

    @NotBlank(message = "Senha administrativa é obrigatória")
    @Size(min = 6, message = "Senha administrativa deve ter no mínimo 6 caracteres")
    public String senhaAdministrativa;
}
