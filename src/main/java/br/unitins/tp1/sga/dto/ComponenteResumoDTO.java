package br.unitins.tp1.sga.dto;

import br.unitins.tp1.sga.model.Cabo;
import br.unitins.tp1.sga.model.Guarnicao;
import br.unitins.tp1.sga.model.Lamina;

public class ComponenteResumoDTO {
    public Long id;
    public String descricao;
    public Double pesoGramas;

    /**
     * Construtor genérico — usado pelas fábricas fromLamina/fromCabo/fromGuarnicao
     * abaixo. Desde a Fase A (remoção de herança JOINED), Lamina/Cabo/Guarnicao
     * são entidades independentes (não estendem mais Componente), então não dá
     * mais para usar um único `fromEntity(Componente)` para os três.
     */
    public static ComponenteResumoDTO of(Long id, String descricao, Double pesoGramas) {
        ComponenteResumoDTO dto = new ComponenteResumoDTO();
        dto.id = id;
        dto.descricao = descricao;
        dto.pesoGramas = pesoGramas;
        return dto;
    }

    public static ComponenteResumoDTO fromLamina(Lamina lamina) {
        if (lamina == null) {
            return null;
        }
        return of(lamina.id, lamina.getDescricao(), lamina.getPesoGramas());
    }

    public static ComponenteResumoDTO fromCabo(Cabo cabo) {
        if (cabo == null) {
            return null;
        }
        return of(cabo.id, cabo.getDescricao(), cabo.getPesoGramas());
    }

    public static ComponenteResumoDTO fromGuarnicao(Guarnicao guarnicao) {
        if (guarnicao == null) {
            return null;
        }
        return of(guarnicao.id, guarnicao.getDescricao(), guarnicao.getPesoGramas());
    }
}
