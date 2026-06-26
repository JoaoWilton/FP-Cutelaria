package br.unitins.tp1.sga.model;

import br.unitins.tp1.sga.model.Cliente;
import br.unitins.tp1.sga.model.EnderecoCliente;
import br.unitins.tp1.sga.model.OpcaoPersonalizacao;
import br.unitins.tp1.sga.model.TipoUsuario;
import br.unitins.tp1.sga.model.TipoPersonalizacao;
import br.unitins.tp1.sga.model.StatusCompra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Models auxiliares — testes unitários")
class ModelAuxiliarTest {

    @Test
    @DisplayName("Cliente: dataCadastro inicializada como hoje")
    void clienteDataCadastroHoje() {
        Cliente c = new Cliente();
        assertEquals(LocalDate.now(), c.getDataCadastro());
    }

    @Test
    @DisplayName("Cliente: cadastroCompleto false por padrao")
    void clienteCadastroCompletoFalse() {
        assertFalse(new Cliente().isCadastroCompleto());
    }

    @Test
    @DisplayName("Cliente: deve aceitar CPF")
    void clienteAceitaCpf() {
        Cliente c = new Cliente();
        c.setCpf("12345678901");
        assertEquals("12345678901", c.getCpf());
    }

    @Test
    @DisplayName("Cliente: listas inicializadas")
    void clienteListasInicializadas() {
        Cliente c = new Cliente();
        assertNotNull(c.getItensListaDesejos());
        assertNotNull(c.getCompras());
        assertNotNull(c.getEnderecos());
        assertNotNull(c.getAvaliacoes());
    }

    @Test
    @DisplayName("Cliente: deve armazenar data de nascimento")
    void clienteDataNascimento() {
        Cliente c = new Cliente();
        LocalDate dn = LocalDate.of(1995, 5, 20);
        c.setDataNascimento(dn);
        assertEquals(dn, c.getDataNascimento());
    }

    @Test
    @DisplayName("Cliente: deve completar cadastro")
    void clienteCompletarCadastro() {
        Cliente c = new Cliente();
        c.setCadastroCompleto(true);
        assertTrue(c.isCadastroCompleto());
    }

    @Test
    @DisplayName("EnderecoCliente: ativo true por padrao")
    void enderecoAtivoTrue() {
        assertTrue(new EnderecoCliente().isAtivo());
    }

    @Test
    @DisplayName("EnderecoCliente: principal false por padrao")
    void enderecoPrincipalFalse() {
        assertFalse(new EnderecoCliente().isPrincipal());
    }

    @Test
    @DisplayName("EnderecoCliente: getters corretos")
    void enderecoGetters() {
        EnderecoCliente e = new EnderecoCliente();
        e.setCep("77001000");
        e.setRua("Quadra 103 Sul");
        e.setNumero("12");
        e.setBairro("Plano Diretor Sul");
        e.setCidade("Palmas");
        e.setEstado("TO");
        assertEquals("77001000", e.getCep());
        assertEquals("Palmas", e.getCidade());
        assertEquals("TO", e.getEstado());
    }

    @Test
    @DisplayName("EnderecoCliente: complemento null por padrao")
    void enderecoComplementoNull() {
        assertNull(new EnderecoCliente().getComplemento());
    }

    @Test
    @DisplayName("EnderecoCliente: definir como principal")
    void enderecoDefinirPrincipal() {
        EnderecoCliente e = new EnderecoCliente();
        e.setPrincipal(true);
        assertTrue(e.isPrincipal());
    }

    @Test
    @DisplayName("EnderecoCliente: associar cliente")
    void enderecoAssociarCliente() {
        EnderecoCliente e = new EnderecoCliente();
        Cliente c = new Cliente();
        c.setNome("Pedro");
        e.setCliente(c);
        assertEquals("Pedro", e.getCliente().getNome());
    }

    @Test
    @DisplayName("OpcaoPersonalizacao: ativo true por padrao")
    void opcaoAtivoTrue() {
        assertTrue(new OpcaoPersonalizacao().isAtivo());
    }

