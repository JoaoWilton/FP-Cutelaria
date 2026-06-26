package br.unitins.tp1.sga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Linha de uma Compra: um ModeloFaca com suas personalizações e preços calculados
 * no momento do checkout.
 *
 * Linha de uma Compra. Os campos de cabeçalho (status, datas, cliente,
 * enderecoEntrega) ficam em Compra.java.
 */
@Entity
public class ItemCompra extends PanacheEntity {

    @NotNull(message = "Preço base é obrigatório")
    @PositiveOrZero(message = "Preço base não pode ser negativo")
    private BigDecimal precoBase = BigDecimal.ZERO;

    @NotNull(message = "Custo das personalizações é obrigatório")
    @PositiveOrZero(message = "Custo das personalizações não pode ser negativo")
    private BigDecimal custoPersonalizacoes = BigDecimal.ZERO;

    @NotNull(message = "Preço total é obrigatório")
    @PositiveOrZero(message = "Preço total não pode ser negativo")
    private BigDecimal precoTotal = BigDecimal.ZERO;

    @ElementCollection
    @CollectionTable(name = "item_compra_personalizacao", joinColumns = @JoinColumn(name = "item_compra_id"))
    @MapKeyColumn(name = "tipo")
    @Column(name = "opcao")
    private Map<String, String> personalizacoes = new HashMap<>();

    @ManyToOne
    @NotNull(message = "Modelo de faca é obrigatório")
    private ModeloFaca modeloFaca;

    @ManyToOne
    @NotNull(message = "Compra é obrigatória")
    private Compra compra;

    public ItemCompra() {}

    public BigDecimal calcularTotal() {
        BigDecimal base = precoBase != null ? precoBase : BigDecimal.ZERO;
        BigDecimal personalizacao = custoPersonalizacoes != null ? custoPersonalizacoes : BigDecimal.ZERO;
        this.precoTotal = base.add(personalizacao);
        return this.precoTotal;
    }

    public BigDecimal getPrecoBase() { return precoBase; }
    public void setPrecoBase(BigDecimal precoBase) { this.precoBase = precoBase; }

    public BigDecimal getCustoPersonalizacoes() { return custoPersonalizacoes; }
    public void setCustoPersonalizacoes(BigDecimal custoPersonalizacoes) { this.custoPersonalizacoes = custoPersonalizacoes; }

    public BigDecimal getPrecoTotal() { return precoTotal; }
    public void setPrecoTotal(BigDecimal precoTotal) { this.precoTotal = precoTotal; }

    public Map<String, String> getPersonalizacoes() { return personalizacoes; }
    public void setPersonalizacoes(Map<String, String> personalizacoes) { this.personalizacoes = personalizacoes; }

    public ModeloFaca getModeloFaca() { return modeloFaca; }
    public void setModeloFaca(ModeloFaca modeloFaca) { this.modeloFaca = modeloFaca; }

    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }
}
