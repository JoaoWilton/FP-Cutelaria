package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.NotaFiscal;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Repositório para operações CRUD da entidade NotaFiscal.
 */
@ApplicationScoped
public class NotaFiscalRepository implements PanacheRepository<NotaFiscal> {

    // Buscar nota fiscal por número
    public NotaFiscal findByNumero(String numero) {
        return find("numero", numero).firstResult();
    }

    // Buscar notas fiscais por valor mínimo
    public List<NotaFiscal> findByValorMinimo(Double valorMinimo) {
        return find("valor >= ?1", valorMinimo).list();
    }

    // Buscar notas fiscais por período (data início e fim)
    public List<NotaFiscal> findByPeriodo(String dataInicio, String dataFim) {
        return find("dataEmissao BETWEEN ?1 AND ?2", dataInicio, dataFim).list();
    }
}