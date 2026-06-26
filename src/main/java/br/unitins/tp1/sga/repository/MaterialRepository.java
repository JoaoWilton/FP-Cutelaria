package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.Material;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class MaterialRepository implements PanacheRepository<Material> {

    public List<Material> findByNome(String nome) {
        return find("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%").list();
    }

    public List<Material> findByTipoMaterial(String tipoMaterial) {
        return find("UPPER(tipoMaterial) LIKE UPPER(?1)", "%" + tipoMaterial + "%").list();
    }

    public List<Material> findByCustoMenorQue(Double valor) {
        return find("custoUnitario < ?1", valor).list();
    }

    public List<Material> findByCustoMaiorQue(Double valor) {
        return find("custoUnitario > ?1", valor).list();
    }
}