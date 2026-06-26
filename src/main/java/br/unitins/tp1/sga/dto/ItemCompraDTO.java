package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.ItemCompra;

import java.math.BigDecimal;
import java.util.Map;

public class ItemCompraDTO {
    public Long id;
    public Long modeloFacaId;
    public String modeloFacaNome;
    public BigDecimal precoBase;
    public BigDecimal custoPersonalizacoes;
    public BigDecimal precoTotal;
    public Map<String, String> personalizacoes;

    public static ItemCompraDTO fromEntity(ItemCompra item) {
        ItemCompraDTO dto = new ItemCompraDTO();
        dto.id = item.id;
        dto.modeloFacaId = item.getModeloFaca().id;
        dto.modeloFacaNome = item.getModeloFaca().getNome();
        dto.precoBase = item.getPrecoBase();
        dto.custoPersonalizacoes = item.getCustoPersonalizacoes();
        dto.precoTotal = item.getPrecoTotal();
        dto.personalizacoes = item.getPersonalizacoes();
        return dto;
    }
}
