package br.unitins.tp1.sga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Entity
public class OpcaoPersonalizacao extends PanacheEntity {

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tipo de personalização é obrigatório")
    private TipoPersonalizacao tipo;

    @NotBlank(message = "Opção de personalização é obrigatória")
    private String opcao;

    @NotNull(message = "Custo adicional é obrigatório")
    @PositiveOrZero(message = "Custo adicional não pode ser negativo")
    private BigDecimal custoAdicional = BigDecimal.ZERO;

    private boolean ativo = true;

    @ManyToOne
    @NotNull(message = "Modelo de faca é obrigatório")
    private ModeloFaca modeloFaca;

    public TipoPersonalizacao getTipo() { return tipo; }
    public void setTipo(TipoPersonalizacao tipo) { this.tipo = tipo; }

    public String getOpcao() { return opcao; }
    public void setOpcao(String opcao) { this.opcao = opcao; }

    public BigDecimal getCustoAdicional() { return custoAdicional; }
    public void setCustoAdicional(BigDecimal custoAdicional) { this.custoAdicional = custoAdicional; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public ModeloFaca getModeloFaca() { return modeloFaca; }
    public void setModeloFaca(ModeloFaca modeloFaca) { this.modeloFaca = modeloFaca; }
}
