package br.unitins.tp1.sga.filter;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Serviço de rate limiting em memória, baseado em janela fixa por IP.
 *
 * Implementação simples (sem dependências externas como bucket4j) adequada
 * para uma única instância da aplicação. Para ambientes distribuídos/produção,
 * substituir por bucket4j + Redis ou um rate limiter no API Gateway.
 */
@ApplicationScoped
public class RateLimitService {

    /**
     * Estrutura: chave = "endpoint:ip", valor = contador + início da janela.
     */
    private final Map<String, Janela> contadores = new ConcurrentHashMap<>();

    private static class Janela {
        AtomicInteger contagem = new AtomicInteger(0);
        volatile Instant inicio = Instant.now();
    }

    /**
     * Verifica se a requisição é permitida e incrementa o contador.
     *
     * @param chave        identificador único (ex: "login:192.168.0.1")
     * @param limite       número máximo de requisições permitidas na janela
     * @param janelaSegundos duração da janela em segundos
     * @return true se permitido, false se o limite foi excedido
     */
    public boolean permitir(String chave, int limite, int janelaSegundos) {
        Janela janela = contadores.computeIfAbsent(chave, k -> new Janela());

        synchronized (janela) {
            Instant agora = Instant.now();
            if (agora.isAfter(janela.inicio.plusSeconds(janelaSegundos))) {
                // Janela expirou — reinicia
                janela.inicio = agora;
                janela.contagem.set(0);
            }
            int novaContagem = janela.contagem.incrementAndGet();
            return novaContagem <= limite;
        }
    }

    /**
     * Limpa entradas antigas para evitar crescimento ilimitado do mapa.
     * Chamado periodicamente (ver RateLimitCleanupScheduler, se configurado).
     */
    public void limparAntigos(int janelaSegundos) {
        Instant limite = Instant.now().minusSeconds(janelaSegundos * 2L);
        contadores.entrySet().removeIf(e -> e.getValue().inicio.isBefore(limite));
    }
}
