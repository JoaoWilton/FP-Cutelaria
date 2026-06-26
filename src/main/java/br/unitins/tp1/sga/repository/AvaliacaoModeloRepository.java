package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.AvaliacaoModelo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class AvaliacaoModeloRepository implements PanacheRepository<AvaliacaoModelo> {

    public List<AvaliacaoModelo> findByModeloFacaId(Long modeloFacaId) {
        return find("modeloFaca.id = ?1 order by dataAvaliacao desc", modeloFacaId).list();
    }

    public Double mediaByModeloFacaId(Long modeloFacaId) {
        return getEntityManager()
                .createQuery("select avg(a.nota) from AvaliacaoModelo a where a.modeloFaca.id = :modeloFacaId", Double.class)
                .setParameter("modeloFacaId", modeloFacaId)
                .getSingleResult();
    }
}
