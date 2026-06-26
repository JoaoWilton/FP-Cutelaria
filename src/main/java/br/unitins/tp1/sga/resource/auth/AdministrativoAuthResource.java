package br.unitins.tp1.sga.resource.auth;

import br.unitins.tp1.sga.resource.auth.AuthDTOs.LoginAdminRequest;
import br.unitins.tp1.sga.resource.auth.AuthDTOs.LoginResponse;
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

@Path("/administrativo/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdministrativoAuthResource {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    @RateLimit(limite = 5, janelaSegundos = 60)
    public Response loginAdmin(@Valid LoginAdminRequest req) {
        try {
            return Response.ok(
                    new LoginResponse(authService.loginAdministrativo(req.login, req.senhaAdministrativa))
            ).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"erro\": \"" + e.getMessage() + "\"}").build();
        }
    }
}
