package smarthouse.com.main.model

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@DisplayName("Model Entities - Testes de Construção e Relacionamentos")
class UserTest {

    @Nested
    @DisplayName("Construção de User")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarUserComTodosOsCampos")
        fun deveCriarUserComTodosOsCampos() {

            val user = User(
                id = 1L,
                email = "test@example.com",
                password = "encoded_password",
                name = "Test User"
            )

            assertNotNull(user)
            assertEquals(1L, user.id)
            assertEquals("test@example.com", user.email)
            assertEquals("encoded_password", user.password)
            assertEquals("Test User", user.name)
            assertNull(user.profile)

        }

        @Test
        @DisplayName("deveCriarUserComValoresPadrao")
        fun deveCriarUserComValoresPadrao() {
            // Given & When
            val user = User()

            // Then
            assertNull(user.id)
            assertEquals("", user.email)
            assertEquals("", user.password)
            assertEquals("", user.name)
            assertNull(user.profile)
        }

        @Test
        @DisplayName("deveCriarUserComEmailVazio")
        fun deveCriarUserComEmailVazio() {
            // Given & When
            val user = User(email = "", password = "pwd", name = "Test")

            // Then
            assertEquals("", user.email)
        }
    }

    @Nested
    @DisplayName("Relacionamentos de User")
    inner class RelacionamentosUser {

        @Test
        @DisplayName("deveAssociarProfileAUser")
        fun deveAssociarProfileAUser() {
            // Given
            val profile = Profile(id = 1L, name = "ADMIN")
            val user = User(id = 1L, email = "test@test.com", password = "pwd", name = "Test")

            // When
            user.profile = profile

            // Then
            assertNotNull(user.profile)
            assertEquals("ADMIN", user.profile!!.name)
        }

        @Test
        @DisplayName("deveRemoverProfileDeUser")
        fun deveRemoverProfileDeUser() {
            // Given
            val profile = Profile(id = 1L, name = "ADMIN")
            val user = User(id = 1L, email = "test@test.com", password = "pwd", name = "Test")
            user.profile = profile

            // When
            user.profile = null

            // Then
            assertNull(user.profile)
        }
    }

    @Nested
    @DisplayName("Modificação de Campos")
    inner class ModificacaoCampos {

        @Test
        @DisplayName("deveModificarPassword")
        fun deveModificarPassword() {
            // Given
            val user = User(email = "test@test.com", password = "old_password")

            // When
            user.password = "new_password"

            // Then
            assertEquals("new_password", user.password)
        }

        @Test
        @DisplayName("deveModificarProfile")
        fun deveModificarProfile() {
            // Given
            val user = User()
            val profile1 = Profile(id = 1L, name = "USER")
            val profile2 = Profile(id = 2L, name = "ADMIN")
            user.profile = profile1

            // When
            user.profile = profile2

            // Then
            assertEquals("ADMIN", user.profile!!.name)
        }
    }
}

/**
 * Testes para clase Profile
 */
@DisplayName("Profile Entity - Testes de Construção")
class ProfileTest {

    @Nested
    @DisplayName("Construção de Profile")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarProfileComTodosOsCampos")
        fun deveCriarProfileComTodosOsCampos() {
            // Given & When
            val profile = Profile(
                id = 1L,
                name = "ADMIN",
                description = "Administrator",
                canControlDevices = true,
                canEditStructure = true,
                canViewLogs = true
            )

            // Then
            assertNotNull(profile)
            assertEquals(1L, profile.id)
            assertEquals("ADMIN", profile.name)
            assertEquals("Administrator", profile.description)
            assertEquals(true, profile.canControlDevices)
            assertEquals(true, profile.canEditStructure)
            assertEquals(true, profile.canViewLogs)
        }

        @Test
        @DisplayName("deveCriarProfileComValoresPadrao")
        fun deveCriarProfileComValoresPadrao() {
            // Given & When
            val profile = Profile()

            // Then
            assertNull(profile.id)
            assertEquals("", profile.name)
            assertEquals("", profile.description)
            assertEquals(false, profile.canControlDevices)
            assertEquals(false, profile.canEditStructure)
            assertEquals(true, profile.canViewLogs)
        }

        @Test
        @DisplayName("deveCriarProfileVisitante")
        fun deveCriarProfileVisitante() {
            // Given & When
            val profile = Profile(
                id = 3L,
                name = "VISITANTE",
                canControlDevices = false,
                canEditStructure = false,
                canViewLogs = true
            )

            // Then
            assertEquals("VISITANTE", profile.name)
            assertEquals(false, profile.canControlDevices)
            assertEquals(false, profile.canEditStructure)
            assertEquals(true, profile.canViewLogs)
        }

