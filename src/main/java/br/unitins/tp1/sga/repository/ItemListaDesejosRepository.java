package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.ItemListaDesejos;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ItemListaDesejosRepository implements PanacheRepository<ItemListaDesejos> {

    public List<ItemListaDesejos> findByClienteId(Long clienteId) {
        return find("cliente.id = ?1 order by dataAdicionado desc", clienteId).list();
    }
}
