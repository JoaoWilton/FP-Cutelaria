package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.Compra;
import br.unitins.tp1.sga.model.StatusCompra;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CompraRepository implements PanacheRepository<Compra> {

    public List<Compra> findByClienteId(Long clienteId) {
        return find("cliente.id = ?1 order by dataCriacao desc", clienteId).list();
    }

    public Optional<Compra> findByNumeroSistema(String numeroSistema) {
        return find("numeroSistema = ?1", numeroSistema).firstResultOptional();
    }

    public List<Compra> findByStatus(StatusCompra status) {
        return find("status = ?1 order by dataCriacao desc", status).list();
    }
}
