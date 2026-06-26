package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class MaterialDTO {

    @NotBlank(message = "Nome é obrigatório")
    public String nome;

    public String tipoMaterial;

    @NotNull(message = "Custo unitário é obrigatório")
    @Positive(message = "Custo unitário deve ser positivo")
    public BigDecimal custoUnitario;
}