    @Test
    @DisplayName("OpcaoPersonalizacao: custoAdicional zero por padrao")
    void opcaoCustoZero() {
        assertEquals(0, new OpcaoPersonalizacao().getCustoAdicional().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("OpcaoPersonalizacao: armazenar custo")
    void opcaoArmazenarCusto() {
        OpcaoPersonalizacao op = new OpcaoPersonalizacao();
        op.setCustoAdicional(new BigDecimal("45.50"));
        assertEquals(new BigDecimal("45.50"), op.getCustoAdicional());
    }

    @Test
    @DisplayName("OpcaoPersonalizacao: armazenar texto")
    void opcaoArmazenarTexto() {
        OpcaoPersonalizacao op = new OpcaoPersonalizacao();
        op.setOpcao("Cabo Jacarandá");
        assertEquals("Cabo Jacarandá", op.getOpcao());
    }

    @Test
    @DisplayName("OpcaoPersonalizacao: desativar")
    void opcaoDesativar() {
        OpcaoPersonalizacao op = new OpcaoPersonalizacao();
        op.setAtivo(false);
        assertFalse(op.isAtivo());
    }

    @Test
    @DisplayName("TipoUsuario: deve ter 4 valores")
    void tipoUsuarioQuatroValores() {
        assertEquals(4, TipoUsuario.values().length);
    }

    @Test
    @DisplayName("TipoUsuario: CLIENTE existe")
    void tipoUsuarioCliente() {
        assertDoesNotThrow(() -> TipoUsuario.valueOf("CLIENTE"));
    }

    @Test
    @DisplayName("TipoUsuario: CUTELEIRO existe")
    void tipoUsuarioCuteleiro() {
        assertDoesNotThrow(() -> TipoUsuario.valueOf("CUTELEIRO"));
    }

    @Test
    @DisplayName("TipoUsuario: ADMINISTRADOR existe")
    void tipoUsuarioAdministrador() {
        assertDoesNotThrow(() -> TipoUsuario.valueOf("ADMINISTRADOR"));
    }

    @Test
    @DisplayName("TipoUsuario: FORNECEDOR existe")
    void tipoUsuarioFornecedor() {
        assertDoesNotThrow(() -> TipoUsuario.valueOf("FORNECEDOR"));
    }

    @Test
    @DisplayName("StatusCompra: deve ter 6 valores")
    void statusCompraSeisValores() {
        assertEquals(6, StatusCompra.values().length);
    }

    @Test
    @DisplayName("StatusCompra: AGUARDANDO_PAGAMENTO existe")
    void statusAguardandoPagamento() {
        assertDoesNotThrow(() -> StatusCompra.valueOf("AGUARDANDO_PAGAMENTO"));
    }

    @Test
    @DisplayName("StatusCompra: PAGAMENTO_CONFIRMADO existe")
    void statusPagamentoConfirmado() {
        assertDoesNotThrow(() -> StatusCompra.valueOf("PAGAMENTO_CONFIRMADO"));
    }

    @Test
    @DisplayName("StatusCompra: EM_PRODUCAO existe")
    void statusEmProducao() {
        assertDoesNotThrow(() -> StatusCompra.valueOf("EM_PRODUCAO"));
    }

    @Test
    @DisplayName("StatusCompra: CANCELADO existe")
    void statusCancelado() {
        assertDoesNotThrow(() -> StatusCompra.valueOf("CANCELADO"));
    }

    @Test
    @DisplayName("StatusCompra: ENTREGUE existe")
    void statusEntregue() {
        assertDoesNotThrow(() -> StatusCompra.valueOf("ENTREGUE"));
    }

    @Test
    @DisplayName("StatusCompra: CARRINHO não existe mais")
    void statusCarrinhoNaoExiste() {
        assertThrows(IllegalArgumentException.class, () -> StatusCompra.valueOf("CARRINHO"));
    }

    @Test
    @DisplayName("TipoPersonalizacao: deve ter 2 valores")
    void tipoPersonalizacaoCincoValores() {
        assertEquals(2, TipoPersonalizacao.values().length);
    }

    @Test
    @DisplayName("TipoPersonalizacao: GRAVURA_LAMINA existe")
    void tipoGravura() {
        assertDoesNotThrow(() -> TipoPersonalizacao.valueOf("GRAVURA_LAMINA"));
    }

    @Test
    @DisplayName("TipoPersonalizacao: GRAVURA_CABO existe")
    void tipoAcabamento() {
        assertDoesNotThrow(() -> TipoPersonalizacao.valueOf("GRAVURA_CABO"));
    }

    @Test
    @DisplayName("TipoPersonalizacao: valor inválido deve lançar exceção")
    void tipoPomo() {
        assertThrows(IllegalArgumentException.class, () -> TipoPersonalizacao.valueOf("POMO"));
    }

    @Test
    @DisplayName("TipoPersonalizacao: OUTRO não existe mais")
    void tipoOutro() {
        assertThrows(IllegalArgumentException.class, () -> TipoPersonalizacao.valueOf("OUTRO"));
    }
}
