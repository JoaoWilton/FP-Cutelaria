package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.Cuteleiro;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CuteleiroRepository implements PanacheRepository<Cuteleiro> {

    public List<Cuteleiro> findByEspecialidade(String especialidade) {
        return find("especialidade", especialidade).list();
    }

    public List<Cuteleiro> findByNome(String nome) {
        return find("UPPER(nome) LIKE UPPER(?1)", "%" + nome + "%").list();
    }

    public Cuteleiro findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public Cuteleiro findByLogin(String login) {
        return find("login", login).firstResult();
    }
}