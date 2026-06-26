package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.Administrador;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AdministradorRepository implements PanacheRepository<Administrador> {

    public Administrador findByLogin(String login) {
        return find("login", login).firstResult();
    }
}
