package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.Lamina;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class LaminaRepository implements PanacheRepository<Lamina> {

    public List<Lamina> findByDescricao(String descricao) {
        return find("UPPER(descricao) LIKE UPPER(?1)", "%" + descricao + "%").list();
    }

    public List<Lamina> findByMaterialId(Long materialId) {
        return find("material.id", materialId).list();
    }
}
