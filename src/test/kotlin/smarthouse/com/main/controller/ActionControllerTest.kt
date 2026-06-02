package smarthouse.com.main.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import smarthouse.com.main.model.Action
import smarthouse.com.main.model.IotDevice
import smarthouse.com.main.model.Room
import smarthouse.com.main.model.DeviceType
import smarthouse.com.main.repository.ActionRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Testes unitários para ActionController
 *
 * Objetivo: Testar operações CRUD de ações com tratamento de exceções
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("ActionController - Gerenciamento de Ações")
class ActionControllerTest {

    @Mock
    private lateinit var actionRepository: ActionRepository

    private lateinit var actionController: ActionController

    private lateinit var testAction: Action
    private lateinit var testDevice: IotDevice
    private lateinit var testRoom: Room
    private lateinit var testDeviceType: DeviceType

    @BeforeEach
    fun setUp() {
        actionController = ActionController(actionRepository)

        // Mock utilizado para simular retorno do repositório
        testDeviceType = DeviceType(
            id = 1L,
            name = "Lâmpada RGB",
            manufacturer = "Philips",
            unit = null
        )

        // Stub utilizado para fornecer dados controlados
        testRoom = Room(
            id = 1L,
            name = "Sala",
            type = "SALA",
            house = null
        )

        // Stub utilizado para fornecer dados controlados
        testDevice = IotDevice(
            id = 1L,
            name = "Lâmpada 1",
            deviceType = testDeviceType,
            topic = "casa/sala/lampada1",
            status = "OFF",
            room = testRoom
        )

        testAction = Action(
            id = 1L,
            name = "Ligar Lâmpada",
            device = testDevice,
            command = "ON"
        )
    }

    @Nested
    @DisplayName("Operações de Listagem")
    inner class ListagensTests {

        @Test
        @DisplayName("deveListarTodasAsAcoes")
        fun deveListarTodasAsAcoes() {
            // Given
            val acoes = listOf(testAction)
            whenever(actionRepository.findAll()).thenReturn(acoes)

            // When
            val resultado = actionController.list()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("Ligar Lâmpada", resultado[0].name)
            assertEquals("ON", resultado[0].command)
            verify(actionRepository).findAll()
        }

        @Test
        @DisplayName("deveListarAcoesVazio")
        fun deveListarAcoesVazio() {
            // Given
            whenever(actionRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = actionController.list()

            // Then
            assertEquals(0, resultado.size)
            verify(actionRepository).findAll()
        }

        @Test
        @DisplayName("deveListarMultiplasAcoes")
        fun deveListarMultiplasAcoes() {
            // Given
            val acao2 = Action(
                id = 2L,
                name = "Desligar Lâmpada",
                device = testDevice,
                command = "OFF"
            )
            val acoes = listOf(testAction, acao2)
            whenever(actionRepository.findAll()).thenReturn(acoes)

            // When
            val resultado = actionController.list()

            // Then
            assertEquals(2, resultado.size)
            assertEquals("Ligar Lâmpada", resultado[0].name)
            assertEquals("Desligar Lâmpada", resultado[1].name)
        }
    }

    @Nested
    @DisplayName("Operações de Criação")
    inner class CriacaoTests {

        @Test
        @DisplayName("deveCriarUmaAcao")
        fun deveCriarUmaAcao() {
            // Given
            whenever(actionRepository.save(any())).thenReturn(testAction)

            // When
            val resultado = actionController.create(testAction)

            // Then
            assertNotNull(resultado)
            assertEquals("Ligar Lâmpada", resultado.name)
            assertEquals("ON", resultado.command)
            verify(actionRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarAcaoComComandoVazio")
        fun deveCriarAcaoComComandoVazio() {
            // Given
            val acaoVazia = Action(
                id = null,
                name = "Ação Vazia",
                device = testDevice,
                command = ""
            )
            whenever(actionRepository.save(any())).thenReturn(acaoVazia)

            // When
            val resultado = actionController.create(acaoVazia)

            // Then
            assertNotNull(resultado)
            assertEquals("", resultado.command)
            verify(actionRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarAcaoSemDispositivo")
        fun deveCriarAcaoSemDispositivo() {
            // Given
            val acaoSemDispositivo = Action(
                id = null,
                name = "Ação Sem Dispositivo",
                device = null,
                command = "TEST"
            )
            whenever(actionRepository.save(any())).thenReturn(acaoSemDispositivo)

            // When
            val resultado = actionController.create(acaoSemDispositivo)

            // Then
            assertNotNull(resultado)
            assertEquals(null, resultado.device)
            verify(actionRepository).save(any())
        }
    }

    @Nested
    @DisplayName("Operações de Atualização")
    inner class AtualizacaoTests {

        @Test
        @DisplayName("deveAtualizarUmaAcao")
        fun deveAtualizarUmaAcao() {
            // Given
            val acaoAtualizada = Action(
                id = 1L,
                name = "Ligar Lâmpada - Atualizada",
                device = testDevice,
                command = "ON_BRIGHTNESS:100"
            )
            whenever(actionRepository.findById(1L)).thenReturn(Optional.of(testAction))
            whenever(actionRepository.save(any())).thenReturn(acaoAtualizada)

            // When
            val resultado = actionController.update(1L, acaoAtualizada)

            // Then
            assertNotNull(resultado)
            assertEquals("Ligar Lâmpada - Atualizada", resultado.name)
            assertEquals("ON_BRIGHTNESS:100", resultado.command)
            verify(actionRepository).findById(1L)
            verify(actionRepository).save(any())
        }

        @Test
        @DisplayName("deveAtualizarComDispositivoDiferente")
        fun deveAtualizarComDispositivoDiferente() {
            // Given
            val outroDispositivo = IotDevice(
                id = 2L,
                name = "Lâmpada 2",
                deviceType = testDeviceType,
                topic = "casa/quarto/lampada2",
                status = "OFF",
                room = testRoom
            )
            val acaoAtualizada = Action(
                id = 1L,
                name = "Ligar Lâmpada",
                device = outroDispositivo,
                command = "ON"
            )
            whenever(actionRepository.findById(1L)).thenReturn(Optional.of(testAction))
            whenever(actionRepository.save(any())).thenReturn(acaoAtualizada)

            // When
            val resultado = actionController.update(1L, acaoAtualizada)

            // Then
            assertNotNull(resultado)
            assertEquals(2L, resultado.device?.id)
            verify(actionRepository).findById(1L)
        }
    }

    @Nested
    @DisplayName("Operações de Deleção")
    inner class DelecaoTests {

        @Test
        @DisplayName("deveDeletarUmaAcao")
        fun deveDeletarUmaAcao() {
            // When
            actionController.delete(1L)

            // Then
            verify(actionRepository).deleteById(1L)
        }

        @Test
        @DisplayName("deveDeletarAcaoComIdDiferente")
        fun deveDeletarAcaoComIdDiferente() {
            // When
            actionController.delete(999L)

            // Then
            verify(actionRepository).deleteById(999L)
        }

        @Test
        @DisplayName("deveDeletarMultiplasAcoes")
        fun deveDeletarMultiplasAcoes() {
            // When
            actionController.delete(1L)
            actionController.delete(2L)
            actionController.delete(3L)

            // Then
            verify(actionRepository).deleteById(1L)
            verify(actionRepository).deleteById(2L)
            verify(actionRepository).deleteById(3L)
        }
    }
}

