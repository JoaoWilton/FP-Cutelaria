package br.unitins.tp1.sga.resource;

import br.unitins.tp1.sga.resource.auth.AuthDTOs.LoginRequest;
import br.unitins.tp1.sga.resource.auth.AuthDTOs.LoginResponse;
import br.unitins.tp1.sga.resource.auth.AuthDTOs.RecuperarSenhaRequest;
import br.unitins.tp1.sga.resource.auth.AuthDTOs.RedefinirSenhaRequest;
import br.unitins.tp1.sga.filter.RateLimit;
import br.unitins.tp1.sga.service.AuthService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Auth legado (/auth/login) mantido para compatibilidade,
 * mais os endpoints públicos de recuperação de senha.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    AuthService authService;

    @ConfigProperty(name = "quarkus.profile", defaultValue = "prod")
    String perfilAtivo;

    /** Login legado — equivalente a POST /ecommerce/v1/auth/login. */
    @POST
    @Path("/login")
    @RateLimit(limite = 5, janelaSegundos = 60)
    public Response login(@Valid LoginRequest req) {
        try {
            return Response.ok(new LoginResponse(authService.login(req.login, req.senha))).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"erro\": \"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Passo 1 — recuperação de senha:
     * Valida o e-mail e gera código de 6 dígitos (15 min) para qualquer tipo de usuário.
     * Envia por e-mail via EmailService (mock em dev/test, SMTP real em produção).
     * Em dev/test, o código também é retornado na resposta para facilitar testes.
     */
    @POST
    @Path("/recuperar-senha")
    @RateLimit(limite = 3, janelaSegundos = 300)
    public Response recuperarSenha(@Valid RecuperarSenhaRequest req) {
        try {
            String codigo = authService.gerarCodigoRecuperacao(req.email);
            if (!"prod".equals(perfilAtivo)) {
                return Response.ok("{\"mensagem\": \"Código enviado para o e-mail cadastrado\", \"codigo\": \"" + codigo + "\"}").build();
            }
            return Response.ok("{\"mensagem\": \"Código enviado para o e-mail cadastrado\"}").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\": \"" + e.getMessage() + "\"}").build();
        }
    }

    /**
     * Passo 2 — redefinição de senha:
     * Valida o código e salva a nova senha com hash bcrypt.
     */
    @POST
    @Path("/redefinir-senha")
    public Response redefinirSenha(@Valid RedefinirSenhaRequest req) {
        try {
            authService.redefinirSenha(req.email, req.codigo, req.novaSenha);
            return Response.ok("{\"mensagem\": \"Senha redefinida com sucesso\"}").build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\": \"" + e.getMessage() + "\"}").build();
        }
    }
}