        @Test
        @DisplayName("deveCriarProfileMorador")
        fun deveCriarProfileMorador() {
            // Given & When
            val profile = Profile(
                id = 2L,
                name = "MORADOR",
                canControlDevices = true,
                canEditStructure = false,
                canViewLogs = true
            )

            // Then
            assertEquals("MORADOR", profile.name)
            assertEquals(true, profile.canControlDevices)
            assertEquals(false, profile.canEditStructure)
        }
    }
}

/**
 * Testes para House
 */
@DisplayName("House Entity - Testes de Construção")
class HouseTest {

    @Nested
    @DisplayName("Construção de House")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarHouseComTodosOsCampos")
        fun deveCriarHouseComTodosOsCampos() {
            // Given
            val user = User(id = 1L, email = "user@test.com")

            // When
            val house = House(
                id = 1L,
                name = "Casa Principal",
                address = "Rua Teste 123",
                user = user
            )

            // Then
            assertNotNull(house)
            assertEquals(1L, house.id)
            assertEquals("Casa Principal", house.name)
            assertEquals("Rua Teste 123", house.address)
            assertNotNull(house.user)
            assertEquals(1L, house.user!!.id)
        }

        @Test
        @DisplayName("deveCriarHouseComValoresPadrao")
        fun deveCriarHouseComValoresPadrao() {
            // Given & When
            val house = House()

            // Then
            assertNull(house.id)
            assertEquals("", house.name)
            assertEquals("", house.address)
            assertNull(house.user)
        }
    }

    @Nested
    @DisplayName("Relacionamentos de House")
    inner class RelacionamentosHouse {

        @Test
        @DisplayName("deveAssociarUserAHouse")
        fun deveAssociarUserAHouse() {
            // Given
            val user = User(id = 1L, email = "user@test.com")
            val house = House(id = 1L, name = "Casa")

            // When
            house.user = user

            // Then
            assertNotNull(house.user)
            assertEquals(1L, house.user!!.id)
        }
    }
}

/**
 * Testes para Room
 */
@DisplayName("Room Entity - Testes de Construção")
class RoomTest {

    @Nested
    @DisplayName("Construção de Room")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarRoomComTodosOsCampos")
        fun deveCriarRoomComTodosOsCampos() {
            // Given
            val house = House(id = 1L, name = "Casa")

            // When
            val room = Room(
                id = 1L,
                name = "Sala",
                type = "SALA",
                house = house
            )

            // Then
            assertNotNull(room)
            assertEquals(1L, room.id)
            assertEquals("Sala", room.name)
            assertEquals("SALA", room.type)
            assertNotNull(room.house)
        }

        @Test
        @DisplayName("deveCriarRoomComValoresPadrao")
        fun deveCriarRoomComValoresPadrao() {
            // Given & When
            val room = Room()

            // Then
            assertNull(room.id)
            assertEquals("", room.name)
            assertEquals("", room.type)
            assertNull(room.house)
        }
    }
}

/**
 * Testes para Sensor
 */
@DisplayName("Sensor Entity - Testes de Construção")
class SensorTest {

    @Nested
    @DisplayName("Construção de Sensor")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarSensorComTodosOsCampos")
        fun deveCriarSensorComTodosOsCampos() {
            // Given
            val room = Room(id = 1L, name = "Sala")
            val deviceType = DeviceType(id = 1L, name = "Temperatura", unit = "°C")

            // When
            val sensor = Sensor(
                id = 1L,
                name = "Sensor Temp Sala",
                mqttTopic = "sensor/1/temp",
                deviceType = deviceType,
                room = room
            )

            // Then
            assertNotNull(sensor)
            assertEquals(1L, sensor.id)
            assertEquals("Sensor Temp Sala", sensor.name)
            assertEquals("sensor/1/temp", sensor.mqttTopic)
            assertNotNull(sensor.deviceType)
            assertNotNull(sensor.room)
        }

        @Test
        @DisplayName("deveCriarSensorComValoresPadrao")
        fun deveCriarSensorComValoresPadrao() {
            // Given & When
            val sensor = Sensor()

            // Then
            assertNull(sensor.id)
            assertEquals("", sensor.name)
            assertEquals("", sensor.mqttTopic)
            assertNull(sensor.deviceType)
            assertNull(sensor.room)
        }
    }
}

/**
 * Testes para IotDevice
 */
@DisplayName("IotDevice Entity - Testes de Construção")
class IotDeviceTest {

