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
import smarthouse.com.main.model.House
import smarthouse.com.main.model.User
import smarthouse.com.main.repository.HouseRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Testes unitários para HouseController
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("HouseController - Gerenciamento de Casas")
class HouseControllerTest {

    @Mock
    private lateinit var houseRepository: HouseRepository

    private lateinit var houseController: HouseController

    private lateinit var testUser: User
    private lateinit var testHouse: House

    @BeforeEach
    fun setUp() {
        houseController = HouseController(houseRepository)

        testUser = User(id = 1L, email = "user@test.com", name = "Test User")

        testHouse = House(
            id = 1L,
            name = "Casa Principal",
            address = "Rua Teste 123",
            user = testUser
        )
    }

    @Nested
    @DisplayName("Operações de Listagem")
    inner class OperacoesListagem {

        @Test
        @DisplayName("deveListarTodasAsCasas")
        fun deveListarTodasAsCasas() {
            // Given
            whenever(houseRepository.findAll()).thenReturn(listOf(testHouse))

            // When
            val resultado = houseController.listAll()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("Casa Principal", resultado[0].name)
        }

        @Test
        @DisplayName("deveListarCasasVazio")
        fun deveListarCasasVazio() {
            // Given
            whenever(houseRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = houseController.listAll()

            // Then
            assertEquals(0, resultado.size)
        }

        @Test
        @DisplayName("deveListarMultiplasCasas")
        fun deveListarMultiplasCasas() {
            // Given
            val casa2 = House(2L, "Casa Secundária", "Rua Teste 456", testUser)
            val casa3 = House(3L, "Casa de Praia", "Avenida Litoral", testUser)
            whenever(houseRepository.findAll()).thenReturn(listOf(testHouse, casa2, casa3))

            // When
            val resultado = houseController.listAll()

            // Then
            assertEquals(3, resultado.size)
            assertEquals("Casa Principal", resultado[0].name)
            assertEquals("Casa Secundária", resultado[1].name)
            assertEquals("Casa de Praia", resultado[2].name)
        }

        @Test
        @DisplayName("deveListarCasasComEnderecosDiferentes")
        fun deveListarCasasComEnderecosDiferentes() {
            // Given
            val casa2 = House(2L, "Casa 2", "Rua 2", testUser)
            whenever(houseRepository.findAll()).thenReturn(listOf(testHouse, casa2))

            // When
            val resultado = houseController.listAll()

            // Then
            assertEquals("Rua Teste 123", resultado[0].address)
            assertEquals("Rua 2", resultado[1].address)
        }
    }

    @Nested
    @DisplayName("Operações de Busca")
    inner class OperacoesBusca {

        @Test
        @DisplayName("deveListarHousesPorUser")
        fun deveListarHousesPorUser() {
            // Given
            whenever(houseRepository.findByUserId(1L)).thenReturn(listOf(testHouse))

            // When
            val resultado = houseController.listByUser(1L)

            // Then
            assertEquals(1, resultado.size)
            assertEquals("Casa Principal", resultado[0].name)
        }

        @Test
        @DisplayName("deveListarHousesPorUserVazio")
        fun deveListarHousesPorUserVazio() {
            // Given
            whenever(houseRepository.findByUserId(999L)).thenReturn(emptyList())

            // When
            val resultado = houseController.listByUser(999L)

            // Then
            assertEquals(0, resultado.size)
        }

        @Test
        @DisplayName("deveListarMultiplasHousesPorUser")
        fun deveListarMultiplasHousesPorUser() {
            // Given
            val casa2 = House(2L, "Casa 2", "Endereço 2", testUser)
            val casa3 = House(3L, "Casa 3", "Endereço 3", testUser)
            whenever(houseRepository.findByUserId(1L)).thenReturn(listOf(testHouse, casa2, casa3))

            // When
            val resultado = houseController.listByUser(1L)

            // Then
            assertEquals(3, resultado.size)
            for (house in resultado) {
                assertEquals(1L, house.user?.id)
            }
        }

        @Test
        @DisplayName("deveListarHousesPorUserDiferente")
        fun deveListarHousesPorUserDiferente() {
            // Given
            val outroUser = User(2L, "outro@test.com", "Outro User")
            val caseDoOutroUser = House(5L, "Casa do Outro", "Endereço Outro", outroUser)
            whenever(houseRepository.findByUserId(2L)).thenReturn(listOf(caseDoOutroUser))

            // When
            val resultado = houseController.listByUser(2L)

            // Then
            assertEquals(1, resultado.size)
            assertEquals(2L, resultado[0].user?.id)
        }
    }

    @Nested
    @DisplayName("Operações de Persistência")
    inner class OperacoesPersistencia {

        @Test
        @DisplayName("deveCriarHouse")
        fun deveCriarHouse() {
            // Given
            whenever(houseRepository.save(any<House>())).thenReturn(testHouse)

            // When
            val resultado = houseController.create(testHouse)

            // Then
            assertNotNull(resultado)
            assertEquals("Casa Principal", resultado.name)
            assertEquals("Rua Teste 123", resultado.address)
        }

        @Test
        @DisplayName("deveCriarMultiplasHouses")
        fun deveCriarMultiplasHouses() {
            // Given
            val casa2 = House(2L, "Casa 2", "Endereço 2", testUser)
            val casa3 = House(3L, "Casa 3", "Endereço 3", testUser)
            whenever(houseRepository.save(any()))
                .thenReturn(testHouse)
                .thenReturn(casa2)
                .thenReturn(casa3)

            // When
            val res1 = houseController.create(testHouse)
            val res2 = houseController.create(casa2)
            val res3 = houseController.create(casa3)

            // Then
            assertEquals("Casa Principal", res1.name)
            assertEquals("Casa 2", res2.name)
            assertEquals("Casa 3", res3.name)
        }

        @Test
        @DisplayName("deveCriarHouseComEnderecoLongo")
        fun deveCriarHouseComEnderecoLongo() {
            // Given
            val addressLongo = "Rua Principal, 123, Apto 456, Bairro Importante, Cidade Grande, Estado - CEP 12345-678"
            val houseEndereco = House(null, "Casa Endereço Longo", addressLongo, testUser)
            whenever(houseRepository.save(any())).thenReturn(houseEndereco)

            // When
            val resultado = houseController.create(houseEndereco)

            // Then
            assertEquals(addressLongo, resultado.address)
        }

        @Test
        @DisplayName("deveDeleteHouse")
        fun deveDeleteHouse() {
            // Given & When
            houseController.delete(1L)

            // Then
            verify(houseRepository).deleteById(1L)
        }

        @Test
        @DisplayName("deveDeletarMultiplasHouses")
        fun deveDeletarMultiplasHouses() {
            // When
            houseController.delete(1L)
            houseController.delete(2L)
            houseController.delete(3L)

            // Then
            verify(houseRepository).deleteById(1L)
            verify(houseRepository).deleteById(2L)
            verify(houseRepository).deleteById(3L)
        }
    }
}


