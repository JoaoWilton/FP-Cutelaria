package br.unitins.tp1.sga.repository;

import java.util.List;

import br.unitins.tp1.sga.model.Fornecedor;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FornecedorRepository implements PanacheRepository<Fornecedor> {

    public Fornecedor findByCnpj(String cnpj) {
        return find("cnpj", cnpj).firstResult();
    }

    public List<Fornecedor> findByNome(String nome) {
        return find("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%").list();
    }

    public Fornecedor findByLogin(String login) {
        return find("login", login).firstResult();
    }
}