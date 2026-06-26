package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para criação de avaliação de um modelo de faca.
 */
public class CriarAvaliacaoDTO {

    @NotNull(message = "Nota é obrigatória")
    @Min(value = 1, message = "Nota mínima é 1")
    @Max(value = 5, message = "Nota máxima é 5")
    public Integer nota;

    @Size(max = 1000, message = "Comentário deve ter no máximo 1000 caracteres")
    public String comentario;
}
