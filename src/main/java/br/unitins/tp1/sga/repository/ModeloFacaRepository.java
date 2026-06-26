package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.ModeloFaca;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ModeloFacaRepository implements PanacheRepository<ModeloFaca> {

    public List<ModeloFaca> findAtivos() {
        return find("ativo", true).list();
    }

    public List<ModeloFaca> findByCuteleiroId(Long cuteleiroId) {
        return find("criador.id", cuteleiroId).list();
    }

    public List<ModeloFaca> findAtivosFiltrados(String nome, BigDecimal precoMin, BigDecimal precoMax, int page, int size) {
        return find(queryAtivos(nome, precoMin, precoMax), params(nome, precoMin, precoMax))
                .page(page, size)
                .list();
    }

    public long countAtivosFiltrados(String nome, BigDecimal precoMin, BigDecimal precoMax) {
        return count(queryAtivos(nome, precoMin, precoMax), params(nome, precoMin, precoMax));
    }

    private String queryAtivos(String nome, BigDecimal precoMin, BigDecimal precoMax) {
        StringBuilder query = new StringBuilder("ativo = true");
        if (nome != null && !nome.isBlank()) {
            query.append(" and upper(nome) like upper(:nome)");
        }
        if (precoMin != null) {
            query.append(" and precoBase >= :precoMin");
        }
        if (precoMax != null) {
            query.append(" and precoBase <= :precoMax");
        }
        query.append(" order by nome");
        return query.toString();
    }

    private Map<String, Object> params(String nome, BigDecimal precoMin, BigDecimal precoMax) {
        Map<String, Object> params = new HashMap<>();
        if (nome != null && !nome.isBlank()) {
            params.put("nome", "%" + nome + "%");
        }
        if (precoMin != null) {
            params.put("precoMin", precoMin);
        }
        if (precoMax != null) {
            params.put("precoMax", precoMax);
        }
        return params;
    }
}
