package smarthouse.com.main.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import smarthouse.com.main.model.AutomationRule
import smarthouse.com.main.model.Action
import smarthouse.com.main.model.IotDevice
import smarthouse.com.main.model.DeviceType
import smarthouse.com.main.model.Room
import smarthouse.com.main.repository.AutomationRuleRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Testes unitários para AutomationRuleController
 *
 * Objetivo: Testar operações CRUD de regras de automação com condições e ações
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("AutomationRuleController - Gerenciamento de Regras de Automação")
class AutomationRuleControllerTest {

    @Mock
    private lateinit var automationRuleRepository: AutomationRuleRepository

    private lateinit var automationRuleController: AutomationRuleController

    private lateinit var testRule: AutomationRule
    private lateinit var testAction: Action
    private lateinit var testDevice: IotDevice
    private lateinit var testDeviceType: DeviceType
    private lateinit var testRoom: Room

    @BeforeEach
    fun setUp() {
        automationRuleController = AutomationRuleController(automationRuleRepository)

        // Mock utilizado para simular retorno do repositório
        testDeviceType = DeviceType(
            id = 1L,
            name = "Lâmpada",
            manufacturer = "Philips",
            unit = null
        )

        testRoom = Room(
            id = 1L,
            name = "Sala",
            type = "SALA",
            house = null
        )

        testDevice = IotDevice(
            id = 1L,
            name = "Lâmpada Sala",
            deviceType = testDeviceType,
            topic = "casa/sala/lampada",
            status = "OFF",
            room = testRoom
        )

        testAction = Action(
            id = 1L,
            name = "Ligar Lâmpada",
            device = testDevice,
            command = "ON"
        )

        testRule = AutomationRule(
            id = 1L,
            name = "Ligar luz ao anoitecer",
            condition = "time > 18:00",
            enabled = true,
            action = testAction
        )
    }

