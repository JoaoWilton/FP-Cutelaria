package br.unitins.tp1.sga.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public class CompletarCadastroDTO {

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
    public String cpf;

    @NotNull(message = "Data de nascimento é obrigatória")
    public LocalDate dataNascimento;

    public String telefone;

    // Endereço principal (opcional, mas recomendado)
    @Valid
    public EnderecoClienteCreateDTO endereco;
}
