package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.Compra;
import br.unitins.tp1.sga.model.StatusCompra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CompraDTO {
    public Long id;
    public String numeroSistema;
    public StatusCompra status;
    public BigDecimal precoTotal;
    public LocalDateTime dataCriacao;
    public LocalDateTime dataPagamento;
    public LocalDateTime dataEntrega;
    public String motivoCancelamento;
    public Long enderecoEntregaId;
    public List<ItemCompraDTO> itens;

    public static CompraDTO fromEntity(Compra compra) {
        CompraDTO dto = new CompraDTO();
        dto.id = compra.id;
        dto.numeroSistema = compra.getNumeroSistema();
        dto.status = compra.getStatus();
        dto.precoTotal = compra.getPrecoTotal();
        dto.dataCriacao = compra.getDataCriacao();
        dto.dataPagamento = compra.getDataPagamento();
        dto.dataEntrega = compra.getDataEntrega();
        dto.motivoCancelamento = compra.getMotivoCancelamento();
        dto.enderecoEntregaId = compra.getEnderecoEntrega() != null ? compra.getEnderecoEntrega().id : null;
        dto.itens = compra.getItens().stream()
                .map(ItemCompraDTO::fromEntity)
                .collect(Collectors.toList());
        return dto;
    }
}
