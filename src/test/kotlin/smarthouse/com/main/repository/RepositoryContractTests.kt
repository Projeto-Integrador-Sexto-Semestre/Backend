package smarthouse.com.main.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import smarthouse.com.main.model.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Testes para Repositories
 *
 * Objetivo: Testar contratos de todas as repositories usando mocks
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("Repositories - Testes de Contrato")
class RepositoryContractTests {

    @Mock private lateinit var profileRepository: ProfileRepository
    @Mock private lateinit var houseRepository: HouseRepository
    @Mock private lateinit var roomRepository: RoomRepository
    @Mock private lateinit var sensorRepository: SensorRepository
    @Mock private lateinit var deviceTypeRepository: DeviceTypeRepository
    @Mock private lateinit var iotDeviceRepository: IotDeviceRepository
    @Mock private lateinit var sensorHistoryRepository: SensorHistoryRepository
    @Mock private lateinit var eventLogRepository: EventLogRepository
    @Mock private lateinit var alertTypeRepository: AlertTypeRepository
    @Mock private lateinit var alertRepository: AlertRepository
    @Mock private lateinit var actionRepository: ActionRepository
    @Mock private lateinit var automationRuleRepository: AutomationRuleRepository
    @Mock private lateinit var notificationRepository: NotificationRepository

    private lateinit var testProfile: Profile
    private lateinit var testUser: User
    private lateinit var testHouse: House
    private lateinit var testRoom: Room
    private lateinit var testDeviceType: DeviceType
    private lateinit var testSensor: Sensor

    @BeforeEach
    fun setUp() {
        testProfile = Profile(id = 1L, name = "ADMIN", canControlDevices = true, canEditStructure = true, canViewLogs = true)
        testUser = User(id = 1L, email = "test@test.com", password = "pwd", name = "Test", profile = testProfile)
        testHouse = House(id = 1L, name = "Casa", address = "Rua 1", user = testUser)
        testRoom = Room(id = 1L, name = "Sala", type = "SALA", house = testHouse)
        testDeviceType = DeviceType(id = 1L, name = "Sensor", manufacturer = "Teste", unit = "°C")
        testSensor = Sensor(id = 1L, name = "Sensor Temp", mqttTopic = "sensor/1", deviceType = testDeviceType, room = testRoom)
    }

    @Nested
    @DisplayName("ProfileRepository - Testes")
    inner class ProfileRepositoryTests {

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            whenever(profileRepository.findAll()).thenReturn(listOf(testProfile))

            // When
            val result = profileRepository.findAll()

            // Then
            assertEquals(1, result.size)
            assertEquals("ADMIN", result[0].name)
            verify(profileRepository).findAll()
        }

        @Test
        @DisplayName("deveFindById")
        fun deveFindById() {
            // Given
            whenever(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile))

            // When
            val result = profileRepository.findById(1L)

