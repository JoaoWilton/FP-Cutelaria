package br.unitins.tp1.sga.service;

import br.unitins.tp1.sga.model.Administrador;
import br.unitins.tp1.sga.model.Cliente;
import br.unitins.tp1.sga.model.CodigoRecuperacaoSenha;
import br.unitins.tp1.sga.model.Cuteleiro;
import br.unitins.tp1.sga.model.Usuario;
import br.unitins.tp1.sga.repository.CodigoRecuperacaoSenhaRepository;
import br.unitins.tp1.sga.repository.UsuarioRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class AuthService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    EmailService emailService;

    @Inject
    CodigoRecuperacaoSenhaRepository codigoRepository;

    // -------------------------------------------------------------------------
    // Login e-commerce (gera token com role do usuário: Cliente/Cuteleiro/Administrador)
    // -------------------------------------------------------------------------

    public String login(String login, String senha) {
        Usuario usuario = usuarioRepository.findByLogin(login);

        if (usuario == null || !BcryptUtil.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("Login ou senha inválidos");
        }

        Set<String> roles = resolverRolesEcommerce(usuario);

        return Jwt.issuer("https://localhost:8080")
                .upn(usuario.getLogin())
                .groups(roles)
                .expiresIn(3600)
                .sign();
    }

    // -------------------------------------------------------------------------
    // Login administrativo (segunda senha separada → gera token com role Administrador)
    // -------------------------------------------------------------------------

    public String loginAdministrativo(String login, String senhaAdministrativa) {
        Usuario usuario = usuarioRepository.findByLogin(login);

        if (usuario == null) {
            throw new RuntimeException("Login ou senha administrativa inválidos");
        }

        boolean podeSerAdmin = usuario instanceof Administrador;

        if (!podeSerAdmin) {
            throw new RuntimeException("Usuário não possui perfil administrativo");
        }

        if (usuario.getSenhaAdministrativa() == null
                || !BcryptUtil.matches(senhaAdministrativa, usuario.getSenhaAdministrativa())) {
            throw new RuntimeException("Login ou senha administrativa inválidos");
        }

        // Token administrativo: garante role Administrador independente do tipo
        Set<String> roles = new HashSet<>();
        roles.add("Administrador");

        return Jwt.issuer("https://localhost:8080")
                .upn(usuario.getLogin())
                .groups(roles)
                .claim("admin", true)
                .expiresIn(3600)
                .sign();
    }

    // -------------------------------------------------------------------------
    // Recuperação de senha
    // -------------------------------------------------------------------------

    /**
     * Gera código de recuperação de senha para qualquer tipo de usuário (email-only).
     * Em produção o código é enviado por e-mail; em dev/test também é retornado para o chamador.
     */
    @Transactional
    public String gerarCodigoRecuperacao(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            // Mensagem genérica para não revelar se o e-mail existe
            throw new RuntimeException("Dados não conferem com nenhum cadastro");
        }

        // Invalida códigos anteriores não usados
        codigoRepository.find("usuario.id = ?1 and usado = false", usuario.id)
                .list()
                .forEach(c -> {
                    c.setUsado(true);
                    codigoRepository.persist(c);
                });

        String codigo = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        CodigoRecuperacaoSenha entidade = new CodigoRecuperacaoSenha(usuario, codigo);
        codigoRepository.persist(entidade);

        emailService.enviarCodigoRecuperacaoSenha(usuario.getEmail(), usuario.getNome(), codigo);

        return codigo; // retornado também para fins de log/teste; o e-mail já foi disparado
    }

    /**
     * Valida o código e, se correto e não expirado, redefine a senha do usuário.
     */
    @Transactional
    public void redefinirSenha(String email, String codigo, String novaSenha) {
        CodigoRecuperacaoSenha entidade = codigoRepository
                .findByEmailAndCodigo(email, codigo)
                .orElseThrow(() -> new RuntimeException("Código inválido ou expirado"));

        if (!entidade.isValido()) {
            throw new RuntimeException("Código inválido ou expirado");
        }

        entidade.getUsuario().setSenha(hashSenha(novaSenha));
        entidade.setUsado(true);
        codigoRepository.persist(entidade);
    }

    // -------------------------------------------------------------------------
    // Utilitários
    // -------------------------------------------------------------------------

    public String hashSenha(String senhaTextoPuro) {
        return BcryptUtil.bcryptHash(senhaTextoPuro);
    }

    private Set<String> resolverRolesEcommerce(Usuario usuario) {
        Set<String> roles = new HashSet<>();
        if (usuario instanceof Administrador) {
            roles.add("Administrador");
        } else if (usuario instanceof Cliente) {
            roles.add("Cliente");
        } else if (usuario instanceof Cuteleiro) {
            roles.add("Cuteleiro");
        }

        return roles;
    }
}
