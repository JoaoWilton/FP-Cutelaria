package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.Guarnicao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GuarnicaoRepository implements PanacheRepository<Guarnicao> {

    public List<Guarnicao> findByDescricao(String descricao) {
        return find("UPPER(descricao) LIKE UPPER(?1)", "%" + descricao + "%").list();
    }

    public List<Guarnicao> findByMaterialId(Long materialId) {
        return find("material.id", materialId).list();
    }
}
