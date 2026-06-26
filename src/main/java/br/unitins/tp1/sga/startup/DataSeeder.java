package br.unitins.tp1.sga.startup;

import br.unitins.tp1.sga.model.Administrador;
import br.unitins.tp1.sga.model.AvaliacaoModelo;
import br.unitins.tp1.sga.model.Cabo;
import br.unitins.tp1.sga.model.Cliente;
import br.unitins.tp1.sga.model.Compra;
import br.unitins.tp1.sga.model.Cuteleiro;
import br.unitins.tp1.sga.model.EnderecoCliente;
import br.unitins.tp1.sga.model.ItemCompra;
import br.unitins.tp1.sga.model.ItemListaDesejos;
import br.unitins.tp1.sga.model.Lamina;
import br.unitins.tp1.sga.model.Material;
import br.unitins.tp1.sga.model.ModeloFaca;
import br.unitins.tp1.sga.model.OpcaoPersonalizacao;
import br.unitins.tp1.sga.model.StatusCompra;
import br.unitins.tp1.sga.model.TipoPersonalizacao;
import br.unitins.tp1.sga.model.TipoUsuario;
import br.unitins.tp1.sga.repository.UsuarioRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Popula o banco com dados de teste no startup.
 *
 * Padrão de usuários de teste (todos com senha: 123456):
 *  - cliente   / teste_cliente   / teste@cliente.com    → Cliente (cadastro completo)
 *  - cuteleiro / teste_cuteleiro / teste@cuteleiro.com  → Cuteleiro
 *  - admin     / teste_admin     / teste@admin.com      → Administrador (senhaAdm: teste123)
 *
 * Catálogo:
 *  - Faca Bowie Artesanal  — criada pelo cuteleiro de teste
 *  - Faca de Caça Huntsman — criada pelo cuteleiro de teste
 *
 * Histórico de demonstração (5.B.9 — metade 1):
 *  - 1 Compra já paga (PAGAMENTO_CONFIRMADO) da Bowie com GRAVURA_LAMINA
 *  - 1 ItemListaDesejos da Huntsman com GRAVURA_LAMINA (sem personalização paga)
 */
@ApplicationScoped
public class DataSeeder {

    private static final Logger LOG = Logger.getLogger(DataSeeder.class);

