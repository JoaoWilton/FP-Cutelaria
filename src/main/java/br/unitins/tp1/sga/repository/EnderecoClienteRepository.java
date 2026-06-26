package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.EnderecoCliente;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class EnderecoClienteRepository implements PanacheRepository<EnderecoCliente> {

    public List<EnderecoCliente> findAtivosByClienteId(Long clienteId) {
        return find("cliente.id = ?1 and ativo = true order by principal desc, id", clienteId).list();
    }
}
