package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO de entrada para POST /clientes — cadastro simples.
 * CPF e dataNascimento são opcionais: se ausentes, cadastroCompleto = false
 * (ver ClienteResource.inserir e PATCH /clientes/{id}/completar-cadastro).
 */
public class CadastroClienteDTO {

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

    // Opcionais — se ambos vierem preenchidos, cadastroCompleto = true
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
    public String cpf;

    public LocalDate dataNascimento;
}
