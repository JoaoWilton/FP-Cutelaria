package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.OpcaoPersonalizacao;
import br.unitins.tp1.sga.model.TipoPersonalizacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class OpcaoPersonalizacaoRepository implements PanacheRepository<OpcaoPersonalizacao> {

    public List<OpcaoPersonalizacao> findAtivasByModeloFacaId(Long modeloFacaId) {
        return find("modeloFaca.id = ?1 and ativo = true order by tipo, opcao", modeloFacaId).list();
    }

    public OpcaoPersonalizacao findAtivaByModeloTipoOpcao(Long modeloFacaId, TipoPersonalizacao tipo, String opcao) {
        return find("modeloFaca.id = ?1 and tipo = ?2 and opcao = ?3 and ativo = true", modeloFacaId, tipo, opcao).firstResult();
    }
}
