package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.Cliente;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ClienteRepository implements PanacheRepository<Cliente> {

    public Cliente findByCpf(String cpf) {
        return find("cpf", cpf).firstResult();
    }

    public List<Cliente> findByNome(String nome) {
        return find("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%").list();
    }

    public Cliente findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public Cliente findByLogin(String login) {
        return find("login", login).firstResult();
    }
}