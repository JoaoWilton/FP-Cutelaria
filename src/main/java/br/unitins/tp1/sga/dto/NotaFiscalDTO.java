package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class NotaFiscalDTO {

    @NotBlank(message = "Número da nota é obrigatório")
    public String numero;

    public LocalDate dataEmissao;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    public BigDecimal valor;
}
