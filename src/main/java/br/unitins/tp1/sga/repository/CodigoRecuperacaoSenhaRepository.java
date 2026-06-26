package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.CodigoRecuperacaoSenha;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class CodigoRecuperacaoSenhaRepository implements PanacheRepository<CodigoRecuperacaoSenha> {

    /**
     * Busca o código mais recente (não usado) para um dado email + código.
     * Funciona para qualquer tipo de usuário via Usuario.email.
     */
    public Optional<CodigoRecuperacaoSenha> findByEmailAndCodigo(String email, String codigo) {
        return find("usuario.email = ?1 and codigo = ?2 and usado = false order by expiracao desc",
                email, codigo)
                .firstResultOptional();
    }
}
