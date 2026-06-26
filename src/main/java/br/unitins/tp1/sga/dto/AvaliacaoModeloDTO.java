package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.AvaliacaoModelo;
import java.time.LocalDateTime;

public class AvaliacaoModeloDTO {
    public Long id;
    public Integer nota;
    public String comentario;
    public LocalDateTime dataAvaliacao;
    public boolean verificado;

    public static AvaliacaoModeloDTO fromEntity(AvaliacaoModelo avaliacao) {
        AvaliacaoModeloDTO dto = new AvaliacaoModeloDTO();
        dto.id = avaliacao.id;
        dto.nota = avaliacao.getNota();
        dto.comentario = avaliacao.getComentario();
        dto.dataAvaliacao = avaliacao.getDataAvaliacao();
        dto.verificado = avaliacao.isVerificado();
        return dto;
    }
}
