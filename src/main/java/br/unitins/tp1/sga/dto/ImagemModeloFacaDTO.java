package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.ImagemModeloFaca;

public class ImagemModeloFacaDTO {
    public Long id;
    public String url;
    public String descricao;
    public Integer ordem;

    public static ImagemModeloFacaDTO fromEntity(ImagemModeloFaca imagem) {
        ImagemModeloFacaDTO dto = new ImagemModeloFacaDTO();
        dto.id = imagem.id;
        dto.url = imagem.getUrl();
        dto.descricao = imagem.getDescricao();
        dto.ordem = imagem.getOrdem();
        return dto;
    }
}
