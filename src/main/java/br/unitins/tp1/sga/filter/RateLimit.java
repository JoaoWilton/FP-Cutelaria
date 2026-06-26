package br.unitins.tp1.sga.filter;

import jakarta.ws.rs.NameBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para aplicar rate limiting a um endpoint específico.
 *
 * Exemplo:
 * <pre>
 * {@literal @POST}
 * {@literal @Path}("/login")
 * {@literal @RateLimit}(limite = 5, janelaSegundos = 60)
 * public Response login(...) { ... }
 * </pre>
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RateLimit {

    /** Número máximo de requisições permitidas dentro da janela. */
    int limite() default 10;

    /** Duração da janela em segundos. */
    int janelaSegundos() default 60;
}
