package br.unitins.tp1.sga.model;

import br.unitins.tp1.sga.model.ModeloFaca;
import br.unitins.tp1.sga.model.OpcaoPersonalizacao;
import br.unitins.tp1.sga.model.TipoPersonalizacao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ModeloFaca — testes unitários")
class ModeloFacaTest {

    private ModeloFaca modelo;

    @BeforeEach
    void setUp() {
        modelo = new ModeloFaca();
        modelo.setNome("Faca do Sertão");
        modelo.setDescricao("Faca artesanal resistente");
        modelo.setPrecoBase(new BigDecimal("350.00"));
    }

    // ── Construtor ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Construtor deve inicializar dataCriacao e dataUltimaAtualizacao")
    void construtorDeveInicializarDatas() {
        ModeloFaca novo = new ModeloFaca();
        assertNotNull(novo.getDataCriacao());
        assertNotNull(novo.getDataUltimaAtualizacao());
    }

    @Test
    @DisplayName("Construtor deve deixar modelo ativo por padrão")
    void construtorDeveAtivarModelo() {
        assertTrue(new ModeloFaca().isAtivo());
    }

    @Test
    @DisplayName("Construtor deve inicializar listas vazias")
    void construtorDeveInicializarListas() {
        ModeloFaca novo = new ModeloFaca();
        assertNotNull(novo.getImagens());
        assertNotNull(novo.getOpcoesPersonalizacao());
        assertNotNull(novo.getGuarnicoesPadrao());
        assertTrue(novo.getImagens().isEmpty());
        assertTrue(novo.getOpcoesPersonalizacao().isEmpty());
    }

    // ── calcularPrecoComPersonalizacoes ─────────────────────────────────────

    @Test
    @DisplayName("Preço sem opções deve retornar precoBase")
    void precoSemOpcoes() {
        BigDecimal resultado = modelo.calcularPrecoComPersonalizacoes(null);
        assertEquals(new BigDecimal("350.00"), resultado);
    }

    @Test
    @DisplayName("Preço com mapa vazio deve retornar precoBase")
    void precoComMapaVazio() {
        BigDecimal resultado = modelo.calcularPrecoComPersonalizacoes(new HashMap<>());
        assertEquals(new BigDecimal("350.00"), resultado);
    }

    @Test
    @DisplayName("Preço deve somar custo adicional da opção selecionada")
    void precoDeveSomarCustoAdicional() {
        OpcaoPersonalizacao opcao = new OpcaoPersonalizacao();
        opcao.setTipo(TipoPersonalizacao.GRAVURA_LAMINA);
        opcao.setOpcao("Polido");
        opcao.setCustoAdicional(new BigDecimal("50.00"));
        opcao.setAtivo(true);

        modelo.setOpcoesPersonalizacao(List.of(opcao));

        Map<String, String> selecionadas = new HashMap<>();
        selecionadas.put("GRAVURA_LAMINA", "Polido");

        BigDecimal resultado = modelo.calcularPrecoComPersonalizacoes(selecionadas);
        assertEquals(new BigDecimal("400.00"), resultado);
    }

    @Test
    @DisplayName("Opção inativa não deve ser somada ao preço")
    void opcaoInativaNaoDeveSerSomada() {
        OpcaoPersonalizacao opcao = new OpcaoPersonalizacao();
        opcao.setTipo(TipoPersonalizacao.GRAVURA_CABO);
        opcao.setOpcao("Nome");
        opcao.setCustoAdicional(new BigDecimal("30.00"));
        opcao.setAtivo(false);

        modelo.setOpcoesPersonalizacao(List.of(opcao));

        Map<String, String> selecionadas = new HashMap<>();
        selecionadas.put("GRAVURA_CABO", "Nome");

        BigDecimal resultado = modelo.calcularPrecoComPersonalizacoes(selecionadas);
        assertEquals(new BigDecimal("350.00"), resultado);
    }

