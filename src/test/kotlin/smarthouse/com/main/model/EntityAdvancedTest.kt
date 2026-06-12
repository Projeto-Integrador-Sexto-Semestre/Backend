package smarthouse.com.main.model

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Testes para Alert e AlertType
 */
@DisplayName("Alert Entity - Testes de Construção")
class AlertTest {

    @Nested
    @DisplayName("Construção de Alert")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarAlertComTodosOsCampos")
        fun deveCriarAlertComTodosOsCampos() {
            val now = LocalDateTime.now()
            val sensor = Sensor(id = 1L, name = "Sensor Temp")
            val alertType = AlertType(id = 1L, name = "CRITICAL")

            // When
            val alert = Alert(
                id = 1L,
                message = "Temperatura muito alta",
                timestamp = now,
                acknowledged = false,
                alertType = alertType,
                sensor = sensor
            )

            // Then
            assertNotNull(alert)
            assertEquals(1L, alert.id)
            assertEquals("Temperatura muito alta", alert.message)
            assertEquals(false, alert.acknowledged)
            assertNotNull(alert.alertType)
            assertNotNull(alert.sensor)
        }

        @Test
        @DisplayName("deveModificarAcknowledged")
        fun deveModificarAcknowledged() {
            // Given
            val alert = Alert(id = 1L, acknowledged = false)

            // When
            alert.acknowledged = true

            // Then
            assertEquals(true, alert.acknowledged)
        }

        @Test
        @DisplayName("deveCriarAlertComDevice")
        fun deveCriarAlertComDevice() {
            // Given
            val device = IotDevice(id = 1L, name = "Lampada")

            // When
            val alert = Alert(
                id = 1L,
                message = "Lampada offline",
                device = device
            )

            // Then
            assertNotNull(alert.device)
            assertEquals(1L, alert.device!!.id)
        }
    }
}

@DisplayName("AlertType Entity - Testes de Construção")
class AlertTypeTest {

    @Nested
    @DisplayName("Construção de AlertType")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarAlertTypeComTodosOsCampos")
        fun deveCriarAlertTypeComTodosOsCampos() {
            // Given & When
            val alertType = AlertType(
                id = 1L,
                name = "CRITICAL",
                description = "Alerta crítico"
            )

            // Then
            assertNotNull(alertType)
            assertEquals(1L, alertType.id)
            assertEquals("CRITICAL", alertType.name)
            assertEquals("Alerta crítico", alertType.description)
        }

        @Test
        @DisplayName("deveCriarAlertTypeSemDescricao")
        fun deveCriarAlertTypeSemDescricao() {
            // Given & When
            val alertType = AlertType(
                id = 1L,
                name = "WARNING"
            )

            // Then
            assertNull(alertType.description)
        }
    }
}

/**
 * Testes para Notification
 */
@DisplayName("Notification Entity - Testes de Construção")
class NotificationTest {

    @Nested
    @DisplayName("Construção de Notification")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarNotificationComTodosOsCampos")
        fun deveCriarNotificationComTodosOsCampos() {
            // Given
            val now = LocalDateTime.now()
            val user = User(id = 1L, email = "user@test.com")

            // When
            val notification = Notification(
                id = 1L,
                message = "Novo evento",
                timestamp = now,
                read = false,
                user = user
            )

            // Then
            assertNotNull(notification)
            assertEquals(1L, notification.id)
            assertEquals("Novo evento", notification.message)
            assertEquals(false, notification.read)
            assertNotNull(notification.user)
        }

        @Test
        @DisplayName("deveModificarReadFlag")
        fun deveModificarReadFlag() {
            // Given
            val notification = Notification(id = 1L, read = false)

            // When
            notification.read = true

            // Then
            assertEquals(true, notification.read)
        }

        @Test
        @DisplayName("deveCriarNotificationSemUser")
        fun deveCriarNotificationSemUser() {
            // Given & When
            val notification = Notification(id = 1L, message = "Notificação")

            // Then
            assertNull(notification.user)
        }
    }
}

/**
 * Testes para EventLog
 */
@DisplayName("EventLog Entity - Testes de Construção")
class EventLogTest {

    @Nested
    @DisplayName("Construção de EventLog")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarEventLogComTodosOsCampos")
        fun deveCriarEventLogComTodosOsCampos() {
            // Given
            val now = LocalDateTime.now()
            val user = User(id = 1L, email = "user@test.com")

            // When
            val event = EventLog(
                id = 1L,
                eventType = "SENSOR_UPDATE",
                message = "Sensor 1 atualizado",
                timestamp = now,
                user = user
            )

            // Then
            assertNotNull(event)
            assertEquals(1L, event.id)
            assertEquals("SENSOR_UPDATE", event.eventType)
            assertEquals("Sensor 1 atualizado", event.message)
            assertNotNull(event.user)
        }

