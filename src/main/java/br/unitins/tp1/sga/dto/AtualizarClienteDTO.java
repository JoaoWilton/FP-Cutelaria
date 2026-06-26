package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para PUT /clientes/{id} — edição de dados básicos.
 * CPF/dataNascimento mudam via PATCH /completar-cadastro; senha via PATCH /senha.
 */
public class AtualizarClienteDTO {

    @NotBlank(message = "Nome é obrigatório")
    public String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    public String email;

    public String telefone;
}
