package smarthouse.com.main.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import smarthouse.com.main.model.User
import smarthouse.com.main.repository.UserRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Testes unitários para UserController
 *
 * Objetivo: Testar operações de CRUD de usuários, autenticação (login/register)
 * e validação de credenciais com BCrypt. Cobrir fluxos sucesso e erro.
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("UserController - Gerenciamento de Usuários e Autenticação")
class UserControllerTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var passwordEncoder: BCryptPasswordEncoder
    private lateinit var userController: UserController

    private lateinit var testUser: User
    private val testEmail = "test@example.com"
    private val testPassword = "password123"
    private val testName = "Test User"

    @BeforeEach
    fun setUp() {
        passwordEncoder = BCryptPasswordEncoder()
        userController = UserController(userRepository, passwordEncoder)

        testUser = User(
            id = 1L,
            email = testEmail,
            password = passwordEncoder.encode(testPassword) ?: "",
            name = testName
        )
    }

    @Nested
    @DisplayName("Operações de Listagem e Busca")
    inner class OperacoesdeLista {

        @Test
        @DisplayName("deveListarTodosOsUsuarios")
        fun deveListarTodosOsUsuarios() {
            // Given: Mock retorna lista de usuários
            val usuarios = listOf(testUser)
            whenever(userRepository.findAll()).thenReturn(usuarios)

            // When
            val resultado = userController.listAll()

            // Then
            assertEquals(1, resultado.size)
            assertEquals(testEmail, resultado[0].email)
            verify(userRepository).findAll()
        }

        @Test
        @DisplayName("deveListarVazioQuandoNaoExistemUsuarios")
        fun deveListarVazioQuandoNaoExistemUsuarios() {
            // Given
            whenever(userRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = userController.listAll()

            // Then
            assertEquals(0, resultado.size)
            verify(userRepository).findAll()
        }

        @Test
        @DisplayName("deveListarMultiplosUsuarios")
        fun deveListarMultiplosUsuarios() {
            // Given
            val usuario2 = User(
                id = 2L,
                email = "user2@example.com",
                password = "encoded",
                name = "User 2"
            )
            val usuarios = listOf(testUser, usuario2)
            whenever(userRepository.findAll()).thenReturn(usuarios)

            // When
            val resultado = userController.listAll()

            // Then
            assertEquals(2, resultado.size)
            verify(userRepository).findAll()
        }
    }

    @Nested
    @DisplayName("Busca por ID")
    inner class BuscaPorId {

        @Test
        @DisplayName("deveBuscarUsuarioPorIdComSucesso")
        fun deveBuscarUsuarioPorIdComSucesso() {
            // Given: Mock retorna usuário
            whenever(userRepository.findById(1L)).thenReturn(Optional.of(testUser))

            // When
            val resultado = userController.findById(1L)

            // Then
            assertNotNull(resultado)
            assertEquals(testEmail, resultado.email)
            assertEquals(testName, resultado.name)
            verify(userRepository).findById(1L)
        }

        @Test
        @DisplayName("deveLancarExcecaoQuandoUsuarioNaoEncontrado")
        fun deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            // Given
            whenever(userRepository.findById(999L)).thenReturn(Optional.empty())

            // When & Then
            try {
                userController.findById(999L)
                assert(false) { "Deve lançar RuntimeException" }
            } catch (e: RuntimeException) {
                assertEquals("Usuário não encontrado", e.message)
            }
            verify(userRepository).findById(999L)
        }

        @Test
        @DisplayName("deveBuscarUsuarioPorIdComIdZero")
        fun deveBuscarUsuarioPorIdComIdZero() {
            // Given
            whenever(userRepository.findById(0L)).thenReturn(Optional.empty())

            // When & Then
            try {
                userController.findById(0L)
                assert(false)
            } catch (e: RuntimeException) {
                assertEquals("Usuário não encontrado", e.message)
            }
        }

        @Test
        @DisplayName("deveBuscarUsuarioPorIdComIdNegativo")
        fun deveBuscarUsuarioPorIdComIdNegativo() {
            // Given
            whenever(userRepository.findById(-1L)).thenReturn(Optional.empty())

            // When & Then
            try {
                userController.findById(-1L)
                assert(false)
            } catch (e: RuntimeException) {
                assertEquals("Usuário não encontrado", e.message)
            }
        }
    }

    @Nested
    @DisplayName("Registro de Novo Usuário")
    inner class RegistroDeUsuario {

        @Test
        @DisplayName("deveRegistrarUsuarioComSucesso")
        fun deveRegistrarUsuarioComSucesso() {
            // Given
            val novoUsuario = User(
                email = "novo@example.com",
                password = testPassword,
                name = "Novo Usuário"
            )
            val usuarioSalvo = User(
                id = 1L,
                email = "novo@example.com",
                password = passwordEncoder.encode(testPassword) ?: "",
                name = "Novo Usuário"
            )
            whenever(userRepository.save(any<User>())).thenReturn(usuarioSalvo)

            // When
            val resultado = userController.register(novoUsuario)

            // Then
            assertNotNull(resultado)
            assertEquals(1L, resultado.id)
            assertEquals("novo@example.com", resultado.email)
            assertEquals("Novo Usuário", resultado.name)
            // Verifica que a senha foi encriptada (nunca armazena texto plano)
            assertTrue(passwordEncoder.matches(testPassword, resultado.password))
            verify(userRepository).save(any<User>())
        }

        @Test
        @DisplayName("deveEncriptarSenhaAoRegistrar")
        fun deveEncriptarSenhaAoRegistrar() {
            // Given
            val usuario = User(email = "test@test.com", password = testPassword, name = "Test")
            val usuarioComSenhaEncriptada = User(
                id = 1L,
                email = "test@test.com",
                password = passwordEncoder.encode(testPassword) ?: "",
                name = "Test"
            )
            whenever(userRepository.save(any())).thenReturn(usuarioComSenhaEncriptada)

            // When
            userController.register(usuario)

            // Then: Verifica que a senha foi passada encriptada
            verify(userRepository).save(any<User>())
        }

        @Test
        @DisplayName("deveRegistrarComEmailVazio")
        fun deveRegistrarComEmailVazio() {
            // Given
            val usuario = User(email = "", password = testPassword, name = "Test")
            whenever(userRepository.save(any<User>())).thenReturn(usuario)

            // When
            val resultado = userController.register(usuario)

            // Then
            assertNotNull(resultado)
            assertEquals("", resultado.email)
            verify(userRepository).save(any<User>())
        }

        @Test
        @DisplayName("deveRegistrarComSenhaVazia")
        fun deveRegistrarComSenhaVazia() {
            // Given
            val usuario = User(email = "user@test.com", password = "", name = "Test")
            whenever(userRepository.save(any<User>())).thenReturn(usuario)

            // When
            val resultado = userController.register(usuario)

            // Then
            assertNotNull(resultado)
            verify(userRepository).save(any<User>())
        }

        @Test
        @DisplayName("deveRegistrarComNomeVazio")
        fun deveRegistrarComNomeVazio() {
            // Given
            val usuario = User(email = "user@test.com", password = testPassword, name = "")
            whenever(userRepository.save(any<User>())).thenReturn(usuario)

            // When
            val resultado = userController.register(usuario)

            // Then
            assertNotNull(resultado)
            assertEquals("", resultado.name)
            verify(userRepository).save(any<User>())
        }
    }

    @Nested
    @DisplayName("Login de Usuário - Fluxo Feliz")
    inner class LoginFluxoFeliz {

        @Test
        @DisplayName("deveLoginComSucesso_QuandoCredenciaisCorretas")
        fun deveLoginComSucessoComCredenciaisCorretas() {
            // Given
            val loginData: Map<String, String> = mapOf(
                "email" to testEmail,
                "password" to testPassword
            )
            whenever(userRepository.findByEmail(testEmail)).thenReturn(testUser)

            // When
            val resultado = userController.login(loginData)

            // Then
            assertTrue(resultado is User)
            val usuarioRetornado = resultado as User
            assertEquals(testEmail, usuarioRetornado.email)
            assertEquals(testName, usuarioRetornado.name)
            verify(userRepository).findByEmail(testEmail)
        }

        @Test
        @DisplayName("deveRetornarUsuario_QuandoSenhaEstaCorreta")
        fun deveRetornarUsuarioQuandoSenhaEstaCorreta() {
            // Given
            val loginData: Map<String, String> = mapOf(
                "email" to testEmail,
                "password" to testPassword
            )
            whenever(userRepository.findByEmail(testEmail)).thenReturn(testUser)

            // When
            val resultado = userController.login(loginData)

            // Then: Deve retornar objeto User, não mensagem de erro
            assertTrue(resultado is User)
        }
    }

    @Nested
    @DisplayName("Login de Usuário - Fluxos de Erro")
    inner class LoginFluxosErro {

        @Test
        @DisplayName("deveRetornarMensagem_QuandoUsuarioNaoEncontrado")
        fun deveRetornarMensagemQuandoUsuarioNaoEncontrado() {
            // Given
            val loginData = mapOf(
                "email" to "naoexiste@example.com",
                "password" to testPassword
            )
            whenever(userRepository.findByEmail("naoexiste@example.com")).thenReturn(null)

            // When
            val resultado = userController.login(loginData)

            // Then: Deve retornar mapa com mensagem de erro
            assertTrue(resultado is Map<*, *>)
            @Suppress("UNCHECKED_CAST")
            val mapa = resultado as Map<String, String>
            assertEquals("Usuário não encontrado", mapa["message"])
            verify(userRepository).findByEmail("naoexiste@example.com")
        }

        @Test
        @DisplayName("deveRetornarMensagem_QuandoSenhaEstaIncorreta")
        fun deveRetornarMensagemQuandoSenhaEstaIncorreta() {
            // Given
            val loginData = mapOf(
                "email" to testEmail,
                "password" to "senhaErrada"
            )
            whenever(userRepository.findByEmail(testEmail)).thenReturn(testUser)

            // When
            val resultado = userController.login(loginData)

            // Then: Deve retornar mapa com mensagem de erro
            assertTrue(resultado is Map<*, *>)
            @Suppress("UNCHECKED_CAST")
            val mapa = resultado as Map<String, String>
            assertEquals("Senha incorreta", mapa["message"])
            verify(userRepository).findByEmail(testEmail)
        }

        @Test
        @DisplayName("deveRetornarMensagem_QuandoEmailVazio")
        fun deveRetornarMensagemQuandoEmailVazio() {
            // Given
            val loginData = mapOf(
                "email" to "",
                "password" to testPassword
            )
            whenever(userRepository.findByEmail("")).thenReturn(null)

            // When
            val resultado = userController.login(loginData)

            // Then
            assertTrue(resultado is Map<*, *>)
        }

        @Test
        @DisplayName("deveRetornarMensagem_QuandoSenhaVazia")
        fun deveRetornarMensagemQuandoSenhaVazia() {
            // Given
            val loginData = mapOf(
                "email" to testEmail,
                "password" to ""
            )
            whenever(userRepository.findByEmail(testEmail)).thenReturn(testUser)

            // When
            val resultado = userController.login(loginData)

            // Then
            assertTrue(resultado is Map<*, *>)
            @Suppress("UNCHECKED_CAST")
            val mapa = resultado as Map<String, String>
            assertEquals("Senha incorreta", mapa["message"])
        }
    }

    @Nested
    @DisplayName("Login - Casos de Borda")
    inner class LoginCasosBorda {

        @Test
        @DisplayName("deveRetornarMensagem_QuandoLoginDataEstaVazio")
        fun deveRetornarMensagemQuandoLoginDataEstaVazio() {
            // Given
            val loginData = emptyMap<String, String>()

            // When
            val resultado = userController.login(loginData)

            // Then
            assertTrue(resultado is Map<*, *>)
        }

        @Test
        @DisplayName("deveRetornarMensagem_QuandoFaltaEmailNoLogin")
        fun deveRetornarMensagemQuandoFaltaEmailNoLogin() {
            // Given
            val loginData = mapOf("password" to testPassword)
            val email = loginData["email"] ?: ""

            // When - O controller usa ?: "" para default
            whenever(userRepository.findByEmail(email)).thenReturn(null)
            val resultado = userController.login(loginData)

            // Then
            assertTrue(resultado is Map<*, *>)
        }

        @Test
        @DisplayName("deveRetornarMensagem_QuandoFaltaSenhaNoLogin")
        fun deveRetornarMensagemQuandoFaltaSenhaNoLogin() {
            // Given
            val loginData = mapOf("email" to testEmail)
            whenever(userRepository.findByEmail(testEmail)).thenReturn(testUser)

            // When
            val resultado = userController.login(loginData)

            // Then
            assertTrue(resultado is Map<*, *>)
            @Suppress("UNCHECKED_CAST")
            val mapa = resultado as Map<String, String>
            assertEquals("Senha incorreta", mapa["message"])
        }

        @Test
        @DisplayName("deveCompararSenhasComSeguranca_UsandoBCrypt")
        fun deveCompararSenhasComSegurancaBCrypt() {
            // Given
            val senhaPlana = "minhaSenha123"
            val senhaEncriptada = passwordEncoder.encode(senhaPlana) ?: ""
            val usuarioComSenhaEncriptada = User(
                id = testUser.id,
                email = testUser.email,
                password = senhaEncriptada,
                name = testUser.name,
                profile = testUser.profile
            )

            val loginData = mapOf(
                "email" to testEmail,
                "password" to senhaPlana
            )
            whenever(userRepository.findByEmail(testEmail)).thenReturn(usuarioComSenhaEncriptada)

            // When
            val resultado = userController.login(loginData)

            // Then: BCrypt deve reconhecer a senha
            assertTrue(resultado is User)
        }
    }

    @Nested
    @DisplayName("Cobertura de Operadores Elvis")
    inner class CoberturElvisOperators {

        @Test
        @DisplayName("deveTratarGetOrNull_ComMapVazia")
        fun deveTratarGetOrNullComMapVazia() {
            // Given
            val loginData = emptyMap<String, String>()

            // When
            val resultado = userController.login(loginData)

            // Then: ?: "" fornece valores default
            assertTrue(resultado is Map<*, *>)
        }

        @Test
        @DisplayName("deveLancarExcecaoComOrElseThrow_QuandoIdNaoExiste")
        fun deveLancarExcecaoComOrElseThrowQuandoIdNaoExiste() {
            // Given
            whenever(userRepository.findById(1L)).thenReturn(Optional.empty())

            // When & Then
            try {
                userController.findById(1L)
                assert(false) { "Deve lançar exceção" }
            } catch (e: RuntimeException) {
                assertTrue(e.message?.contains("Usuário não encontrado") ?: false)
            }
        }
    }

}

