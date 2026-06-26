package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.ModeloFaca;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ModeloFacaDTO {
    public Long id;
    public String nome;
    public String descricao;
    public BigDecimal precoBase;
    public boolean ativo;
    public LocalDateTime dataCriacao;
    public Long cuteleiroId;
    public String cuteleiroNome;
    public ComponenteResumoDTO laminaPadrao;
    public ComponenteResumoDTO caboPadrao;
    public List<ComponenteResumoDTO> guarnicoesPadrao;
    public List<ImagemModeloFacaDTO> imagens;
    public List<OpcaoPersonalizacaoDTO> opcoesPersonalizacao;
    public Double avaliacaoMedia;
    public Long totalAvaliacoes;

    public static ModeloFacaDTO resumo(ModeloFaca modelo) {
        ModeloFacaDTO dto = base(modelo);
        dto.imagens = modelo.getImagens().stream()
                .map(ImagemModeloFacaDTO::fromEntity)
                .toList();
        return dto;
    }

    public static ModeloFacaDTO detalhes(ModeloFaca modelo, Double avaliacaoMedia, Long totalAvaliacoes) {
        ModeloFacaDTO dto = base(modelo);
        dto.laminaPadrao = ComponenteResumoDTO.fromLamina(modelo.getLaminaPadrao());
        dto.caboPadrao = ComponenteResumoDTO.fromCabo(modelo.getCaboPadrao());
        dto.guarnicoesPadrao = modelo.getGuarnicoesPadrao().stream()
                .map(ComponenteResumoDTO::fromGuarnicao)
                .toList();
        dto.imagens = modelo.getImagens().stream()
                .map(ImagemModeloFacaDTO::fromEntity)
                .toList();
        dto.opcoesPersonalizacao = modelo.getOpcoesPersonalizacao().stream()
                .filter(opcao -> opcao.isAtivo())
                .map(OpcaoPersonalizacaoDTO::fromEntity)
                .toList();
        dto.avaliacaoMedia = avaliacaoMedia;
        dto.totalAvaliacoes = totalAvaliacoes;
        return dto;
    }

    private static ModeloFacaDTO base(ModeloFaca modelo) {
        ModeloFacaDTO dto = new ModeloFacaDTO();
        dto.id = modelo.id;
        dto.nome = modelo.getNome();
        dto.descricao = modelo.getDescricao();
        dto.precoBase = modelo.getPrecoBase();
        dto.ativo = modelo.isAtivo();
        dto.dataCriacao = modelo.getDataCriacao();
        if (modelo.getCriador() != null) {
            dto.cuteleiroId = modelo.getCriador().id;
            dto.cuteleiroNome = modelo.getCriador().getNome();
        }
        return dto;
    }
}
