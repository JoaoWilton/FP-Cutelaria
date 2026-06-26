package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.ImagemModeloFaca;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ImagemModeloFacaRepository implements PanacheRepository<ImagemModeloFaca> {

    public List<ImagemModeloFaca> findByModeloFacaId(Long modeloFacaId) {
        return find("modeloFaca.id = ?1 order by ordem", modeloFacaId).list();
    }
}