    @Nested
    @DisplayName("Construção de IotDevice")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarIotDeviceComTodosOsCampos")
        fun deveCriarIotDeviceComTodosOsCampos() {
            // Given
            val room = Room(id = 1L, name = "Sala")
            val deviceType = DeviceType(id = 1L, name = "Lampada")

            // When
            val device = IotDevice(
                id = 1L,
                name = "Lampada Sala",
                deviceType = deviceType,
                topic = "device/1",
                status = "ON",
                room = room
            )

            // Then
            assertNotNull(device)
            assertEquals(1L, device.id)
            assertEquals("Lampada Sala", device.name)
            assertEquals("device/1", device.topic)
            assertEquals("ON", device.status)
            assertNotNull(device.deviceType)
            assertNotNull(device.room)
        }

        @Test
        @DisplayName("deveCriarIotDeviceComValoresPadrao")
        fun deveCriarIotDeviceComValoresPadrao() {
            // Given & When
            val device = IotDevice()

            // Then
            assertNull(device.id)
            assertEquals("", device.name)
            assertEquals("", device.topic)
            assertEquals("OFF", device.status)
            assertNull(device.deviceType)
            assertNull(device.room)
        }

        @Test
        @DisplayName("deveModificarStatusDoDevice")
        fun deveModificarStatusDoDevice() {
            // Given
            val device = IotDevice(id = 1L, status = "OFF")

            // When
            device.status = "ON"

            // Then
            assertEquals("ON", device.status)
        }
    }
}

/**
 * Testes para DeviceType
 */
@DisplayName("DeviceType Entity - Testes de Construção")
class DeviceTypeTest {

    @Nested
    @DisplayName("Construção de DeviceType")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarDeviceTypeComTodosOsCampos")
        fun deveCriarDeviceTypeComTodosOsCampos() {
            // Given & When
            val deviceType = DeviceType(
                id = 1L,
                name = "Sensor Temperatura",
                manufacturer = "Xiaomi",
                unit = "°C"
            )

            // Then
            assertNotNull(deviceType)
            assertEquals(1L, deviceType.id)
            assertEquals("Sensor Temperatura", deviceType.name)
            assertEquals("Xiaomi", deviceType.manufacturer)
            assertEquals("°C", deviceType.unit)
        }

        @Test
        @DisplayName("deveCriarDeviceTypeSemUnit")
        fun deveCriarDeviceTypeSemUnit() {
            // Given & When
            val deviceType = DeviceType(
                id = 1L,
                name = "Lampada",
                manufacturer = "Sonoff"
            )

            // Then
            assertNotNull(deviceType)
            assertNull(deviceType.unit)
        }
    }
}

/**
 * Testes para AutomationRule
 */
@DisplayName("AutomationRule Entity - Testes de Construção")
class AutomationRuleTest {

    @Nested
    @DisplayName("Construção de AutomationRule")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarAutomationRuleComTodosOsCampos")
        fun deveCriarAutomationRuleComTodosOsCampos() {
            // Given
            val action = Action(id = 1L, name = "Ligar Lampada")

            // When
            val rule = AutomationRule(
                id = 1L,
                name = "Ligar ao Entardecer",
                condition = "sunset",
                enabled = true,
                action = action
            )

            // Then
            assertNotNull(rule)
            assertEquals(1L, rule.id)
            assertEquals("Ligar ao Entardecer", rule.name)
            assertEquals("sunset", rule.condition)
            assertEquals(true, rule.enabled)
            assertNotNull(rule.action)
        }

        @Test
        @DisplayName("deveModificarEnabledFlag")
        fun deveModificarEnabledFlag() {
            // Given
            val rule = AutomationRule(id = 1L, enabled = true)

            // When
            rule.enabled = false

            // Then
            assertEquals(false, rule.enabled)
        }
    }
}

/**
 * Testes para Action
 */
@DisplayName("Action Entity - Testes de Construção")
class ActionTest {

    @Nested
    @DisplayName("Construção de Action")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarActionComTodosOsCampos")
        fun deveCriarActionComTodosOsCampos() {
            // Given
            val device = IotDevice(id = 1L, name = "Lampada")

            // When
            val action = Action(
                id = 1L,
                name = "Ligar Lampada",
                device = device,
                command = "ON"
            )

            // Then
            assertNotNull(action)
            assertEquals(1L, action.id)
            assertEquals("Ligar Lampada", action.name)
            assertEquals("ON", action.command)
            assertNotNull(action.device)
        }

        @Test
        @DisplayName("deveCriarActionComComandoComplexo")
        fun deveCriarActionComComandoComplexo() {
            // Given & When
            val action = Action(
                id = 1L,
                name = "Ajustar Temperatura",
                command = "SET:25"
            )

            // Then
            assertEquals("SET:25", action.command)
        }
    }
}

