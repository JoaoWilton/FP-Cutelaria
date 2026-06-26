package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO de entrada para POST/PUT em /administrativo/v1/laminas.
 * Não expõe o campo "id" — gerado pelo banco (mesmo padrão de MaterialDTO).
 */
public class CriarLaminaDTO {

    @NotBlank(message = "Descrição é obrigatória")
    public String descricao;

    @NotNull(message = "Peso em gramas é obrigatório")
    @Positive(message = "Peso deve ser positivo")
    public Double pesoGramas;

    /** Id de um Material já cadastrado (opcional). */
    public Long materialId;

    @NotBlank(message = "Perfil de desbaste é obrigatório")
    public String perfilDesbaste; // "Plano", "Convexo", "Côncavo"

    @NotNull(message = "Espessura é obrigatória")
    @Positive(message = "Espessura deve ser positiva")
    public Double espessuraMm;
}
