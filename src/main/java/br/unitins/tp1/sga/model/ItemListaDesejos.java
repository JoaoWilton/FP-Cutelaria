package br.unitins.tp1.sga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Item da lista de desejos de um cliente.
 *
 * Não há mais constraint de unicidade (cliente_id, modelo_faca_id) — o mesmo
 * modelo pode aparecer várias vezes na lista com personalizações diferentes
 * (mesmo padrão de "salvar configurações de produto" usado em e-commerces
 * de produtos personalizáveis).
 */
@Entity
public class ItemListaDesejos extends PanacheEntity {

    @NotNull(message = "Data de adição é obrigatória")
    private LocalDateTime dataAdicionado;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @NotNull(message = "Cliente é obrigatório")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "modelo_faca_id")
    @NotNull(message = "Modelo de faca é obrigatório")
    private ModeloFaca modeloFaca;

    /**
     * Personalizações escolhidas para este item, no mesmo formato usado em
     * Compra: chave = TipoPersonalizacao.name(), valor = opção escolhida.
     */
    @ElementCollection
    @CollectionTable(name = "item_lista_desejos_personalizacao",
            joinColumns = @JoinColumn(name = "item_lista_desejos_id"))
    @MapKeyColumn(name = "tipo")
    @Column(name = "opcao")
    private Map<String, String> personalizacoes = new HashMap<>();

    /**
     * Preço calculado no momento em que o item foi adicionado/atualizado
     * (precoBase do modelo + custos das personalizações escolhidas).
     * Recalculado no checkout (Compra nunca confia neste valor).
     */
    @NotNull
    @PositiveOrZero
    private BigDecimal precoCalculado = BigDecimal.ZERO;

    public ItemListaDesejos() {
        this.dataAdicionado = LocalDateTime.now();
    }

    public LocalDateTime getDataAdicionado() { return dataAdicionado; }
    public void setDataAdicionado(LocalDateTime dataAdicionado) { this.dataAdicionado = dataAdicionado; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public ModeloFaca getModeloFaca() { return modeloFaca; }
    public void setModeloFaca(ModeloFaca modeloFaca) { this.modeloFaca = modeloFaca; }

    public Map<String, String> getPersonalizacoes() { return personalizacoes; }
    public void setPersonalizacoes(Map<String, String> personalizacoes) { this.personalizacoes = personalizacoes; }

    public BigDecimal getPrecoCalculado() { return precoCalculado; }
    public void setPrecoCalculado(BigDecimal precoCalculado) { this.precoCalculado = precoCalculado; }
}
