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
import smarthouse.com.main.model.EventLog
import smarthouse.com.main.model.User
import smarthouse.com.main.repository.EventLogRepository
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Testes unitários para EventLogController
 *
 * Objetivo: Testar operações CRUD de logs de eventos com rastreamento de usuários
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("EventLogController - Gerenciamento de Logs de Eventos")
class EventLogControllerTest {

    @Mock
    private lateinit var eventLogRepository: EventLogRepository

    private lateinit var eventLogController: EventLogController

    private lateinit var testEventLog: EventLog
    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        eventLogController = EventLogController(eventLogRepository)

        // Mock utilizado para simular retorno do repositório
        testUser = User(
            id = 1L,
            name = "User Test",
            email = "test@example.com",
            password = "hashed_password"
        )

        // Stub utilizado para fornecer dados controlados
        testEventLog = EventLog(
            id = 1L,
            eventType = "LOGIN",
            message = "Usuário realizou login",
            timestamp = LocalDateTime.now(),
            user = testUser
        )
    }

    @Nested
    @DisplayName("Operações de Listagem")
    inner class ListagensTests {

        @Test
        @DisplayName("deveListarTodosOsEventos")
        fun deveListarTodosOsEventos() {
            // Given
            val eventos = listOf(testEventLog)
            whenever(eventLogRepository.findAll()).thenReturn(eventos)

            // When
            val resultado = eventLogController.list()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("LOGIN", resultado[0].eventType)
            assertEquals("Usuário realizou login", resultado[0].message)
            verify(eventLogRepository).findAll()
        }

        @Test
        @DisplayName("deveListarEventosVazio")
        fun deveListarEventosVazio() {
            // Given
            whenever(eventLogRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = eventLogController.list()

            // Then
            assertEquals(0, resultado.size)
            verify(eventLogRepository).findAll()
        }

        @Test
        @DisplayName("deveListarMultiplosEventos")
        fun deveListarMultiplosEventos() {
            // Given
            val evento2 = EventLog(
                id = 2L,
                eventType = "LOGOUT",
                message = "Usuário realizou logout",
                timestamp = LocalDateTime.now().minusHours(1),
                user = testUser
            )
            val evento3 = EventLog(
                id = 3L,
                eventType = "DEVICE_CONTROL",
                message = "Dispositivo controlado",
                timestamp = LocalDateTime.now().minusHours(2),
                user = testUser
            )
            val eventos = listOf(testEventLog, evento2, evento3)
            whenever(eventLogRepository.findAll()).thenReturn(eventos)

            // When
            val resultado = eventLogController.list()

            // Then
            assertEquals(3, resultado.size)
            assertEquals("LOGIN", resultado[0].eventType)
            assertEquals("LOGOUT", resultado[1].eventType)
            assertEquals("DEVICE_CONTROL", resultado[2].eventType)
        }

        @Test
        @DisplayName("deveListarEventosComUsuarioNull")
        fun deveListarEventosComUsuarioNull() {
            // Given
            val eventoSemUsuario = EventLog(
                id = 1L,
                eventType = "SYSTEM",
                message = "Evento do sistema",
                timestamp = LocalDateTime.now(),
                user = null
            )
            whenever(eventLogRepository.findAll()).thenReturn(listOf(eventoSemUsuario))

            // When
            val resultado = eventLogController.list()

            // Then
            assertEquals(1, resultado.size)
            assertEquals(null, resultado[0].user)
        }
    }

    @Nested
    @DisplayName("Operações de Criação")
    inner class CriacaoTests {

        @Test
        @DisplayName("deveCriarUmEventoLog")
        fun deveCriarUmEventoLog() {
            // Given
            whenever(eventLogRepository.save(any())).thenReturn(testEventLog)

            // When
            val resultado = eventLogController.create(testEventLog)

            // Then
            assertNotNull(resultado)
            assertEquals("LOGIN", resultado.eventType)
            assertEquals("Usuário realizou login", resultado.message)
            assertEquals(1L, resultado.user?.id)
            verify(eventLogRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarEventoComTipoVariado")
        fun deveCriarEventoComTipoVariado() {
            // Given
            val tiposEventos = listOf("LOGIN", "LOGOUT", "DEVICE_CONTROL", "RULE_TRIGGERED", "ALERT")
            for (tipo in tiposEventos) {
                val evento = EventLog(
                    id = null,
                    eventType = tipo,
                    message = "Evento de $tipo",
                    timestamp = LocalDateTime.now(),
                    user = testUser
                )
                whenever(eventLogRepository.save(any())).thenReturn(evento)

                // When
                val resultado = eventLogController.create(evento)

                // Then
                assertEquals(tipo, resultado.eventType)
            }
        }

        @Test
        @DisplayName("deveCriarEventoSemUsuario")
        fun deveCriarEventoSemUsuario() {
            // Given
            val eventoSistemico = EventLog(
                id = null,
                eventType = "SYSTEM_BOOT",
                message = "Sistema iniciado",
                timestamp = LocalDateTime.now(),
                user = null
            )
            whenever(eventLogRepository.save(any())).thenReturn(eventoSistemico)

            // When
            val resultado = eventLogController.create(eventoSistemico)

            // Then
            assertNotNull(resultado)
            assertEquals(null, resultado.user)
            assertEquals("SYSTEM_BOOT", resultado.eventType)
        }

        @Test
        @DisplayName("deveCriarEventoComMensagemLonga")
        fun deveCriarEventoComMensagemLonga() {
            // Given
            val mensagemLonga = "X".repeat(500)
            val evento = EventLog(
                id = null,
                eventType = "LONG_MESSAGE",
                message = mensagemLonga,
                timestamp = LocalDateTime.now(),
                user = testUser
            )
            whenever(eventLogRepository.save(any())).thenReturn(evento)

            // When
            val resultado = eventLogController.create(evento)

            // Then
            assertEquals(500, resultado.message.length)
        }

        @Test
        @DisplayName("deveCriarMultiplosEventosSequenciais")
        fun deveCriarMultiplosEventosSequenciais() {
            // Given
            val evento1 = EventLog(1L, "LOGIN", "Login", LocalDateTime.now(), testUser)
            val evento2 = EventLog(2L, "ACTION", "Action", LocalDateTime.now(), testUser)
            whenever(eventLogRepository.save(any()))
                .thenReturn(evento1)
                .thenReturn(evento2)

            // When
            val res1 = eventLogController.create(evento1)
            val res2 = eventLogController.create(evento2)

            // Then
            assertEquals("LOGIN", res1.eventType)
            assertEquals("ACTION", res2.eventType)
        }
    }

    @Nested
    @DisplayName("Operações de Deleção")
    inner class DelecaoTests {

        @Test
        @DisplayName("deveDeletarUmEventoLog")
        fun deveDeletarUmEventoLog() {
            // When
            eventLogController.delete(1L)

            // Then
            verify(eventLogRepository).deleteById(1L)
        }

        @Test
        @DisplayName("deveDeletarMultiplosEventoLogs")
        fun deveDeletarMultiplosEventoLogs() {
            // When
            eventLogController.delete(1L)
            eventLogController.delete(2L)
            eventLogController.delete(3L)

            // Then
            verify(eventLogRepository).deleteById(1L)
            verify(eventLogRepository).deleteById(2L)
            verify(eventLogRepository).deleteById(3L)
        }

        @Test
        @DisplayName("deveDeletarEventoComIdGrande")
        fun deveDeletarEventoComIdGrande() {
            // When
            eventLogController.delete(999999999L)

            // Then
            verify(eventLogRepository).deleteById(999999999L)
        }
    }
}

