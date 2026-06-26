package br.unitins.tp1.sga.dto;

import java.util.Map;

/**
 * DTO de entrada para POST /ecommerce/v1/lista-desejos/{modeloId}.
 * `personalizacoes` é opcional — se omitido/vazio, o item entra com as
 * configurações padrão do modelo (sem custo adicional).
 *
 * Formato: chave = TipoPersonalizacao.name(),
 * valor = opção escolhida (deve corresponder a uma OpcaoPersonalizacao ativa
 * do modelo, senão a requisição é rejeitada com 400).
 */
public class AdicionarListaDesejosDTO {
    public Map<String, String> personalizacoes;
}
