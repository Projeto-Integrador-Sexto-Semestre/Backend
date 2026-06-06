package smarthouse.com.main.dto

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Testes para DTOs (Data Transfer Objects)
 *
 * Objetivo: Testar criação, serialização e validação de todos os DTOs
 */
@DisplayName("DTO - Testes de Validação e Construção")
class DTOValidationTest {

    @Nested
    @DisplayName("RegisterUserRequest - Testes")
    inner class RegisterUserRequestTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given & When
            val dto = RegisterUserRequest(
                email = "test@example.com",
                password = "password123",
                name = "Test User",
                profileId = 1L
            )

            // Then
            assertEquals("test@example.com", dto.email)
            assertEquals("password123", dto.password)
            assertEquals("Test User", dto.name)
            assertEquals(1L, dto.profileId)
        }

        @Test
        @DisplayName("devePermitirEmailVazio")
        fun devePermitirEmailVazio() {
            // Given & When
            val dto = RegisterUserRequest(
                email = "",
                password = "pwd",
                name = "User",
                profileId = 1L
            )

            // Then
            assertEquals("", dto.email)
        }

        @Test
        @DisplayName("devePermitirSenhaVazia")
        fun devePermitirSenhaVazia() {
            // Given & When
            val dto = RegisterUserRequest(
                email = "test@test.com",
                password = "",
                name = "User",
                profileId = 1L
            )

            // Then
            assertEquals("", dto.password)
        }

        @Test
        @DisplayName("devePermitirNomeVazio")
        fun devePermitirNomeVazio() {
            // Given & When
            val dto = RegisterUserRequest(
                email = "test@test.com",
                password = "pwd",
                name = "",
                profileId = 1L
            )

            // Then
            assertEquals("", dto.name)
        }

        @Test
        @DisplayName("devePermitirProfileIdZero")
        fun devePermitirProfileIdZero() {
            // Given & When
            val dto = RegisterUserRequest(
                email = "test@test.com",
                password = "pwd",
                name = "User",
                profileId = 0L
            )

            // Then
            assertEquals(0L, dto.profileId)
        }

        @Test
        @DisplayName("devePermitirProfileIdNegativo")
        fun devePermitirProfileIdNegativo() {
            // Given & When
            val dto = RegisterUserRequest(
                email = "test@test.com",
                password = "pwd",
                name = "User",
                profileId = -1L
            )

            // Then
            assertEquals(-1L, dto.profileId)
        }
    }

    @Nested
    @DisplayName("UserResponse - Testes")
    inner class UserResponseTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given
            val profileResponse = ProfileResponse(
                id = 1L,
                name = "ADMIN",
                canControlDevices = true,
                canEditStructure = true,
                canViewLogs = true
            )

            // When
            val dto = UserResponse(
                id = 1L,
                email = "test@example.com",
                name = "Test User",
                profile = profileResponse
            )

            // Then
            assertEquals(1L, dto.id)
            assertEquals("test@example.com", dto.email)
            assertEquals("Test User", dto.name)
            assertNotNull(dto.profile)
            assertEquals("ADMIN", dto.profile!!.name)
        }

        @Test
        @DisplayName("devePermitirProfileNull")
        fun devePermitirProfileNull() {
            // Given & When
            val dto = UserResponse(
                id = 1L,
                email = "test@example.com",
                name = "Test User",
                profile = null
            )

            // Then
            assertNull(dto.profile)
        }

        @Test
        @DisplayName("devePermitirIdNull")
        fun devePermitirIdNull() {
            // Given & When
            val dto = UserResponse(
                id = null,
                email = "test@example.com",
                name = "Test User",
                profile = null
            )

            // Then
            assertNull(dto.id)
        }

        @Test
        @DisplayName("devePermitirEmailVazio")
        fun devePermitirEmailVazio() {
            // Given & When
            val dto = UserResponse(
                id = 1L,
                email = "",
                name = "Test User",
                profile = null
            )

            // Then
            assertEquals("", dto.email)
        }
    }

    @Nested
    @DisplayName("ProfileResponse - Testes")
    inner class ProfileResponseTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given & When
            val dto = ProfileResponse(
                id = 1L,
                name = "ADMIN",
                canControlDevices = true,
                canEditStructure = true,
                canViewLogs = true
            )

            // Then
            assertEquals(1L, dto.id)
            assertEquals("ADMIN", dto.name)
            assertEquals(true, dto.canControlDevices)
            assertEquals(true, dto.canEditStructure)
            assertEquals(true, dto.canViewLogs)
        }

        @Test
        @DisplayName("devePermitirIdNull")
        fun devePermitirIdNull() {
            // Given & When
            val dto = ProfileResponse(
                id = null,
                name = "ADMIN",
                canControlDevices = true,
                canEditStructure = false,
                canViewLogs = true
            )

            // Then
            assertNull(dto.id)
        }

        @Test
        @DisplayName("devePermitirPermissoesVariadas")
        fun devePermitirPermissoesVariadas() {
            // Given & When
            val dto = ProfileResponse(
                id = 1L,
                name = "MORADOR",
                canControlDevices = true,
                canEditStructure = false,
                canViewLogs = true
            )

            // Then
            assertEquals(true, dto.canControlDevices)
            assertEquals(false, dto.canEditStructure)
            assertEquals(true, dto.canViewLogs)
        }
    }

    @Nested
    @DisplayName("CreateRoomRequest - Testes")
    inner class CreateRoomRequestTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given & When
            val dto = CreateRoomRequest(
                name = "Sala de Estar",
                type = "SALA",
                houseId = 1L
            )

            // Then
            assertEquals("Sala de Estar", dto.name)
            assertEquals("SALA", dto.type)
            assertEquals(1L, dto.houseId)
        }

        @Test
        @DisplayName("devePermitirNomeVazio")
        fun devePermitirNomeVazio() {
            // Given & When
            val dto = CreateRoomRequest(name = "", type = "SALA", houseId = 1L)

            // Then
            assertEquals("", dto.name)
        }

        @Test
        @DisplayName("devePermitirTipoVazio")
        fun devePermitirTipoVazio() {
            // Given & When
            val dto = CreateRoomRequest(name = "Sala", type = "", houseId = 1L)

            // Then
            assertEquals("", dto.type)
        }
    }

    @Nested
    @DisplayName("CreateHouseRequest - Testes")
    inner class CreateHouseRequestTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given & When
            val dto = CreateHouseRequest(
                name = "Casa Principal",
                address = "Rua Principal 123",
                userId = 1L
            )

            // Then
            assertEquals("Casa Principal", dto.name)
            assertEquals("Rua Principal 123", dto.address)
            assertEquals(1L, dto.userId)
        }

        @Test
        @DisplayName("devePermitirAddressVazio")
        fun devePermitirAddressVazio() {
            // Given & When
            val dto = CreateHouseRequest(name = "Casa", address = "", userId = 1L)

            // Then
            assertEquals("", dto.address)
        }
    }

    @Nested
    @DisplayName("CreateSensorRequest - Testes")
    inner class CreateSensorRequestTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given & When
            val dto = CreateSensorRequest(
                name = "Sensor Temperatura",
                mqttTopic = "casa/sensor/temp",
                deviceTypeId = 1L,
                roomId = 1L
            )

            // Then
            assertEquals("Sensor Temperatura", dto.name)
            assertEquals("casa/sensor/temp", dto.mqttTopic)
            assertEquals(1L, dto.deviceTypeId)
            assertEquals(1L, dto.roomId)
        }

        @Test
        @DisplayName("devePermitirMqttTopicVazio")
        fun devePermitirMqttTopicVazio() {
            // Given & When
            val dto = CreateSensorRequest(name = "Sensor", mqttTopic = "", deviceTypeId = 1L, roomId = 1L)

            // Then
            assertEquals("", dto.mqttTopic)
        }
    }

    @Nested
    @DisplayName("CreateSensorHistoryRequest - Testes")
    inner class CreateSensorHistoryRequestTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given & When
            val dto = CreateSensorHistoryRequest(
                value = "25.5",
                sensorId = 1L
            )

            // Then
            assertEquals("25.5", dto.value)
            assertEquals(1L, dto.sensorId)
        }

        @Test
        @DisplayName("devePermitirValueVazio")
        fun devePermitirValueVazio() {
            // Given & When
            val dto = CreateSensorHistoryRequest(value = "", sensorId = 1L)

            // Then
            assertEquals("", dto.value)
        }

        @Test
        @DisplayName("devePermitirSensorIdZero")
        fun devePermitirSensorIdZero() {
            // Given & When
            val dto = CreateSensorHistoryRequest(value = "25.5", sensorId = 0L)

            // Then
            assertEquals(0L, dto.sensorId)
        }
    }

    @Nested
    @DisplayName("CreateEventLogRequest - Testes")
    inner class CreateEventLogRequestTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given & When
            val dto = CreateEventLogRequest(
                eventType = "USER_LOGIN",
                message = "Usuário fez login",
                userId = 1L
            )

            // Then
            assertEquals("USER_LOGIN", dto.eventType)
            assertEquals("Usuário fez login", dto.message)
            assertEquals(1L, dto.userId)
        }

        @Test
        @DisplayName("devePermitirEventTypeVazio")
        fun devePermitirEventTypeVazio() {
            // Given & When
            val dto = CreateEventLogRequest(eventType = "", message = "msg", userId = 1L)

            // Then
            assertEquals("", dto.eventType)
        }

        @Test
        @DisplayName("devePermitirMessageVazio")
        fun devePermitirMessageVazio() {
            // Given & When
            val dto = CreateEventLogRequest(eventType = "TYPE", message = "", userId = 1L)

            // Then
            assertEquals("", dto.message)
        }
    }

    @Nested
    @DisplayName("CreateNotificationRequest - Testes")
    inner class CreateNotificationRequestTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given & When
            val dto = CreateNotificationRequest(
                message = "Nova notificação",
                userId = 1L
            )

            // Then
            assertEquals("Nova notificação", dto.message)
            assertEquals(1L, dto.userId)
        }

        @Test
        @DisplayName("devePermitirMessageVazio")
        fun devePermitirMessageVazio() {
            // Given & When
            val dto = CreateNotificationRequest(message = "", userId = 1L)

            // Then
            assertEquals("", dto.message)
        }
    }

    @Nested
    @DisplayName("CreateIotDeviceRequest - Testes")
    inner class CreateIotDeviceRequestTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given & When
            val dto = CreateIotDeviceRequest(
                name = "Lâmpada LED",
                deviceTypeId = 1L,
                topic = "casa/lamp/1",
                status = "ON",
                roomId = 1L
            )

            // Then
            assertEquals("Lâmpada LED", dto.name)
            assertEquals(1L, dto.deviceTypeId)
            assertEquals("casa/lamp/1", dto.topic)
            assertEquals("ON", dto.status)
            assertEquals(1L, dto.roomId)
        }

        @Test
        @DisplayName("devePermitirStatusOFF")
        fun devePermitirStatusOFF() {
            // Given & When
            val dto = CreateIotDeviceRequest(
                name = "Device",
                deviceTypeId = 1L,
                topic = "topic",
                status = "OFF",
                roomId = 1L
            )

            // Then
            assertEquals("OFF", dto.status)
        }
    }

    @Nested
    @DisplayName("CreateAlertRequest - Testes")
    inner class CreateAlertRequestTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given & When
            val dto = CreateAlertRequest(
                message = "Alerta de temperatura",
                alertTypeId = 1L,
                sensorId = 1L,
                deviceId = null
            )

            // Then
            assertEquals("Alerta de temperatura", dto.message)
            assertEquals(1L, dto.alertTypeId)
            assertEquals(1L, dto.sensorId)
            assertNull(dto.deviceId)
        }

        @Test
        @DisplayName("devePermitirSensorIdNull")
        fun devePermitirSensorIdNull() {
            // Given & When
            val dto = CreateAlertRequest(
                message = "Alerta",
                alertTypeId = 1L,
                sensorId = null,
                deviceId = 1L
            )

            // Then
            assertNull(dto.sensorId)
            assertEquals(1L, dto.deviceId)
        }

        @Test
        @DisplayName("devePermitirDeviceIdNull")
        fun devePermitirDeviceIdNull() {
            // Given & When
            val dto = CreateAlertRequest(
                message = "Alerta",
                alertTypeId = 1L,
                sensorId = 1L,
                deviceId = null
            )

            // Then
            assertEquals(1L, dto.sensorId)
            assertNull(dto.deviceId)
        }

        @Test
        @DisplayName("devePermitirAmbosNulls")
        fun devePermitirAmbosNulls() {
            // Given & When
            val dto = CreateAlertRequest(
                message = "Alerta",
                alertTypeId = 1L,
                sensorId = null,
                deviceId = null
            )

            // Then
            assertNull(dto.sensorId)
            assertNull(dto.deviceId)
        }
    }

    @Nested
    @DisplayName("CreateActionRequest - Testes")
    inner class CreateActionRequestTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given & When
            val dto = CreateActionRequest(
                name = "Ligar Luz",
                deviceId = 1L,
                command = "ON"
            )

            // Then
            assertEquals("Ligar Luz", dto.name)
            assertEquals(1L, dto.deviceId)
            assertEquals("ON", dto.command)
        }

        @Test
        @DisplayName("devePermitirCommandComParametros")
        fun devePermitirCommandComParametros() {
            // Given & When
            val dto = CreateActionRequest(
                name = "Setbrilho",
                deviceId = 1L,
                command = "SET:50"
            )

            // Then
            assertEquals("SET:50", dto.command)
        }
    }

    @Nested
    @DisplayName("CreateAutomationRuleRequest - Testes")
    inner class CreateAutomationRuleRequestTests {

        @Test
        @DisplayName("deveCriarComTodosCampos")
        fun deveCriarComTodosCampos() {
            // Given & When
            val dto = CreateAutomationRuleRequest(
                name = "Regra Temperatura",
                condition = "temperature > 30",
                enabled = true,
                actionId = 1L
            )

            // Then
            assertEquals("Regra Temperatura", dto.name)
            assertEquals("temperature > 30", dto.condition)
            assertEquals(true, dto.enabled)
            assertEquals(1L, dto.actionId)
        }

        @Test
        @DisplayName("devePermitirEnabledFalse")
        fun devePermitirEnabledFalse() {
            // Given & When
            val dto = CreateAutomationRuleRequest(
                name = "Regra",
                condition = "condition",
                enabled = false,
                actionId = 1L
            )

            // Then
            assertEquals(false, dto.enabled)
        }

        @Test
        @DisplayName("devePermitirConditionComplexo")
        fun devePermitirConditionComplexo() {
            // Given & When
            val dto = CreateAutomationRuleRequest(
                name = "Regra Complexa",
                condition = "(temperature > 30 AND humidity < 50) OR light > 1000",
                enabled = true,
                actionId = 1L
            )

            // Then
            assertEquals("(temperature > 30 AND humidity < 50) OR light > 1000", dto.condition)
        }
    }
}

