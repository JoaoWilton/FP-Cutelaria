package br.unitins.tp1.sga.filter;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Method;

/**
 * Filtro JAX-RS que aplica rate limiting nos endpoints anotados com {@code @RateLimit}.
 *
 * O identificador do "cliente" é o IP remoto (header X-Forwarded-For se presente,
 * caso contrário o IP da conexão). A contagem é por endpoint + IP.
 *
 * Quando o limite é excedido, retorna HTTP 429 (Too Many Requests).
 */
@Provider
@RateLimit
@Priority(Priorities.AUTHENTICATION - 100) // executa antes da autenticação
public class RateLimitFilter implements ContainerRequestFilter {

    @Inject
    RateLimitService rateLimitService;

    @Context
    ResourceInfo resourceInfo;

    @Context
    HttpHeaders headers;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        Method metodo = resourceInfo.getResourceMethod();
        RateLimit anotacao = metodo.getAnnotation(RateLimit.class);
        if (anotacao == null) {
            anotacao = resourceInfo.getResourceClass().getAnnotation(RateLimit.class);
        }
        if (anotacao == null) {
            return; // sem anotação, sem limite
        }

        String ip = obterIpCliente();
        String chave = requestContext.getUriInfo().getPath() + ":" + ip;

        boolean permitido = rateLimitService.permitir(chave, anotacao.limite(), anotacao.janelaSegundos());

        if (!permitido) {
            requestContext.abortWith(
                    Response.status(429)
                            .entity("{\"erro\": \"Muitas requisições. Tente novamente em alguns instantes.\"}")
                            .header("Retry-After", String.valueOf(anotacao.janelaSegundos()))
                            .build()
            );
        }
    }

    private String obterIpCliente() {
        String forwarded = headers.getHeaderString("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            // Pode conter múltiplos IPs separados por vírgula — usa o primeiro
            return forwarded.split(",")[0].trim();
        }
        // Fallback: sem acesso direto ao IP de conexão neste contexto;
        // usar um identificador genérico baseado em User-Agent + Accept-Language
        // como aproximação para ambientes de desenvolvimento sem proxy.
        String userAgent = headers.getHeaderString("User-Agent");
        return "local:" + (userAgent != null ? userAgent.hashCode() : "unknown");
    }
}
