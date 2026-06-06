package smarthouse.com.main.controller

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
import smarthouse.com.main.model.Notification
import smarthouse.com.main.model.User
import smarthouse.com.main.repository.NotificationRepository
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Testes unitários para NotificationController
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("NotificationController - Gerenciamento de Notificações")
class NotificationControllerTest {

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    private lateinit var notificationController: NotificationController

    private lateinit var testUser: User
    private lateinit var testNotification: Notification

    @BeforeEach
    fun setUp() {
        notificationController = NotificationController(notificationRepository)

        testUser = User(id = 1L, email = "user@test.com", name = "User")

        testNotification = Notification(
            id = 1L,
            message = "Novo evento",
            timestamp = LocalDateTime.now(),
            read = false,
            user = testUser
        )
    }

    @Nested
    @DisplayName("Operações de Listagem")
    inner class OperacoesListagem {

        @Test
        @DisplayName("deveListarNotificacoes")
        fun deveListarNotificacoes() {
            // Given
            whenever(notificationRepository.findAll()).thenReturn(listOf(testNotification))

            // When
            val resultado = notificationController.list()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("Novo evento", resultado[0].message)
        }

        @Test
        @DisplayName("deveListarNotificacoesVazio")
        fun deveListarNotificacoesVazio() {
            // Given
            whenever(notificationRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = notificationController.list()

            // Then
            assertEquals(0, resultado.size)
        }

        @Test
        @DisplayName("deveListarMultiplasNotificacoes")
        fun deveListarMultiplasNotificacoes() {
            // Given
            val notif2 = Notification(2L, "Evento 2", LocalDateTime.now().minusHours(1), false, testUser)
            val notif3 = Notification(3L, "Evento 3", LocalDateTime.now().minusHours(2), true, testUser)
            whenever(notificationRepository.findAll()).thenReturn(listOf(testNotification, notif2, notif3))

            // When
            val resultado = notificationController.list()

            // Then
            assertEquals(3, resultado.size)
        }

        @Test
        @DisplayName("deveListarNotificacoesLidas")
        fun deveListarNotificacoesLidas() {
            // Given
            val notifLida = Notification(1L, "Mensagem lida", LocalDateTime.now(), true, testUser)
            whenever(notificationRepository.findAll()).thenReturn(listOf(notifLida))

            // When
            val resultado = notificationController.list()

            // Then
            assertEquals(true, resultado[0].read)
        }
    }

    @Nested
    @DisplayName("Operações de Persistência")
    inner class OperacoesPersistencia {

        @Test
        @DisplayName("deveCriarNotificacao")
        fun deveCriarNotificacao() {
            // Given
            whenever(notificationRepository.save(any<Notification>()))
                .thenReturn(testNotification)

            // When
            val resultado = notificationController.create(testNotification)

            // Then
            assertNotNull(resultado)
            assertEquals("Novo evento", resultado.message)
        }

        @Test
        @DisplayName("deveCriarNotificacaoLida")
        fun deveCriarNotificacaoLida() {
            // Given
            val notifLida = Notification(null, "Notificação já lida", LocalDateTime.now(), true, testUser)
            whenever(notificationRepository.save(any())).thenReturn(notifLida)

            // When
            val resultado = notificationController.create(notifLida)

            // Then
            assertEquals(true, resultado.read)
        }

        @Test
        @DisplayName("deveCriarMultiplasNotificacoes")
        fun deveCriarMultiplasNotificacoes() {
            // Given
            val notif1 = Notification(1L, "Notif 1", LocalDateTime.now(), false, testUser)
            val notif2 = Notification(2L, "Notif 2", LocalDateTime.now(), false, testUser)
            whenever(notificationRepository.save(any()))
                .thenReturn(notif1)
                .thenReturn(notif2)

            // When
            val res1 = notificationController.create(notif1)
            val res2 = notificationController.create(notif2)

            // Then
            assertEquals("Notif 1", res1.message)
            assertEquals("Notif 2", res2.message)
        }

        @Test
        @DisplayName("deveAtualizarNotificacao")
        fun deveAtualizarNotificacao() {
            // Given
            whenever(notificationRepository.findById(1L))
                .thenReturn(Optional.of(testNotification))
            val notificationAtualizada = Notification(
                id = 1L,
                message = "Evento atualizado",
                timestamp = testNotification.timestamp,
                read = true,
                user = testUser
            )
            whenever(notificationRepository.save(any<Notification>()))
                .thenReturn(notificationAtualizada)

            // When
            val resultado = notificationController.update(1L, notificationAtualizada)

            // Then
            assertNotNull(resultado)
            assertEquals("Evento atualizado", resultado.message)
            verify(notificationRepository).findById(1L)
            verify(notificationRepository).save(any<Notification>())
        }

        @Test
        @DisplayName("deveAtualizarStatusParaLida")
        fun deveAtualizarStatusParaLida() {
            // Given
            val notifNaoLida = Notification(1L, "Msg", LocalDateTime.now(), false, testUser)
            val notifLida = Notification(1L, "Msg", LocalDateTime.now(), true, testUser)
            whenever(notificationRepository.findById(1L)).thenReturn(Optional.of(notifNaoLida))
            whenever(notificationRepository.save(any())).thenReturn(notifLida)

            // When
            val resultado = notificationController.update(1L, notifLida)

            // Then
            assertEquals(true, resultado.read)
        }

        @Test
        @DisplayName("deveMudarMensagemDaNotificacao")
        fun deveMudarMensagemDaNotificacao() {
            // Given
            whenever(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification))
            val notificationNovaMensagem = Notification(
                1L,
                "Nova mensagem",
                testNotification.timestamp,
                testNotification.read,
                testUser
            )
            whenever(notificationRepository.save(any())).thenReturn(notificationNovaMensagem)

            // When
            val resultado = notificationController.update(1L, notificationNovaMensagem)

            // Then
            assertEquals("Nova mensagem", resultado.message)
        }

        @Test
        @DisplayName("deveDeleteNotificacao")
        fun deveDeleteNotificacao() {
            // Given & When
            notificationController.delete(1L)

            // Then
            verify(notificationRepository).deleteById(1L)
        }

        @Test
        @DisplayName("deveDeletarMultiplasNotificacoes")
        fun deveDeletarMultiplasNotificacoes() {
            // When
            notificationController.delete(1L)
            notificationController.delete(2L)
            notificationController.delete(3L)

            // Then
            verify(notificationRepository).deleteById(1L)
            verify(notificationRepository).deleteById(2L)
            verify(notificationRepository).deleteById(3L)
        }
    }

    @Nested
    @DisplayName("Operações com Erros")
    inner class OperacoesErros {

        @Test
        @DisplayName("deveLancarExcecaoAoAtualizarNotificacaoInexistente")
        fun deveLancarExcecaoAoAtualizarNotificacaoInexistente() {
            // Given
            whenever(notificationRepository.findById(999L))
                .thenReturn(Optional.empty())

            // When & Then
            try {
                notificationController.update(999L, testNotification)
                assert(false) { "Deve lançar exceção" }
            } catch (e: NoSuchElementException) {
                // Expected
            }
        }
    }
}