    @Inject
    UsuarioRepository usuarioRepository;

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        seedUsuarios();
        seedCatalogo();
        seedHistorico();
    }

    // -------------------------------------------------------------------------
    // Usuários
    // -------------------------------------------------------------------------

    private void seedUsuarios() {

        // --- Cliente ---
        if (usuarioRepository.findByLogin("cliente") == null) {
            Cliente cliente = new Cliente();
            cliente.setNome("teste_cliente");
            cliente.setEmail("teste@cliente.com");
            cliente.setTelefone("63999990001");
            cliente.setLogin("cliente");
            cliente.setSenha(BcryptUtil.bcryptHash("123456"));
            cliente.setTipoUsuario(TipoUsuario.CLIENTE);
            cliente.setCpf("12345678901");
            cliente.setDataNascimento(LocalDate.of(1995, 5, 20));
            cliente.setCadastroCompleto(true);
            cliente.persist();

            EnderecoCliente endereco = new EnderecoCliente();
            endereco.setCep("77000000");
            endereco.setRua("Quadra 103 Sul");
            endereco.setNumero("10");
            endereco.setBairro("Plano Diretor Sul");
            endereco.setCidade("Palmas");
            endereco.setEstado("TO");
            endereco.setAtivo(true);
            endereco.setPrincipal(true);
            endereco.setCliente(cliente);
            endereco.persist();

            LOG.info("[DataSeeder] Cliente 'cliente' criado (senha: 123456)");
        }

        // --- Cuteleiro ---
        if (usuarioRepository.findByLogin("cuteleiro") == null) {
            Cuteleiro cuteleiro = new Cuteleiro();
            cuteleiro.setNome("teste_cuteleiro");
            cuteleiro.setEmail("teste@cuteleiro.com");
            cuteleiro.setTelefone("63988887777");
            cuteleiro.setLogin("cuteleiro");
            cuteleiro.setSenha(BcryptUtil.bcryptHash("123456"));
            cuteleiro.setTipoUsuario(TipoUsuario.CUTELEIRO);
            cuteleiro.setEspecialidade("Facas artesanais forjadas");
            cuteleiro.setDataContratacao(LocalDate.of(2024, 1, 15));
            cuteleiro.persist();

            LOG.info("[DataSeeder] Cuteleiro 'cuteleiro' criado (senha: 123456)");
        }

        // --- Administrador ---
        if (usuarioRepository.findByLogin("admin") == null) {
            Administrador admin = new Administrador();
            admin.setNome("teste_admin");
            admin.setEmail("teste@admin.com");
            admin.setTelefone("63999990002");
            admin.setLogin("admin");
            admin.setSenha(BcryptUtil.bcryptHash("123456"));
            admin.setSenhaAdministrativa(BcryptUtil.bcryptHash("teste123"));
            admin.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            admin.persist();

            LOG.info("[DataSeeder] Admin 'admin' criado (senha: 123456 / senhaAdm: teste123)");
        }
    }

    // -------------------------------------------------------------------------
    // Catálogo
    // -------------------------------------------------------------------------

    private void seedCatalogo() {
        if (ModeloFaca.count() > 0) {
            return;
        }

        Cuteleiro cuteleiro = (Cuteleiro) usuarioRepository.findByLogin("cuteleiro");

        // --- Materiais ---
        Material acoCarbono = new Material();
        acoCarbono.setNome("Aço Carbono 1095");
        acoCarbono.setTipoMaterial("Aço Carbono");
        acoCarbono.setCustoUnitario(new BigDecimal("80.00"));
        acoCarbono.persist();

        Material jacaranda = new Material();
        jacaranda.setNome("Madeira de Jacarandá");
        jacaranda.setTipoMaterial("Madeira Nobre");
        jacaranda.setCustoUnitario(new BigDecimal("60.00"));
        jacaranda.persist();

        Material nogueira = new Material();
        nogueira.setNome("Madeira de Nogueira");
        nogueira.setTipoMaterial("Madeira Nobre");
        nogueira.setCustoUnitario(new BigDecimal("45.00"));
        nogueira.persist();

        // --- Componentes: Faca Bowie ---
        Lamina laminaBowie = new Lamina();
        laminaBowie.setDescricao("Lâmina forjada em aço carbono 1095 — 20cm");
        laminaBowie.setPesoGramas(145.0);
        laminaBowie.setMaterial(acoCarbono);
        laminaBowie.setPerfilDesbaste("Convexo");
        laminaBowie.setEspessuraMm(4.5);
        laminaBowie.persist();

        Cabo caboBowie = new Cabo();
        caboBowie.setDescricao("Cabo em madeira de jacarandá com pino oculto");
        caboBowie.setPesoGramas(95.0);
        caboBowie.setMaterial(jacaranda);
        caboBowie.setFormatoErgonomico("Anatômico");
        caboBowie.setPinoOculto(true);
        caboBowie.persist();

        // --- Modelo 1: Faca Bowie Artesanal ---
        ModeloFaca bowie = new ModeloFaca();
        bowie.setNome("Faca Bowie Artesanal");
        bowie.setDescricao("Faca Bowie forjada à mão em aço carbono 1095. "
                + "Cabo em jacarandá com acabamento encerado. "
                + "Ideal para colecionadores e uso em campo.");
        bowie.setPrecoBase(new BigDecimal("450.00"));
        bowie.setAtivo(true);
        bowie.setCriador(cuteleiro);
        bowie.setLaminaPadrao(laminaBowie);
        bowie.setCaboPadrao(caboBowie);
        bowie.persist();

        OpcaoPersonalizacao gravuraLaminaBowie = new OpcaoPersonalizacao();
        gravuraLaminaBowie.setTipo(TipoPersonalizacao.GRAVURA_LAMINA);
        gravuraLaminaBowie.setOpcao("Gravura na lâmina — ácido");
        gravuraLaminaBowie.setCustoAdicional(new BigDecimal("35.00"));
        gravuraLaminaBowie.setAtivo(true);
        gravuraLaminaBowie.setModeloFaca(bowie);
        gravuraLaminaBowie.persist();

        OpcaoPersonalizacao gravuraCaboBowie = new OpcaoPersonalizacao();
        gravuraCaboBowie.setTipo(TipoPersonalizacao.GRAVURA_CABO);
        gravuraCaboBowie.setOpcao("Gravura no cabo — pirografia");
        gravuraCaboBowie.setCustoAdicional(new BigDecimal("50.00"));
        gravuraCaboBowie.setAtivo(true);
        gravuraCaboBowie.setModeloFaca(bowie);
        gravuraCaboBowie.persist();

        LOG.info("[DataSeeder] Modelo criado: 'Faca Bowie Artesanal' (id=" + bowie.id + ")");

        // --- Componentes: Huntsman ---
        Lamina laminaHuntsman = new Lamina();
        laminaHuntsman.setDescricao("Lâmina gota d'água em aço carbono 1095 — 15cm");
        laminaHuntsman.setPesoGramas(110.0);
        laminaHuntsman.setMaterial(acoCarbono);
        laminaHuntsman.setPerfilDesbaste("Plano");
        laminaHuntsman.setEspessuraMm(3.8);
        laminaHuntsman.persist();

        Cabo caboHuntsman = new Cabo();
        caboHuntsman.setDescricao("Cabo em madeira de nogueira texturizada");
        caboHuntsman.setPesoGramas(80.0);
        caboHuntsman.setMaterial(nogueira);
        caboHuntsman.setFormatoErgonomico("Reto com frisos");
        caboHuntsman.setPinoOculto(false);
        caboHuntsman.persist();

        // --- Modelo 2: Faca de Caça Huntsman ---
        ModeloFaca huntsman = new ModeloFaca();
        huntsman.setNome("Faca de Caça Huntsman");
        huntsman.setDescricao("Faca de caça compacta, leve e resistente. "
                + "Lâmina em aço carbono com perfil plano para trabalhos de precisão. "
                + "Cabo em nogueira com frisos antiderrapantes.");
        huntsman.setPrecoBase(new BigDecimal("320.00"));
        huntsman.setAtivo(true);
        huntsman.setCriador(cuteleiro);
        huntsman.setLaminaPadrao(laminaHuntsman);
        huntsman.setCaboPadrao(caboHuntsman);
        huntsman.persist();

        OpcaoPersonalizacao gravuraLaminaHuntsman = new OpcaoPersonalizacao();
        gravuraLaminaHuntsman.setTipo(TipoPersonalizacao.GRAVURA_LAMINA);
        gravuraLaminaHuntsman.setOpcao("Gravura na lâmina — ácido");
        gravuraLaminaHuntsman.setCustoAdicional(new BigDecimal("35.00"));
        gravuraLaminaHuntsman.setAtivo(true);
        gravuraLaminaHuntsman.setModeloFaca(huntsman);
        gravuraLaminaHuntsman.persist();

        LOG.info("[DataSeeder] Modelo criado: 'Faca de Caça Huntsman' (id=" + huntsman.id + ")");
    }

    // -------------------------------------------------------------------------
    // Histórico de demonstração — 5.B.9 (metade 1)
    // -------------------------------------------------------------------------

    private void seedHistorico() {
        // Só cria se ainda não há compras no banco
        if (Compra.count() > 0) {
            return;
        }

        Cliente cliente = (Cliente) usuarioRepository.findByLogin("cliente");
        ModeloFaca bowie = ModeloFaca.find("nome", "Faca Bowie Artesanal").firstResult();
        ModeloFaca huntsman = ModeloFaca.find("nome", "Faca de Caça Huntsman").firstResult();

        if (cliente == null || bowie == null || huntsman == null) {
            LOG.warn("[DataSeeder] seedHistorico: usuário ou modelos não encontrados — pulando.");
            return;
        }

        EnderecoCliente endereco = EnderecoCliente
                .find("cliente.id = ?1 and principal = true", cliente.id)
                .firstResult();

        // --- Compra já paga: Bowie com GRAVURA_LAMINA ---
        // precoBase: 450,00 + gravura: 35,00 = 485,00
        BigDecimal custoGravura = new BigDecimal("35.00");
        BigDecimal precoBaseBowie = bowie.getPrecoBase(); // 450,00

        ItemCompra itemBowie = new ItemCompra();
        itemBowie.setModeloFaca(bowie);
        itemBowie.setPrecoBase(precoBaseBowie);
        itemBowie.setCustoPersonalizacoes(custoGravura);
        itemBowie.setPersonalizacoes(Map.of("GRAVURA_LAMINA", "Gravura na lâmina — ácido"));
        itemBowie.calcularTotal(); // seta precoTotal = 485,00

        Compra compraPaga = new Compra();
        compraPaga.setNumeroSistema("DEMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        compraPaga.setCliente(cliente);
        compraPaga.setEnderecoEntrega(endereco);
        compraPaga.setStatus(StatusCompra.PAGAMENTO_CONFIRMADO);
        compraPaga.setDataPagamento(LocalDateTime.now().minusDays(5));
        compraPaga.persist(); // precisa de id antes de setar em itemBowie

        itemBowie.setCompra(compraPaga);
        itemBowie.persist();

        compraPaga.getItens().add(itemBowie);
        compraPaga.recalcularTotal(); // 485,00
        compraPaga.persist();

        LOG.info("[DataSeeder] Compra de demonstração criada: " + compraPaga.getNumeroSistema()
                + " | total: R$ " + compraPaga.getPrecoTotal()
                + " | status: " + compraPaga.getStatus());

        // --- ItemListaDesejos: Huntsman com GRAVURA_LAMINA ---
        // precoCalculado: 320,00 + 35,00 = 355,00
        ItemListaDesejos desejo = new ItemListaDesejos();
        desejo.setCliente(cliente);
        desejo.setModeloFaca(huntsman);
        desejo.setPersonalizacoes(Map.of("GRAVURA_LAMINA", "Gravura na lâmina — ácido"));
        desejo.setPrecoCalculado(huntsman.getPrecoBase().add(custoGravura)); // 355,00
        desejo.persist();

        LOG.info("[DataSeeder] ItemListaDesejos de demonstração criado: Huntsman com GRAVURA_LAMINA"
                + " | precoCalculado: R$ " + desejo.getPrecoCalculado());

        // --- Segunda Compra: Huntsman EM_PRODUCAO ---
        // precoBase: 320,00 + gravura: 35,00 = 355,00
        BigDecimal precoBaseHuntsman = huntsman.getPrecoBase();

        ItemCompra itemHuntsman = new ItemCompra();
        itemHuntsman.setModeloFaca(huntsman);
        itemHuntsman.setPrecoBase(precoBaseHuntsman);
        itemHuntsman.setCustoPersonalizacoes(custoGravura);
        itemHuntsman.setPersonalizacoes(Map.of("GRAVURA_LAMINA", "Gravura na lâmina — ácido"));
        itemHuntsman.calcularTotal(); // 355,00

        Compra compraEmProducao = new Compra();
        compraEmProducao.setNumeroSistema("DEMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        compraEmProducao.setCliente(cliente);
        compraEmProducao.setEnderecoEntrega(endereco);
        compraEmProducao.setStatus(StatusCompra.EM_PRODUCAO);
        compraEmProducao.setDataPagamento(LocalDateTime.now().minusDays(2));
        compraEmProducao.persist();

        itemHuntsman.setCompra(compraEmProducao);
        itemHuntsman.persist();

        compraEmProducao.getItens().add(itemHuntsman);
        compraEmProducao.recalcularTotal(); // 355,00
        compraEmProducao.persist();

        LOG.info("[DataSeeder] Compra EM_PRODUCAO criada: " + compraEmProducao.getNumeroSistema()
                + " | total: R$ " + compraEmProducao.getPrecoTotal());

        // --- AvaliacaoModelo: Bowie nota 5, verificado = true ---
        AvaliacaoModelo avaliacao = new AvaliacaoModelo();
        avaliacao.setCliente(cliente);
        avaliacao.setModeloFaca(bowie);
        avaliacao.setNota(5);
        avaliacao.setComentario("Faca incrível! Acabamento impecável e fio afiado. Recomendo muito.");
        avaliacao.setVerificado(true); // compra confirmada existe (compraPaga)
        avaliacao.setDataAvaliacao(LocalDateTime.now().minusDays(3));
        avaliacao.persist();

        LOG.info("[DataSeeder] AvaliacaoModelo criada: Bowie nota 5 (verificado=true)");
    }
}