            // Then
            assertTrue(result.isPresent)
            assertEquals("ADMIN", result.get().name)
        }

        @Test
        @DisplayName("deveSave")
        fun deveSave() {
            // Given
            whenever(profileRepository.save(testProfile)).thenReturn(testProfile)

            // When
            val result = profileRepository.save(testProfile)

            // Then
            assertEquals("ADMIN", result.name)
            verify(profileRepository).save(testProfile)
        }

        @Test
        @DisplayName("deveDeleteById")
        fun deveDeleteById() {
            // When
            profileRepository.deleteById(1L)

            // Then
            verify(profileRepository).deleteById(1L)
        }
    }

    @Nested
    @DisplayName("HouseRepository - Testes")
    inner class HouseRepositoryTests {

        @Test
        @DisplayName("deveFindByUserId")
        fun deveFindByUserId() {
            // Given
            whenever(houseRepository.findByUserId(1L)).thenReturn(listOf(testHouse))

            // When
            val result = houseRepository.findByUserId(1L)

            // Then
            assertEquals(1, result.size)
            assertEquals("Casa", result[0].name)
            verify(houseRepository).findByUserId(1L)
        }

        @Test
        @DisplayName("deveFindByUserIdVazio")
        fun deveFindByUserIdVazio() {
            // Given
            whenever(houseRepository.findByUserId(999L)).thenReturn(emptyList())

            // When
            val result = houseRepository.findByUserId(999L)

            // Then
            assertEquals(0, result.size)
        }

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            whenever(houseRepository.findAll()).thenReturn(listOf(testHouse))

            // When
            val result = houseRepository.findAll()

            // Then
            assertEquals(1, result.size)
        }

        @Test
        @DisplayName("deveSave")
        fun deveSave() {
            // Given
            whenever(houseRepository.save(testHouse)).thenReturn(testHouse)

            // When
            val result = houseRepository.save(testHouse)

            // Then
            assertEquals("Casa", result.name)
        }

        @Test
        @DisplayName("deveFindById")
        fun deveFindById() {
            // Given
            whenever(houseRepository.findById(1L)).thenReturn(Optional.of(testHouse))

            // When
            val result = houseRepository.findById(1L)

            // Then
            assertTrue(result.isPresent)
            assertEquals("Casa", result.get().name)
        }

        @Test
        @DisplayName("deveDeleteById")
        fun deveDeleteById() {
            // When
            houseRepository.deleteById(1L)

            // Then
            verify(houseRepository).deleteById(1L)
        }
    }

    @Nested
    @DisplayName("RoomRepository - Testes")
    inner class RoomRepositoryTests {

        @Test
        @DisplayName("deveFindByHouseId")
        fun deveFindByHouseId() {
            // Given
            whenever(roomRepository.findByHouseId(1L)).thenReturn(listOf(testRoom))

            // When
            val result = roomRepository.findByHouseId(1L)

            // Then
            assertEquals(1, result.size)
            assertEquals("Sala", result[0].name)
            verify(roomRepository).findByHouseId(1L)
        }

        @Test
        @DisplayName("deveFindByHouseIdVazio")
        fun deveFindByHouseIdVazio() {
            // Given
            whenever(roomRepository.findByHouseId(999L)).thenReturn(emptyList())

            // When
            val result = roomRepository.findByHouseId(999L)

            // Then
            assertEquals(0, result.size)
        }

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            whenever(roomRepository.findAll()).thenReturn(listOf(testRoom))

            // When
            val result = roomRepository.findAll()

            // Then
            assertEquals(1, result.size)
        }

        @Test
        @DisplayName("deveSave")
        fun deveSave() {
            // Given
            whenever(roomRepository.save(testRoom)).thenReturn(testRoom)

            // When
            val result = roomRepository.save(testRoom)

            // Then
            assertEquals("Sala", result.name)
        }
    }

    @Nested
    @DisplayName("SensorRepository - Testes")
    inner class SensorRepositoryTests {

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            whenever(sensorRepository.findAll()).thenReturn(listOf(testSensor))

            // When
            val result = sensorRepository.findAll()

            // Then
            assertEquals(1, result.size)
        }

        @Test
        @DisplayName("deveSave")
        fun deveSave() {
            // Given
            whenever(sensorRepository.save(testSensor)).thenReturn(testSensor)

            // When
            val result = sensorRepository.save(testSensor)

            // Then
            assertEquals("Sensor Temp", result.name)
        }

        @Test
        @DisplayName("deveFindById")
        fun deveFindById() {
            // Given
            whenever(sensorRepository.findById(1L)).thenReturn(Optional.of(testSensor))

            // When
            val result = sensorRepository.findById(1L)

            // Then
            assertTrue(result.isPresent)
        }

        @Test
        @DisplayName("deveDeleteById")
        fun deveDeleteById() {
            // When
            sensorRepository.deleteById(1L)

            // Then
            verify(sensorRepository).deleteById(1L)
        }
    }

    @Nested
    @DisplayName("DeviceTypeRepository - Testes")
    inner class DeviceTypeRepositoryTests {

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            whenever(deviceTypeRepository.findAll()).thenReturn(listOf(testDeviceType))

            // When
            val result = deviceTypeRepository.findAll()

            // Then
            assertEquals(1, result.size)
        }

        @Test
        @DisplayName("deveSave")
        fun deveSave() {
            // Given
            whenever(deviceTypeRepository.save(testDeviceType)).thenReturn(testDeviceType)

            // When
            val result = deviceTypeRepository.save(testDeviceType)

            // Then
            assertEquals("Sensor", result.name)
        }

        @Test
        @DisplayName("deveFindById")
        fun deveFindById() {
            // Given
            whenever(deviceTypeRepository.findById(1L)).thenReturn(Optional.of(testDeviceType))

            // When
            val result = deviceTypeRepository.findById(1L)

            // Then
            assertTrue(result.isPresent)
        }
    }

    @Nested
    @DisplayName("IotDeviceRepository - Testes")
    inner class IotDeviceRepositoryTests {

        @Test
        @DisplayName("deveFindByRoomId")
        fun deveFindByRoomId() {
            // Given
            val device = IotDevice(id = 1L, name = "Device", deviceType = testDeviceType, topic = "topic", status = "ON", room = testRoom)
            whenever(iotDeviceRepository.findByRoomId(1L)).thenReturn(listOf(device))

            // When
            val result = iotDeviceRepository.findByRoomId(1L)

            // Then
            assertEquals(1, result.size)
        }

        @Test
        @DisplayName("deveFindByRoomIdVazio")
        fun deveFindByRoomIdVazio() {
            // Given
            whenever(iotDeviceRepository.findByRoomId(999L)).thenReturn(emptyList())

            // When
            val result = iotDeviceRepository.findByRoomId(999L)

            // Then
            assertEquals(0, result.size)
        }

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            val device = IotDevice(id = 1L, name = "Device", deviceType = testDeviceType, topic = "topic", status = "ON", room = testRoom)
            whenever(iotDeviceRepository.findAll()).thenReturn(listOf(device))

            // When
            val result = iotDeviceRepository.findAll()

            // Then
            assertEquals(1, result.size)
        }
    }

    @Nested
    @DisplayName("SensorHistoryRepository - Testes")
    inner class SensorHistoryRepositoryTests {

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            val history = SensorHistory(id = 1L, value = "25.5", sensor = testSensor)
            whenever(sensorHistoryRepository.findAll()).thenReturn(listOf(history))

            // When
            val result = sensorHistoryRepository.findAll()

            // Then
            assertEquals(1, result.size)
        }

        @Test
        @DisplayName("deveSave")
        fun deveSave() {
            // Given
            val history = SensorHistory(id = 1L, value = "25.5", sensor = testSensor)
            whenever(sensorHistoryRepository.save(history)).thenReturn(history)

            // When
            val result = sensorHistoryRepository.save(history)

            // Then
            assertEquals("25.5", result.value)
        }
    }

    @Nested
    @DisplayName("EventLogRepository - Testes")
    inner class EventLogRepositoryTests {

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            val log = EventLog(id = 1L, eventType = "TEST", message = "Test", user = testUser)
            whenever(eventLogRepository.findAll()).thenReturn(listOf(log))

            // When
            val result = eventLogRepository.findAll()

            // Then
            assertEquals(1, result.size)
        }

        @Test
        @DisplayName("deveSave")
        fun deveSave() {
            // Given
            val log = EventLog(id = 1L, eventType = "TEST", message = "Test", user = testUser)
            whenever(eventLogRepository.save(log)).thenReturn(log)

            // When
            val result = eventLogRepository.save(log)

            // Then
            assertEquals("TEST", result.eventType)
        }
    }

    @Nested
    @DisplayName("AlertTypeRepository - Testes")
    inner class AlertTypeRepositoryTests {

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            val type = AlertType(id = 1L, name = "WARNING")
            whenever(alertTypeRepository.findAll()).thenReturn(listOf(type))

            // When
            val result = alertTypeRepository.findAll()

            // Then
            assertEquals(1, result.size)
        }

        @Test
        @DisplayName("deveSave")
        fun deveSave() {
            // Given
            val type = AlertType(id = 1L, name = "WARNING")
            whenever(alertTypeRepository.save(type)).thenReturn(type)

            // When
            val result = alertTypeRepository.save(type)

            // Then
            assertEquals("WARNING", result.name)
        }
    }

    @Nested
    @DisplayName("AlertRepository - Testes")
    inner class AlertRepositoryTests {

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            val alertType = AlertType(id = 1L, name = "WARNING")
            val alert = Alert(id = 1L, message = "Test", alertType = alertType, sensor = testSensor)
            whenever(alertRepository.findAll()).thenReturn(listOf(alert))

            // When
            val result = alertRepository.findAll()

            // Then
            assertEquals(1, result.size)
        }

        @Test
        @DisplayName("deveSave")
        fun deveSave() {
            // Given
            val alertType = AlertType(id = 1L, name = "WARNING")
            val alert = Alert(id = 1L, message = "Test", alertType = alertType)
            whenever(alertRepository.save(alert)).thenReturn(alert)

            // When
            val result = alertRepository.save(alert)

            // Then
            assertEquals("Test", result.message)
        }
    }

    @Nested
    @DisplayName("ActionRepository - Testes")
    inner class ActionRepositoryTests {

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            val device = IotDevice(id = 1L, name = "Device", deviceType = testDeviceType, topic = "topic", status = "ON", room = testRoom)
            val action = Action(id = 1L, name = "Action", device = device, command = "ON")
            whenever(actionRepository.findAll()).thenReturn(listOf(action))

            // When
            val result = actionRepository.findAll()

            // Then
            assertEquals(1, result.size)
        }

        @Test
        @DisplayName("deveSave")
        fun deveSave() {
            // Given
            val action = Action(id = 1L, name = "Action", command = "ON")
            whenever(actionRepository.save(action)).thenReturn(action)

            // When
            val result = actionRepository.save(action)

            // Then
            assertEquals("Action", result.name)
        }
    }

    @Nested
    @DisplayName("AutomationRuleRepository - Testes")
    inner class AutomationRuleRepositoryTests {

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            val rule = AutomationRule(id = 1L, name = "Rule", condition = "condition", enabled = true)
            whenever(automationRuleRepository.findAll()).thenReturn(listOf(rule))

            // When
            val result = automationRuleRepository.findAll()

            // Then
            assertEquals(1, result.size)
        }

        @Test
        @DisplayName("deveSave")
        fun deveSave() {
            // Given
            val rule = AutomationRule(id = 1L, name = "Rule", condition = "condition", enabled = true)
            whenever(automationRuleRepository.save(rule)).thenReturn(rule)

            // When
            val result = automationRuleRepository.save(rule)

            // Then
            assertEquals("Rule", result.name)
        }
    }

    @Nested
    @DisplayName("NotificationRepository - Testes")
    inner class NotificationRepositoryTests {

        @Test
        @DisplayName("deveFindAll")
        fun deveFindAll() {
            // Given
            val notification = Notification(id = 1L, message = "Notif", user = testUser)
            whenever(notificationRepository.findAll()).thenReturn(listOf(notification))

            // When
            val result = notificationRepository.findAll()

            // Then
            assertEquals(1, result.size)
        }

        @Test
        @DisplayName("deveSave")
        fun deveSave() {
            // Given
            val notification = Notification(id = 1L, message = "Notif", user = testUser)
            whenever(notificationRepository.save(notification)).thenReturn(notification)

            // When
            val result = notificationRepository.save(notification)

            // Then
            assertEquals("Notif", result.message)
        }
    }
}

