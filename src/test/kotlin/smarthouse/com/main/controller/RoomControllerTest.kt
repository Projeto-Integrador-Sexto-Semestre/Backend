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
import smarthouse.com.main.model.Room
import smarthouse.com.main.model.User
import smarthouse.com.main.repository.RoomRepository
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Testes unitários para RoomController
 *
 * Objetivo: Testar operações CRUD de salas
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("RoomController - Gerenciamento de Salas")
class RoomControllerTest {

    @Mock
    private lateinit var roomRepository: RoomRepository

    private lateinit var roomController: RoomController

    private lateinit var testHouse: House
    private lateinit var testRoom: Room

    @BeforeEach
    fun setUp() {
        roomController = RoomController(roomRepository)

        testHouse = House(
            id = 1L,
            name = "Casa Principal",
            address = "Rua Teste 123",
            user = User(id = 1L, email = "user@test.com")
        )

        testRoom = Room(
            id = 1L,
            name = "Sala",
            type = "SALA",
            house = testHouse
        )
    }

    @Nested
    @DisplayName("Operações de Listagem")
    inner class OperacoesListagem {

        @Test
        @DisplayName("deveListarTodosAsRooms")
        fun deveListarTodosAsRooms() {
            // Given
            whenever(roomRepository.findAll()).thenReturn(listOf(testRoom))

            // When
            val resultado = roomController.listAll()

            // Then
            assertEquals(1, resultado.size)
            assertEquals("Sala", resultado[0].name)
            verify(roomRepository).findAll()
        }

        @Test
        @DisplayName("deveListarRoomsVazio")
        fun deveListarRoomsVazio() {
            // Given
            whenever(roomRepository.findAll()).thenReturn(emptyList())

            // When
            val resultado = roomController.listAll()

            // Then
            assertEquals(0, resultado.size)
        }

        @Test
        @DisplayName("deveListarMultiplasRooms")
        fun deveListarMultiplasRooms() {
            // Given
            val room2 = Room(id = 2L, name = "Quarto", type = "QUARTO", house = testHouse)
            val room3 = Room(id = 3L, name = "Cozinha", type = "COZINHA", house = testHouse)
            whenever(roomRepository.findAll()).thenReturn(listOf(testRoom, room2, room3))

            // When
            val resultado = roomController.listAll()

            // Then
            assertEquals(3, resultado.size)
            assertEquals("Sala", resultado[0].name)
            assertEquals("Quarto", resultado[1].name)
            assertEquals("Cozinha", resultado[2].name)
        }

        @Test
        @DisplayName("deveListarRoomsPorHouse")
        fun deveListarRoomsPorHouse() {
            // Given
            whenever(roomRepository.findByHouseId(1L)).thenReturn(listOf(testRoom))

            // When
            val resultado = roomController.listByHouse(1L)

            // Then
            assertEquals(1, resultado.size)
            verify(roomRepository).findByHouseId(1L)
        }

        @Test
        @DisplayName("deveListarRoomsPorHouseVazio")
        fun deveListarRoomsPorHouseVazio() {
            // Given
            whenever(roomRepository.findByHouseId(2L)).thenReturn(emptyList())

            // When
            val resultado = roomController.listByHouse(2L)

            // Then
            assertEquals(0, resultado.size)
        }

        @Test
        @DisplayName("deveListarMultiplasRoomsPorHouse")
        fun deveListarMultiplasRoomsPorHouse() {
            // Given
            val room2 = Room(id = 2L, name = "Quarto", type = "QUARTO", house = testHouse)
            val room3 = Room(id = 3L, name = "Cozinha", type = "COZINHA", house = testHouse)
            whenever(roomRepository.findByHouseId(1L)).thenReturn(listOf(testRoom, room2, room3))

            // When
            val resultado = roomController.listByHouse(1L)

            // Then
            assertEquals(3, resultado.size)
            // Verifica que todas as salas pertencem à mesma casa
            for (room in resultado) {
                assertEquals(1L, room.house?.id)
            }
        }

        @Test
        @DisplayName("deveListarRoomsPorHouseComIdsEspecificos")
        fun deveListarRoomsPorHouseComIdsEspecificos() {
            // Given
            val room2 = Room(id = 5L, name = "Garagem", type = "GARAGEM", house = testHouse)
            whenever(roomRepository.findByHouseId(1L)).thenReturn(listOf(room2))

            // When
            val resultado = roomController.listByHouse(1L)

            // Then
            assertEquals(1, resultado.size)
            assertEquals(5L, resultado[0].id)
        }
    }

    @Nested
    @DisplayName("Operações de Persistência")
    inner class OperacoesPersistencia {

        @Test
        @DisplayName("deveCreateRoom")
        fun deveCreateRoom() {
            // Given
            whenever(roomRepository.save(any<Room>())).thenReturn(testRoom)

            // When
            val resultado = roomController.create(testRoom)

            // Then
            assertNotNull(resultado)
            assertEquals("Sala", resultado.name)
            verify(roomRepository).save(any<Room>())
        }

        @Test
        @DisplayName("deveCriarMultiplasRooms")
        fun deveCriarMultiplasRooms() {
            // Given
            val room2 = Room(id = 2L, name = "Quarto", type = "QUARTO", house = testHouse)
            val room3 = Room(id = 3L, name = "Cozinha", type = "COZINHA", house = testHouse)
            whenever(roomRepository.save(any()))
                .thenReturn(testRoom)
                .thenReturn(room2)
                .thenReturn(room3)

            // When
            val res1 = roomController.create(testRoom)
            val res2 = roomController.create(room2)
            val res3 = roomController.create(room3)

            // Then
            assertEquals("Sala", res1.name)
            assertEquals("Quarto", res2.name)
            assertEquals("Cozinha", res3.name)
        }

        @Test
        @DisplayName("deveCriarRoomComTipoDiferente")
        fun deveCriarRoomComTipoDiferente() {
            // Given
            val quarto = Room(id = null, name = "Quarto", type = "QUARTO", house = testHouse)
            whenever(roomRepository.save(any())).thenReturn(quarto)

            // When
            val resultado = roomController.create(quarto)

            // Then
            assertEquals("QUARTO", resultado.type)
        }

        @Test
        @DisplayName("deveCriarRoomComNomeDiferente")
        fun deveCriarRoomComNomeDiferente() {
            // Given
            val room = Room(id = null, name = "Garagem Subterrânea", type = "GARAGEM", house = testHouse)
            whenever(roomRepository.save(any())).thenReturn(room)

            // When
            val resultado = roomController.create(room)

            // Then
            assertEquals("Garagem Subterrânea", resultado.name)
        }

        @Test
        @DisplayName("deveDeleteRoom")
        fun deveDeleteRoom() {
            // Given & When
            roomController.delete(1L)

            // Then
            verify(roomRepository).deleteById(1L)
        }

        @Test
        @DisplayName("deveDeletarMultiplasRooms")
        fun deveDeletarMultiplasRooms() {
            // When
            roomController.delete(1L)
            roomController.delete(2L)
            roomController.delete(3L)

            // Then
            verify(roomRepository).deleteById(1L)
            verify(roomRepository).deleteById(2L)
            verify(roomRepository).deleteById(3L)
        }
    }
}

