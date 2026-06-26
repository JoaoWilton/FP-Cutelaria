package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.Cabo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CaboRepository implements PanacheRepository<Cabo> {

    public List<Cabo> findByDescricao(String descricao) {
        return find("UPPER(descricao) LIKE UPPER(?1)", "%" + descricao + "%").list();
    }

    public List<Cabo> findByMaterialId(Long materialId) {
        return find("material.id", materialId).list();
    }
}
