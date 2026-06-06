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
import smarthouse.com.main.model.AlertType
import smarthouse.com.main.repository.AlertTypeRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Testes unitários para AlertTypeController
 *
 * Objetivo: Testar operações CRUD básicas de tipos de alerta
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("AlertTypeController - Gerenciamento de Tipos de Alerta")
class AlertTypeControllerTest {

    @Mock
    private lateinit var alertTypeRepository: AlertTypeRepository

    private lateinit var alertTypeController: AlertTypeController

    private lateinit var testAlertType: AlertType

    @BeforeEach
    fun setUp() {
        alertTypeController = AlertTypeController(alertTypeRepository)

        // Mock utilizado para simular retorno do repositório
        testAlertType = AlertType(
            id = 1L,
            name = "TEMPERATURA_ALTA",
            description = "Alerta quando temperatura ultrapassa limite"
        )
    }

    @Nested
    @DisplayName("Operações de Listagem")
    inner class ListagensTests {

        @Test
        @DisplayName("deveListarTodosTiposDeAlerta")
        fun deveListarTodosTiposDeAlerta() {
            // Given
            val tiposAlerta = listOf(testAlertType)
            whenever(alertTypeRepository.findAll()).thenReturn(tiposAlerta)

            // When
            val resultado = alertTypeController.list()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("TEMPERATURA_ALTA", resultado[0].name)
            assertEquals("Alerta quando temperatura ultrapassa limite", resultado[0].description)
            verify(alertTypeRepository).findAll()
        }

        @Test
        @DisplayName("deveListarTiposAlertaVazio")
        fun deveListarTiposAlertaVazio() {
            // Given
            whenever(alertTypeRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = alertTypeController.list()

            // Then
            assertEquals(0, resultado.size)
            verify(alertTypeRepository).findAll()
        }

        @Test
        @DisplayName("deveListarMultiplosTiposDeAlerta")
        fun deveListarMultiplosTiposDeAlerta() {
            // Given
            val tipoAlerta2 = AlertType(
                id = 2L,
                name = "TEMPERATURA_BAIXA",
                description = "Alerta quando temperatura está muito baixa"
            )
            val tipoAlerta3 = AlertType(
                id = 3L,
                name = "UMIDADE_ALTA",
                description = "Alerta de alta umidade"
            )
            val tiposAlerta = listOf(testAlertType, tipoAlerta2, tipoAlerta3)
            whenever(alertTypeRepository.findAll()).thenReturn(tiposAlerta)

            // When
            val resultado = alertTypeController.list()

            // Then
            assertEquals(3, resultado.size)
            assertEquals("TEMPERATURA_ALTA", resultado[0].name)
            assertEquals("TEMPERATURA_BAIXA", resultado[1].name)
            assertEquals("UMIDADE_ALTA", resultado[2].name)
        }

        @Test
        @DisplayName("deveListarTipoAlertaSemDescricao")
        fun deveListarTipoAlertaSemDescricao() {
            // Given
            val tipoAlerta = AlertType(
                id = 1L,
                name = "ALERTA_SIMPLES",
                description = null
            )
            whenever(alertTypeRepository.findAll()).thenReturn(listOf(tipoAlerta))

            // When
            val resultado = alertTypeController.list()

            // Then
            assertEquals(1, resultado.size)
            assertNull(resultado[0].description)
        }
    }

    @Nested
    @DisplayName("Operações de Criação")
    inner class CriacaoTests {

        @Test
        @DisplayName("deveCriarUmTipoDeAlerta")
        fun deveCriarUmTipoDeAlerta() {
            // Given
            whenever(alertTypeRepository.save(any())).thenReturn(testAlertType)

            // When
            val resultado = alertTypeController.create(testAlertType)

            // Then
            assertNotNull(resultado)
            assertEquals("TEMPERATURA_ALTA", resultado.name)
            assertEquals("Alerta quando temperatura ultrapassa limite", resultado.description)
            verify(alertTypeRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarTipoAlertaSemDescricao")
        fun deveCriarTipoAlertaSemDescricao() {
            // Given
            val tipoAlerta = AlertType(
                id = null,
                name = "ALERTA_SIMPLES",
                description = null
            )
            whenever(alertTypeRepository.save(any())).thenReturn(tipoAlerta)

            // When
            val resultado = alertTypeController.create(tipoAlerta)

            // Then
            assertNotNull(resultado)
            assertNull(resultado.description)
            verify(alertTypeRepository).save(any())
        }

        @Test
        @DisplayName("deveCriarMultiplosTiposDeAlerta")
        fun deveCriarMultiplosTiposDeAlerta() {
            // Given
            val tipoAlerta1 = AlertType(1L, "TIPO_1", "Descrição 1")
            val tipoAlerta2 = AlertType(2L, "TIPO_2", "Descrição 2")
            whenever(alertTypeRepository.save(any()))
                .thenReturn(tipoAlerta1)
                .thenReturn(tipoAlerta2)

            // When
            val resultado1 = alertTypeController.create(tipoAlerta1)
            val resultado2 = alertTypeController.create(tipoAlerta2)

            // Then
            assertEquals("TIPO_1", resultado1.name)
            assertEquals("TIPO_2", resultado2.name)
            verify(alertTypeRepository).save(tipoAlerta1)
            verify(alertTypeRepository).save(tipoAlerta2)
        }

        @Test
        @DisplayName("deveCriarTipoAlertaComIDNull")
        fun deveCriarTipoAlertaComIDNull() {
            // Given
            val tipoAlerta = AlertType(
                id = null,
                name = "NOVO_TIPO",
                description = "Novo tipo"
            )
            val tipoAlertaSalvo = AlertType(
                id = 10L,
                name = "NOVO_TIPO",
                description = "Novo tipo"
            )
            whenever(alertTypeRepository.save(any())).thenReturn(tipoAlertaSalvo)

            // When
            val resultado = alertTypeController.create(tipoAlerta)

            // Then
            assertNotNull(resultado.id)
            assertEquals(10L, resultado.id)
        }
    }

    @Nested
    @DisplayName("Operações de Deleção")
    inner class DelecaoTests {

        @Test
        @DisplayName("deveDeletarUmTipoDeAlerta")
        fun deveDeletarUmTipoDeAlerta() {
            // When
            alertTypeController.delete(1L)

            // Then
            verify(alertTypeRepository).deleteById(1L)
        }

        @Test
        @DisplayName("deveDeletarMultiplosTiposDeAlerta")
        fun deveDeletarMultiplosTiposDeAlerta() {
            // When
            alertTypeController.delete(1L)
            alertTypeController.delete(2L)
            alertTypeController.delete(3L)

            // Then
            verify(alertTypeRepository).deleteById(1L)
            verify(alertTypeRepository).deleteById(2L)
            verify(alertTypeRepository).deleteById(3L)
        }

        @Test
        @DisplayName("deveDeletarTipoAlertaComIdGrande")
        fun deveDeletarTipoAlertaComIdGrande() {
            // When
            alertTypeController.delete(999999999L)

            // Then
            verify(alertTypeRepository).deleteById(999999999L)
        }
    }
}

