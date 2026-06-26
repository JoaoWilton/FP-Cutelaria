package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** DTO de entrada para POST /administrativo/v1/fornecedores (criação pelo Administrador). */
public class CadastroFornecedorDTO {

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

    @NotNull(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos numéricos")
    public String cnpj;

    public String contato;
}
