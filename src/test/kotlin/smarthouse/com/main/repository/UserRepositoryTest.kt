package smarthouse.com.main.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import smarthouse.com.main.model.User
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Testes unitários para UserRepository
 *
 * Objetivo: Testar contratos do repository usando mocks
 * para simular comportamento da camada de acesso a dados.
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("UserRepository - Testes de Contrato")
class UserRepositoryTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        testUser = User(
            id = 1L,
            email = "test@example.com",
            password = "encoded_password",
            name = "Test User"
        )
    }

    @Nested
    @DisplayName("Operações de Busca e Listagem")
    inner class BuscaEListagem {

        @Test
        @DisplayName("deveFindAllRetornarLista")
        fun deveFindAllRetornarLista() {
            // Given: Mock retorna lista de usuários
            val usuarios = listOf(testUser)
            whenever(userRepository.findAll()).thenReturn(usuarios)

            // When
            val resultado = userRepository.findAll()

            // Then
            assertNotNull(resultado)
            assertEquals(1, resultado.size)
            assertEquals(testUser.email, resultado[0].email)
            verify(userRepository).findAll()
        }

        @Test
        @DisplayName("deveFindAllRetornarVazio")
        fun deveFindAllRetornarVazio() {
            // Given
            whenever(userRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = userRepository.findAll()

            // Then
            assertEquals(0, resultado.size)
            verify(userRepository).findAll()
        }

        @Test
        @DisplayName("deveFindByIdRetornarUsuario")
        fun deveFindByIdRetornarUsuario() {
            // Given
            whenever(userRepository.findById(1L)).thenReturn(Optional.of(testUser))

            // When
            val resultado = userRepository.findById(1L)

            // Then
            assert(resultado.isPresent)
            assertEquals(testUser.id, resultado.get().id)
            verify(userRepository).findById(1L)
        }

        @Test
        @DisplayName("deveFindByIdRetornarVazio")
        fun deveFindByIdRetornarVazio() {
            // Given
            whenever(userRepository.findById(999L)).thenReturn(Optional.empty())

            // When
            val resultado = userRepository.findById(999L)

            // Then
            assert(!resultado.isPresent)
            verify(userRepository).findById(999L)
        }

        @Test
        @DisplayName("deveFindByEmailRetornarUsuario")
        fun deveFindByEmailRetornarUsuario() {
            // Given
            whenever(userRepository.findByEmail("test@example.com")).thenReturn(testUser)

            // When
            val resultado = userRepository.findByEmail("test@example.com")

            // Then
            assertNotNull(resultado)
            assertEquals("test@example.com", resultado!!.email)
            verify(userRepository).findByEmail("test@example.com")
        }

        @Test
        @DisplayName("deveFindByEmailRetornarNull")
        fun deveFindByEmailRetornarNull() {
            // Given
            whenever(userRepository.findByEmail("naoexiste@example.com")).thenReturn(null)

            // When
            val resultado = userRepository.findByEmail("naoexiste@example.com")

            // Then
            assertNull(resultado)
            verify(userRepository).findByEmail("naoexiste@example.com")
        }
    }

    @Nested
    @DisplayName("Operações de Persistência")
    inner class OperacoesPersistencia {

        @Test
        @DisplayName("deveSalvarUsuario")
        fun deveSalvarUsuario() {
            // Given
            whenever(userRepository.save(any<User>())).thenReturn(testUser)

            // When
            val resultado = userRepository.save(testUser)

            // Then
            assertNotNull(resultado)
            assertEquals(testUser.email, resultado.email)
            verify(userRepository).save(any<User>())
        }

        @Test
        @DisplayName("deveDeleteUsuario")
        fun deveDeleteUsuario() {
            // Given & When
            userRepository.delete(testUser)

            // Then
            verify(userRepository).delete(testUser)
        }

        @Test
        @DisplayName("deveExistsByIdRetornarTrue")
        fun deveExistsByIdRetornarTrue() {
            // Given
            whenever(userRepository.existsById(1L)).thenReturn(true)

            // When
            val resultado = userRepository.existsById(1L)

            // Then
            assertEquals(true, resultado)
            verify(userRepository).existsById(1L)
        }

        @Test
        @DisplayName("deveExistsByIdRetornarFalse")
        fun deveExistsByIdRetornarFalse() {
            // Given
            whenever(userRepository.existsById(999L)).thenReturn(false)

            // When
            val resultado = userRepository.existsById(999L)

            // Then
            assertEquals(false, resultado)
            verify(userRepository).existsById(999L)
        }
    }

    @Nested
    @DisplayName("Operações com Múltiplos Usuários")
    inner class OperacoesMultiplos {

        @Test
        @DisplayName("deveSalvarTodos")
        fun deveSalvarTodos() {
            // Given
            val usuario1 = User(id = 1L, email = "user1@test.com", password = "pwd", name = "User 1")
            val usuario2 = User(id = 2L, email = "user2@test.com", password = "pwd", name = "User 2")
            val usuarios = listOf(usuario1, usuario2)
            whenever(userRepository.saveAll(usuarios)).thenReturn(usuarios)

            // When
            val resultado = userRepository.saveAll(usuarios)

            // Then
            assertEquals(2, resultado.size)
            verify(userRepository).saveAll(usuarios)
        }

        @Test
        @DisplayName("deveDeleteTodos")
        fun deveDeleteTodos() {
            // Given
            val usuarios = listOf(testUser)

            // When
            userRepository.deleteAll(usuarios)

            // Then
            verify(userRepository).deleteAll(usuarios)
        }
    }

}

