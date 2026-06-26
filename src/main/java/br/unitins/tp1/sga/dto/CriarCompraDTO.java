package br.unitins.tp1.sga.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CriarCompraDTO {

    @NotEmpty(message = "Selecione ao menos um item da lista de desejos")
    public List<Long> itemListaDesejosIds;

    @NotNull(message = "enderecoEntregaId é obrigatório")
    public Long enderecoEntregaId;
}