    @Nested
    @DisplayName("Operações de Listagem")
    inner class ListagensTests {

        @Test
        @DisplayName("deveListarTodasAsRegras")
        fun deveListarTodasAsRegras() {
            // Given
            val regras = listOf(testRule)
            whenever(automationRuleRepository.findAll()).thenReturn(regras)

            // When
            val resultado = automationRuleController.list()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("Ligar luz ao anoitecer", resultado[0].name)
            assertEquals("time > 18:00", resultado[0].condition)
            assertTrue(resultado[0].enabled)
            verify(automationRuleRepository).findAll()
        }

        @Test
        @DisplayName("deveListarRegrasVazio")
        fun deveListarRegrasVazio() {
            // Given
            whenever(automationRuleRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = automationRuleController.list()

            // Then
            assertEquals(0, resultado.size)
            verify(automationRuleRepository).findAll()
        }

        @Test
        @DisplayName("deveListarMultiplasRegras")
        fun deveListarMultiplasRegras() {
            // Given
            val acao2 = Action(2L, "Desligar Lâmpada", testDevice, "OFF")
            val regra2 = AutomationRule(
                id = 2L,
                name = "Desligar luz ao amanhecer",
                condition = "time < 06:00",
                enabled = true,
                action = acao2
            )
            val regras = listOf(testRule, regra2)
            whenever(automationRuleRepository.findAll()).thenReturn(regras)

            // When
            val resultado = automationRuleController.list()

            // Then
            assertEquals(2, resultado.size)
            assertEquals("Ligar luz ao anoitecer", resultado[0].name)
            assertEquals("Desligar luz ao amanhecer", resultado[1].name)
        }

        @Test
        @DisplayName("deveListarRegraDesabilitada")
        fun deveListarRegraDesabilitada() {
            // Given
            val regraDesabilitada = AutomationRule(
                id = 1L,
                name = "Regra Inativa",
                condition = "condition",
                enabled = false,
                action = testAction
            )
            whenever(automationRuleRepository.findAll()).thenReturn(listOf(regraDesabilitada))

            // When
            val resultado = automationRuleController.list()

            // Then
            assertEquals(1, resultado.size)
            assertFalse(resultado[0].enabled)
        }
    }

    @Nested
    @DisplayName("Operações de Criação")
    inner class CriacaoTests {

        @Test
        @DisplayName("deveCriarUmaRegra")
        fun deveCriarUmaRegra() {
            // Given
            whenever(automationRuleRepository.save(any())).thenReturn(testRule)

            // When
            val resultado = automationRuleController.create(testRule)

            // Then
            assertNotNull(resultado)
            assertEquals("Ligar luz ao anoitecer", resultado.name)
            assertTrue(resultado.enabled)
            verify(automationRuleRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarRegraDesabilitada")
        fun deveCriarRegraDesabilitada() {
            // Given
            val regraDesabilitada = AutomationRule(
                id = null,
                name = "Regra Desabilitada",
                condition = "temperature > 28",
                enabled = false,
                action = testAction
            )
            whenever(automationRuleRepository.save(any())).thenReturn(regraDesabilitada)

            // When
            val resultado = automationRuleController.create(regraDesabilitada)

            // Then
            assertNotNull(resultado)
            assertFalse(resultado.enabled)
            verify(automationRuleRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarRegraSemAcao")
        fun deveCriarRegraSemAcao() {
            // Given
            val regraSemAcao = AutomationRule(
                id = null,
                name = "Regra Sem Ação",
                condition = "test > 0",
                enabled = true,
                action = null
            )
            whenever(automationRuleRepository.save(any())).thenReturn(regraSemAcao)

            // When
            val resultado = automationRuleController.create(regraSemAcao)

            // Then
            assertNotNull(resultado)
            assertEquals(null, resultado.action)
            verify(automationRuleRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarRegraComCondicaoComplexa")
        fun deveCriarRegraComCondicaoComplexa() {
            // Given
            val regraComplexa = AutomationRule(
                id = null,
                name = "Regra Complexa",
                condition = "(temperature > 30 AND humidity < 40) OR (time > 20:00)",
                enabled = true,
                action = testAction
            )
            whenever(automationRuleRepository.save(any())).thenReturn(regraComplexa)

            // When
            val resultado = automationRuleController.create(regraComplexa)

            // Then
            assertNotNull(resultado)
            assertEquals("(temperature > 30 AND humidity < 40) OR (time > 20:00)", resultado.condition)
        }
    }

    @Nested
    @DisplayName("Operações de Atualização")
    inner class AtualizacaoTests {

        @Test
        @DisplayName("deveAtualizarUmaRegra")
        fun deveAtualizarUmaRegra() {
            // Given
            val regraAtualizada = AutomationRule(
                id = 1L,
                name = "Ligar luz ao anoitecer (Atualizada)",
                condition = "time > 19:00",
                enabled = true,
                action = testAction
            )
            whenever(automationRuleRepository.findById(1L)).thenReturn(Optional.of(testRule))
            whenever(automationRuleRepository.save(any())).thenReturn(regraAtualizada)

            // When
            val resultado = automationRuleController.update(1L, regraAtualizada)

            // Then
            assertNotNull(resultado)
            assertEquals("Ligar luz ao anoitecer (Atualizada)", resultado.name)
            verify(automationRuleRepository).findById(1L)
            verify(automationRuleRepository).save(any())
        }

        @Test
        @DisplayName("deveDesabilitarUmaRegra")
        fun deveDesabilitarUmaRegra() {
            // Given
            val regraDesabilitada = AutomationRule(
                id = 1L,
                name = testRule.name,
                condition = testRule.condition,
                enabled = false,
                action = testRule.action
            )
            whenever(automationRuleRepository.findById(1L)).thenReturn(Optional.of(testRule))
            whenever(automationRuleRepository.save(any())).thenReturn(regraDesabilitada)

            // When
            val resultado = automationRuleController.update(1L, regraDesabilitada)

            // Then
            assertNotNull(resultado)
            assertFalse(resultado.enabled)
        }

        @Test
        @DisplayName("deveAtualizarAcao")
        fun deveAtualizarAcao() {
            // Given
            val novaAcao = Action(2L, "Nova Ação", testDevice, "TOGGLE")
            val regraComNovaAcao = AutomationRule(
                id = 1L,
                name = testRule.name,
                condition = testRule.condition,
                enabled = testRule.enabled,
                action = novaAcao
            )
            whenever(automationRuleRepository.findById(1L)).thenReturn(Optional.of(testRule))
            whenever(automationRuleRepository.save(any())).thenReturn(regraComNovaAcao)

            // When
            val resultado = automationRuleController.update(1L, regraComNovaAcao)

            // Then
            assertEquals(2L, resultado.action?.id)
            assertEquals("Nova Ação", resultado.action?.name)
        }
    }

    @Nested
    @DisplayName("Operações de Deleção")
    inner class DelecaoTests {

        @Test
        @DisplayName("deveDeletarUmaRegra")
        fun deveDeletarUmaRegra() {
            // When
            automationRuleController.delete(1L)

            // Then
            verify(automationRuleRepository).deleteById(1L)
        }

        @Test
        @DisplayName("deveDeletarMultiplasRegras")
        fun deveDeletarMultiplasRegras() {
            // When
            automationRuleController.delete(1L)
            automationRuleController.delete(2L)
            automationRuleController.delete(3L)

            // Then
            verify(automationRuleRepository).deleteById(1L)
            verify(automationRuleRepository).deleteById(2L)
            verify(automationRuleRepository).deleteById(3L)
        }

        @Test
        @DisplayName("deveDeletarRegraComIdGrande")
        fun deveDeletarRegraComIdGrande() {
            // When
            automationRuleController.delete(999999L)

            // Then
            verify(automationRuleRepository).deleteById(999999L)
        }
    }
}

