package br.unitins.tp1.sga.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Compra extends PanacheEntity {

    @Column(unique = true)
    private String numeroSistema;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status da compra é obrigatório")
    private StatusCompra status = StatusCompra.AGUARDANDO_PAGAMENTO;

    @NotNull(message = "Preço total é obrigatório")
    @PositiveOrZero(message = "Preço total não pode ser negativo")
    private BigDecimal precoTotal = BigDecimal.ZERO;

    @NotNull(message = "Data de criação é obrigatória")
    private LocalDateTime dataCriacao;

    private LocalDateTime dataPagamento;
    private LocalDateTime dataEntrega;
    private String motivoCancelamento;

    @ManyToOne
    @NotNull(message = "Cliente é obrigatório")
    private Cliente cliente;

    @ManyToOne
    private EnderecoCliente enderecoEntrega;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCompra> itens = new ArrayList<>();

    public Compra() {
        this.dataCriacao = LocalDateTime.now();
    }

    public BigDecimal recalcularTotal() {
        this.precoTotal = itens.stream()
                .map(ItemCompra::getPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this.precoTotal;
    }

    public String getNumeroSistema() { return numeroSistema; }
    public void setNumeroSistema(String numeroSistema) { this.numeroSistema = numeroSistema; }

    public StatusCompra getStatus() { return status; }
    public void setStatus(StatusCompra status) { this.status = status; }

    public BigDecimal getPrecoTotal() { return precoTotal; }
    public void setPrecoTotal(BigDecimal precoTotal) { this.precoTotal = precoTotal; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }

    public LocalDateTime getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(LocalDateTime dataEntrega) { this.dataEntrega = dataEntrega; }

    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(String motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public EnderecoCliente getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(EnderecoCliente enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }

    public List<ItemCompra> getItens() { return itens; }
    public void setItens(List<ItemCompra> itens) { this.itens = itens; }
}
