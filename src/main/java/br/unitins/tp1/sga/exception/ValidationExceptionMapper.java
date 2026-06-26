package br.unitins.tp1.sga.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converte falhas de Bean Validation (@Valid) em 400 com mensagem JSON legível.
 * Sem isso o Quarkus retorna 400 com body vazio.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {
        List<String> erros = e.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath().toString() + ": " + cv.getMessage())
                .collect(Collectors.toList());

        String body = "{\"erros\": [" +
                erros.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", ")) +
                "]}";

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(body)
                .build();
    }
}