        @Test
        @DisplayName("deveCriarEventLogSemUser")
        fun deveCriarEventLogSemUser() {
            // Given & When
            val event = EventLog(
                id = 1L,
                eventType = "SYSTEM_START",
                message = "Sistema iniciado"
            )

            // Then
            assertNull(event.user)
        }
    }
}

/**
 * Testes para SensorHistory
 */
@DisplayName("SensorHistory Entity - Testes de Construção")
class SensorHistoryTest {

    @Nested
    @DisplayName("Construção de SensorHistory")
    inner class ConstructorTests {

        @Test
        @DisplayName("deveCriarSensorHistoryComTodosOsCampos")
        fun deveCriarSensorHistoryComTodosOsCampos() {
            // Given
            val now = LocalDateTime.now()
            val sensor = Sensor(id = 1L, name = "Sensor Temp")

            // When
            val history = SensorHistory(
                id = 1L,
                value = "25.5",
                timestamp = now,
                sensor = sensor
            )

            // Then
            assertNotNull(history)
            assertEquals(1L, history.id)
            assertEquals("25.5", history.value)
            assertNotNull(history.sensor)
        }

        @Test
        @DisplayName("deveCriarMultiplasHistoricas")
        fun deveCriarMultiplasHistoricas() {
            // Given
            val sensor = Sensor(id = 1L, name = "Sensor")

            // When
            val h1 = SensorHistory(id = 1L, value = "20.0", sensor = sensor)
            val h2 = SensorHistory(id = 2L, value = "21.0", sensor = sensor)
            val h3 = SensorHistory(id = 3L, value = "22.0", sensor = sensor)

            // Then
            assertEquals("20.0", h1.value)
            assertEquals("21.0", h2.value)
            assertEquals("22.0", h3.value)
        }

        @Test
        @DisplayName("deveArmazenarHistoricoComValoresAltos")
        fun deveArmazenarHistoricoComValoresAltos() {
            // Given & When
            val history = SensorHistory(
                id = 1L,
                value = "999.99"
            )

            // Then
            assertEquals("999.99", history.value)
        }

        @Test
        @DisplayName("deveArmazenarHistoricoComValoresNegativos")
        fun deveArmazenarHistoricoComValoresNegativos() {
            // Given & When
            val history = SensorHistory(
                id = 1L,
                value = "-5.5"
            )

            // Then
            assertEquals("-5.5", history.value)
        }
    }
}

/**
 * Testes para Relacionamentos complexos
 */
@DisplayName("Entity Relationships - Testes de Relacionamentos Complexos")
class EntityRelationshipsTest {

    @Nested
    @DisplayName("Teste de relacionamento cascata User -> House -> Room -> Sensor")
    inner class RelacionamentosCascata {

        @Test
        @DisplayName("deveCriarHierarquiaCompleta")
        fun deveCriarHierarquiaCompleta() {
            // Given
            val user = User(id = 1L, email = "user@test.com", name = "User")
            val house = House(id = 1L, name = "Casa", user = user)
            val room = Room(id = 1L, name = "Sala", house = house)
            val sensor = Sensor(id = 1L, name = "Sensor", room = room)

            // When & Then
            assertNotNull(sensor.room)
            assertNotNull(sensor.room!!.house)
            assertNotNull(sensor.room!!.house!!.user)
            assertEquals("user@test.com", sensor.room!!.house!!.user!!.email)
        }

        @Test
        @DisplayName("devePermitirMudancaDePropertyEmCascata")
        fun devePermitirMudancaDePropertyEmCascata() {
            // Given
            val user1 = User(id = 1L, email = "user1@test.com")
            val user2 = User(id = 2L, email = "user2@test.com")
            val house = House(id = 1L, name = "Casa", user = user1)

            // When
            house.user = user2

            // Then
            assertEquals("user2@test.com", house.user!!.email)
        }
    }

    @Nested
    @DisplayName("Teste com valores null em relacionamentos")
    inner class RelacionamentosNull {

        @Test
        @DisplayName("deveCriarRoomSemHouse")
        fun deveCriarRoomSemHouse() {
            // Given & When
            val room = Room(id = 1L, name = "Sala")

            // Then
            assertNull(room.house)
        }

        @Test
        @DisplayName("deveCriarSensorSemRoom")
        fun deveCriarSensorSemRoom() {
            // Given & When
            val sensor = Sensor(id = 1L, name = "Sensor")

            // Then
            assertNull(sensor.room)
        }

        @Test
        @DisplayName("deveCriarSensorSemDeviceType")
        fun deveCriarSensorSemDeviceType() {
            // Given & When
            val sensor = Sensor(id = 1L, name = "Sensor")

            // Then
            assertNull(sensor.deviceType)
        }

        @Test
        @DisplayName("deveCriarIotDeviceSemDeviceType")
        fun deveCriarIotDeviceSemDeviceType() {
            // Given & When
            val device = IotDevice(id = 1L, name = "Device")

            // Then
            assertNull(device.deviceType)
        }
    }
}

