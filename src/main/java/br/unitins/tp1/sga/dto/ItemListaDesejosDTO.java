package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.ItemListaDesejos;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public class ItemListaDesejosDTO {
    public Long id;
    public LocalDateTime dataAdicionado;
    public ModeloFacaDTO modeloFaca;
    public Map<String, String> personalizacoes;
    public BigDecimal precoCalculado;

    public static ItemListaDesejosDTO fromEntity(ItemListaDesejos item) {
        ItemListaDesejosDTO dto = new ItemListaDesejosDTO();
        dto.id = item.id;
        dto.dataAdicionado = item.getDataAdicionado();
        dto.modeloFaca = ModeloFacaDTO.resumo(item.getModeloFaca());
        dto.personalizacoes = item.getPersonalizacoes();
        dto.precoCalculado = item.getPrecoCalculado();
        return dto;
    }
}
