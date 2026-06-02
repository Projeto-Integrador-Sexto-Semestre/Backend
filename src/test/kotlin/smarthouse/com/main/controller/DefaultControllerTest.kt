package smarthouse.com.main.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Testes unitários para Default (Health Check) Controller
 *
 * Objetivo: Testar a rota de health check da aplicação
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("Default Controller - Health Check e Informações Básicas")
class DefaultControllerTest {

    private lateinit var defaultController: Default

    @BeforeEach
    fun setUp() {
        defaultController = Default()
    }

    @Test
    @DisplayName("deveRetornarRunningQuandoAplicacaoEstaAtiva")
    fun deveRetornarRunningQuandoAplicacaoEstaAtiva() {
        // When
        val resultado = defaultController.index()

        // Then
        assertNotNull(resultado)
        assertEquals("Running...", resultado)
    }

    @Test
    @DisplayName("deveRetornarStringNaoVazia")
    fun deveRetornarStringNaoVazia() {
        // When
        val resultado = defaultController.index()

        // Then
        assertTrue(resultado.isNotEmpty())
    }

    @Test
    @DisplayName("deveRetornarMesmaMensagemEmMultiplasChamadas")
    fun deveRetornarMesmaMensagemEmMultiplasChamadas() {
        // When
        val resultado1 = defaultController.index()
        val resultado2 = defaultController.index()
        val resultado3 = defaultController.index()

        // Then
        assertEquals(resultado1, resultado2)
        assertEquals(resultado2, resultado3)
    }

    @Test
    @DisplayName("deveRetornarMensagemComRunning")
    fun deveRetornarMensagemComRunning() {
        // When
        val resultado = defaultController.index()

        // Then
        assertTrue(resultado.contains("Running"))
    }

    @Test
    @DisplayName("deveRetornarMensagemRunningComCapitalR")
    fun deveRetornarMensagemRunningComCapitalR() {
        // When
        val resultado = defaultController.index()

        // Then
        assertTrue(resultado.startsWith("Running"))
    }

    @Test
    @DisplayName("deveRetornarStringExata")
    fun deveRetornarStringExata() {
        // When
        val resultado = defaultController.index()

        // Then
        assertEquals("Running...", resultado)
    }
}


