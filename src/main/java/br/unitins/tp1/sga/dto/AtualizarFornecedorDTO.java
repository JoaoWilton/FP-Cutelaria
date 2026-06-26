package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/** DTO de entrada para PUT /administrativo/v1/fornecedores/{id}. Senha não é alterada aqui. */
public class AtualizarFornecedorDTO {

    @NotBlank(message = "Nome é obrigatório")
    public String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    public String email;

    public String telefone;

    @NotBlank(message = "Login é obrigatório")
    public String login;

    @NotNull(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos numéricos")
    public String cnpj;

    public String contato;
}
