package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

/** DTO de entrada para PUT /administrativo/v1/cuteleiros/{id}. Senha não é alterada aqui. */
public class AtualizarCuteleiroDTO {

    @NotBlank(message = "Nome é obrigatório")
    public String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    public String email;

    public String telefone;

    @NotBlank(message = "Login é obrigatório")
    public String login;

    public String especialidade;

    @NotNull(message = "Data de contratação é obrigatória")
    @PastOrPresent(message = "Data de contratação não pode ser futura")
    public LocalDate dataContratacao;
}
