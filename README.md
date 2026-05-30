# Documentação da API - SmartHouse

Esta documentação descreve os endpoints da API, incluindo exemplos de JSON para requisições.

---

## 1. Profile
- **GET /profiles**: Lista todos os perfis.
- **POST /profiles**: Cria um novo perfil.
    - **JSON Request**:
    ```json
    {
        "name": "ADMIN",
        "description": "Acesso total ao sistema",
        "canControlDevices": true,
        "canEditStructure": true,
        "canViewLogs": true
    }
    ```
- **PUT /profiles/{id}**: Atualiza um perfil.
- **DELETE /profiles/{id}**: Remove um perfil.

## 2. Room
- **GET /rooms**: Lista todos os cômodos.
- **POST /rooms**: Cria um novo cômodo.
    - **JSON Request**:
    ```json
    {
        "name": "Sala de Estar",
        "type": "SALA",
        "house": { "id": 1 }
    }
    ```
- **GET /rooms/house/{houseId}**: Lista cômodos de uma casa.
- **DELETE /rooms/{id}**: Remove um cômodo.

## 3. Sensor
- **GET /sensors**: Lista todos os sensores.
- **POST /sensors**: Cria um novo sensor.
    - **JSON Request**:
    ```json
    {
        "name": "Sensor Temperatura",
        "mqttTopic": "casa/sala/temp",
        "deviceType": { "id": 1 },
        "room": { "id": 1 }
    }
    ```
- **DELETE /sensors/{id}**: Remove um sensor.

## 4. SensorHistory
- **GET /sensor-history/sensor/{sensorId}**: Lista histórico.
- **POST /sensor-history**: Registra leitura.
    - **JSON Request**:
    ```json
    {
        "value": "25.5",
        "timestamp": "2026-05-30T16:20:00",
        "sensor": { "id": 1 }
    }
    ```

## 5. User
- **GET /users**: Lista todos os usuários.
- **GET /users/{id}**: Busca usuário por ID.
- **POST /users/register**: Registra novo usuário.
    - **JSON Request**:
    ```json
    {
        "email": "usuario@email.com",
        "password": "senhaSegura123",
        "name": "Nome do Usuário",
        "profile": { "id": 1 }
    }
    ```
- **POST /users/login**: Login.
    - **JSON Request**:
    ```json
    { "email": "usuario@email.com", "password": "senhaSegura123" }
    ```

## 6. DeviceType
- **GET /device-types**: Lista tipos.
- **POST /device-types**: Cria novo tipo.
    - **JSON Request**:
    ```json
    {
        "name": "Lâmpada LED",
        "manufacturer": "Xiaomi",
        "unit": "Lumens"
    }
    ```
- **DELETE /device-types/{id}**: Remove tipo.

## 7. EventLog
- **GET /event-logs**: Lista logs.
- **POST /event-logs**: Cria log.
    - **JSON Request**:
    ```json
    {
        "eventType": "LOGIN",
        "message": "Usuário logou no sistema",
        "timestamp": "2026-05-30T16:20:00",
        "user": { "id": 1 }
    }
    ```

## 8. House
- **GET /houses**: Lista casas.
- **POST /houses**: Cria casa.
    - **JSON Request**:
    ```json
    {
        "name": "Casa da Praia",
        "address": "Av. Beira Mar, 100",
        "user": { "id": 1 }
    }
    ```

## 9. IotDevice
- **GET /devices**: Lista dispositivos.
- **POST /devices**: Cria dispositivo.
    - **JSON Request**:
    ```json
    {
        "name": "Lâmpada Sala",
        "deviceType": { "id": 1 },
        "topic": "casa/sala/lamp",
        "status": "OFF",
        "room": { "id": 1 }
    }
    ```
- **PATCH /devices/{id}/status**: Atualiza status.
    - **JSON Request**: `{ "status": "ON" }`

## 10. Notification
- **POST /notifications**: Cria notificação.
    - **JSON Request**:
    ```json
    {
        "message": "Alerta de movimento na garagem!",
        "timestamp": "2026-05-30T16:20:00",
        "read": false,
        "user": { "id": 1 }
    }
    ```

## 11. Action
- **POST /actions**: Cria ação.
    - **JSON Request**:
    ```json
    {
        "name": "Ligar Luzes",
        "device": { "id": 1 },
        "command": "ON"
    }
    ```

## 12. Alert
- **POST /alerts**: Cria alerta.
    - **JSON Request**:
    ```json
    {
        "message": "Temperatura alta detectada",
        "acknowledged": false,
        "alertType": { "id": 1 },
        "sensor": { "id": 1 }
    }
    ```

## 13. AutomationRule
- **POST /automation-rules**: Cria regra.
    - **JSON Request**:
    ```json
    {
        "name": "Regra Temperatura",
        "condition": "temperature > 30",
        "enabled": true,
        "action": { "id": 1 }
    }
    ```