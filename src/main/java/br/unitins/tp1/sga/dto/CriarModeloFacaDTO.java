package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public class CriarModeloFacaDTO {
    @NotBlank(message = "Nome é obrigatório")
    public String nome;

    public String descricao;

    @NotNull(message = "Preço base é obrigatório")
    @Positive(message = "Preço base deve ser positivo")
    public BigDecimal precoBase;

    @NotNull(message = "Lâmina padrão é obrigatória")
    public Long laminaPadraoId;

    @NotNull(message = "Cabo padrão é obrigatório")
    public Long caboPadraoId;

    public List<Long> guarnicoesPadraoIds;

    /** Opcional — usado pelo Administrador para vincular a faca a um cuteleiro específico.
     * Se omitido e o chamador for Cuteleiro, usa o cuteleiro autenticado. */
    public Long cuteleiroId;
}
