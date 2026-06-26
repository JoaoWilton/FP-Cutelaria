package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.TipoPersonalizacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class CriarOpcaoPersonalizacaoDTO {
    @NotNull(message = "Tipo de personalização é obrigatório")
    public TipoPersonalizacao tipo;

    @NotBlank(message = "Opção é obrigatória")
    public String opcao;

    @NotNull(message = "Custo adicional é obrigatório")
    @PositiveOrZero(message = "Custo adicional não pode ser negativo")
    public BigDecimal custoAdicional;
}
