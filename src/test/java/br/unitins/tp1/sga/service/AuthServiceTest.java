package br.unitins.tp1.sga.service;

import br.unitins.tp1.sga.model.*;
import br.unitins.tp1.sga.repository.CodigoRecuperacaoSenhaRepository;
import br.unitins.tp1.sga.repository.UsuarioRepository;
import br.unitins.tp1.sga.service.AuthService;
import br.unitins.tp1.sga.service.EmailService;
import io.quarkus.elytron.security.common.BcryptUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — testes unitários")
class AuthServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    EmailService emailService;

    @Mock
    CodigoRecuperacaoSenhaRepository codigoRepository;

    @InjectMocks
    AuthService authService;

    // ── helpers ─────────────────────────────────────────────────────────────

    private Cliente clienteComSenha(String login, String senhaPlana) {
        Cliente c = new Cliente();
        c.setNome("Teste Cliente");
        c.setLogin(login);
        c.setEmail("teste@email.com");
        c.setSenha(BcryptUtil.bcryptHash(senhaPlana));
        c.setTipoUsuario(TipoUsuario.CLIENTE);
        return c;
    }

    private Administrador administradorComSenhas(String login, String senhaPlana, String senhaAdminPlana) {
        Administrador a = new Administrador();
        a.setNome("Administrador Teste");
        a.setLogin(login);
        a.setEmail("admin@email.com");
        a.setSenha(BcryptUtil.bcryptHash(senhaPlana));
        a.setSenhaAdministrativa(BcryptUtil.bcryptHash(senhaAdminPlana));
        a.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
        return a;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // hashSenha
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("hashSenha")
    class HashSenhaTests {

        @Test
        @DisplayName("hashSenha deve retornar hash bcrypt não nulo")
        void hashSenhaNaoNulo() {
            String hash = authService.hashSenha("minhasenha");
            assertNotNull(hash);
            assertFalse(hash.isBlank());
        }

        @Test
        @DisplayName("hashSenha deve gerar hash diferente do texto puro")
        void hashDiferenteDaOriginal() {
            String hash = authService.hashSenha("senha123");
            assertNotEquals("senha123", hash);
        }

        @Test
        @DisplayName("hashSenha deve gerar hash verificável com bcrypt")
        void hashVerificavelComBcrypt() {
            String hash = authService.hashSenha("abc@123");
            assertTrue(BcryptUtil.matches("abc@123", hash));
        }

        @Test
        @DisplayName("hashSenha de senhas diferentes deve gerar hashes diferentes")
        void senhasDiferentesHashesDiferentes() {
            String h1 = authService.hashSenha("senha1");
            String h2 = authService.hashSenha("senha2");
            assertNotEquals(h1, h2);
        }

        @Test
        @DisplayName("hashSenha da mesma senha chamado duas vezes deve produzir hashes diferentes (salt)")
        void mesmaSenhaHashesDiferentesDevido_ao_Salt() {
            String h1 = authService.hashSenha("minhaSenha");
            String h2 = authService.hashSenha("minhaSenha");
            // bcrypt usa salt aleatório, então os hashes devem ser diferentes
            assertNotEquals(h1, h2);
            // mas ambos devem validar contra a senha original
            assertTrue(BcryptUtil.matches("minhaSenha", h1));
            assertTrue(BcryptUtil.matches("minhaSenha", h2));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // login (e-commerce)
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("login — e-commerce")
    class LoginEcommerceTests {

        @Test
        @DisplayName("Login com credenciais inválidas deve lançar RuntimeException")
        void loginCredenciaisInvalidas() {
            when(usuarioRepository.findByLogin("inexistente")).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.login("inexistente", "qualquer"));
            assertEquals("Login ou senha inválidos", ex.getMessage());
        }

        @Test
        @DisplayName("Login com senha errada deve lançar RuntimeException")
        void loginSenhaErrada() {
            Cliente cliente = clienteComSenha("joao", "senhaCorreta");
            when(usuarioRepository.findByLogin("joao")).thenReturn(cliente);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.login("joao", "senhaErrada"));
            assertEquals("Login ou senha inválidos", ex.getMessage());
        }

        @Test
        @DisplayName("Login com usuário null deve lançar RuntimeException")
        void loginUsuarioNull() {
            when(usuarioRepository.findByLogin(any())).thenReturn(null);

            assertThrows(RuntimeException.class,
                    () -> authService.login("alguem", "algo"));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // loginAdministrativo
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("loginAdministrativo")
    class LoginAdministrativoTests {

        @Test
        @DisplayName("Login administrativo com usuário inexistente deve lançar exceção")
        void loginAdminUsuarioInexistente() {
            when(usuarioRepository.findByLogin("inexistente")).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.loginAdministrativo("inexistente", "qualquer"));
            assertEquals("Login ou senha administrativa inválidos", ex.getMessage());
        }

        @Test
        @DisplayName("Login administrativo com CLIENTE deve lançar exceção de perfil")
        void loginAdminComCliente() {
            Cliente cliente = clienteComSenha("cliente1", "senha");
            // cliente é TipoUsuario.CLIENTE → não pode ser admin
            when(usuarioRepository.findByLogin("cliente1")).thenReturn(cliente);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.loginAdministrativo("cliente1", "qualquer"));
            assertEquals("Usuário não possui perfil administrativo", ex.getMessage());
        }

        @Test
        @DisplayName("Login administrativo com senha_admin errada deve lançar exceção")
        void loginAdminSenhaErrada() {
            Administrador administrador = administradorComSenhas("admin1", "senhaE", "senhaAdmin");
            when(usuarioRepository.findByLogin("admin1")).thenReturn(administrador);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.loginAdministrativo("admin1", "senhaErrada"));
            assertEquals("Login ou senha administrativa inválidos", ex.getMessage());
        }

        @Test
        @DisplayName("Login administrativo com senhaAdministrativa null deve lançar exceção")
        void loginAdminSenhaAdminNull() {
            Administrador administrador = new Administrador();
            administrador.setLogin("admin2");
            administrador.setSenha(BcryptUtil.bcryptHash("senha"));
            administrador.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            administrador.setSenhaAdministrativa(null); // sem senha admin definida

            when(usuarioRepository.findByLogin("admin2")).thenReturn(administrador);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.loginAdministrativo("admin2", "qualquer"));
            assertEquals("Login ou senha administrativa inválidos", ex.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // gerarCodigoRecuperacao
    // ═══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("gerarCodigoRecuperacao")
    class GerarCodigoTests {

        @Test
        @DisplayName("Deve lançar exceção se usuário não encontrado pelo e-mail")
        void usuarioNaoEncontrado() {
            when(usuarioRepository.findByEmail("naoexiste@email.com")).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> authService.gerarCodigoRecuperacao("naoexiste@email.com"));
            assertEquals("Dados não conferem com nenhum cadastro", ex.getMessage());
        }
    }
}
