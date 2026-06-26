package br.unitins.tp1.sga.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Converte erros de parse de JSON (campo com tipo errado, JSON malformado)
 * em 400 com mensagem legível em vez de 400 vazio.
 */
@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    @Override
    public Response toResponse(JsonProcessingException e) {
        String msg = e.getOriginalMessage() != null ? e.getOriginalMessage() : "JSON inválido";
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"erro\": \"Erro ao interpretar JSON: " + msg.replace("\"", "'") + "\"}")
                .build();
    }
}
