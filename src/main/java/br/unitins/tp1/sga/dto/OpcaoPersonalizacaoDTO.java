package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.OpcaoPersonalizacao;
import br.unitins.tp1.sga.model.TipoPersonalizacao;
import java.math.BigDecimal;

public class OpcaoPersonalizacaoDTO {
    public Long id;
    public TipoPersonalizacao tipo;
    public String opcao;
    public BigDecimal custoAdicional;

    public static OpcaoPersonalizacaoDTO fromEntity(OpcaoPersonalizacao opcao) {
        OpcaoPersonalizacaoDTO dto = new OpcaoPersonalizacaoDTO();
        dto.id = opcao.id;
        dto.tipo = opcao.getTipo();
        dto.opcao = opcao.getOpcao();
        dto.custoAdicional = opcao.getCustoAdicional();
        return dto;
    }
}
