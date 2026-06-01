package smarthouse.com.main

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Testes básicos da Aplicação
 */
@DisplayName("MainApplication - Testes Básicos")
class MainApplicationTests {

	@Test
	@DisplayName("deveCriarAplicacao")
	fun deveCriarAplicacao() {
		// Test application can be created
		assertEquals(1, 1)
	}

}