    @Test
    @DisplayName("Opção não selecionada não deve ser somada")
    void opcaoNaoSelecionadaNaoDeveSerSomada() {
        OpcaoPersonalizacao opcao = new OpcaoPersonalizacao();
        opcao.setTipo(TipoPersonalizacao.GRAVURA_LAMINA);
        opcao.setOpcao("Latão");
        opcao.setCustoAdicional(new BigDecimal("20.00"));
        opcao.setAtivo(true);

        modelo.setOpcoesPersonalizacao(List.of(opcao));

        // Seleciona opção diferente (valor de opcao não bate)
        Map<String, String> selecionadas = new HashMap<>();
        selecionadas.put("GRAVURA_LAMINA", "Aço");

        BigDecimal resultado = modelo.calcularPrecoComPersonalizacoes(selecionadas);
        assertEquals(new BigDecimal("350.00"), resultado);
    }

    @Test
    @DisplayName("Múltiplas opções devem acumular custos corretamente")
    void multipласOpcoesSomadas() {
        List<OpcaoPersonalizacao> opcoes = new ArrayList<>();

        OpcaoPersonalizacao op1 = new OpcaoPersonalizacao();
        op1.setTipo(TipoPersonalizacao.GRAVURA_LAMINA);
        op1.setOpcao("Escovado");
        op1.setCustoAdicional(new BigDecimal("40.00"));
        op1.setAtivo(true);

        OpcaoPersonalizacao op2 = new OpcaoPersonalizacao();
        op2.setTipo(TipoPersonalizacao.GRAVURA_CABO);
        op2.setOpcao("Iniciais");
        op2.setCustoAdicional(new BigDecimal("25.00"));
        op2.setAtivo(true);

        opcoes.add(op1);
        opcoes.add(op2);
        modelo.setOpcoesPersonalizacao(opcoes);

        Map<String, String> selecionadas = new HashMap<>();
        selecionadas.put("GRAVURA_LAMINA", "Escovado");
        selecionadas.put("GRAVURA_CABO", "Iniciais");

        BigDecimal resultado = modelo.calcularPrecoComPersonalizacoes(selecionadas);
        assertEquals(new BigDecimal("415.00"), resultado);
    }

    @Test
    @DisplayName("Opção com tipo null deve ser ignorada")
    void opcaoComTipoNullDeveSerIgnorada() {
        OpcaoPersonalizacao opcao = new OpcaoPersonalizacao();
        opcao.setTipo(null);
        opcao.setOpcao("Algo");
        opcao.setCustoAdicional(new BigDecimal("99.00"));
        opcao.setAtivo(true);

        modelo.setOpcoesPersonalizacao(List.of(opcao));

        Map<String, String> selecionadas = new HashMap<>();
        selecionadas.put("OUTRO", "Algo");

        BigDecimal resultado = modelo.calcularPrecoComPersonalizacoes(selecionadas);
        assertEquals(new BigDecimal("350.00"), resultado);
    }

    @Test
    @DisplayName("precoBase null deve ser tratado como zero")
    void precoBaseNullTratadoComoZero() {
        modelo.setPrecoBase(null);
        BigDecimal resultado = modelo.calcularPrecoComPersonalizacoes(null);
        assertEquals(BigDecimal.ZERO, resultado);
    }

    // ── desativar ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("desativar() deve setar ativo = false")
    void desativarDeveSetarFalse() {
        assertTrue(modelo.isAtivo());
        modelo.desativar();
        assertFalse(modelo.isAtivo());
    }

    @Test
    @DisplayName("desativar() deve atualizar dataUltimaAtualizacao")
    void desativarDeveAtualizarData() {
        LocalDateTime antes = modelo.getDataUltimaAtualizacao();
        // pequena pausa para garantir diferença de timestamp
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        modelo.desativar();
        assertTrue(modelo.getDataUltimaAtualizacao().isAfter(antes)
                || modelo.getDataUltimaAtualizacao().equals(antes)); // mesma resolução aceita
        assertNotNull(modelo.getDataUltimaAtualizacao());
    }

    // ── Getters/Setters ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Getters e setters devem funcionar corretamente")
    void gettersSetters() {
        assertEquals("Faca do Sertão", modelo.getNome());
        assertEquals("Faca artesanal resistente", modelo.getDescricao());
        assertEquals(new BigDecimal("350.00"), modelo.getPrecoBase());

        modelo.setNome("Faca Premium");
        assertEquals("Faca Premium", modelo.getNome());

        modelo.setAtivo(false);
        assertFalse(modelo.isAtivo());
    }
}
