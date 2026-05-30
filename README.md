# Documentação da API - SmartHouse

Esta documentação descreve os endpoints da API, organizados por entidade, incluindo os parâmetros necessários para as requisições baseados nos modelos (models) do sistema.

---

## 1. Profile
Gerenciamento de níveis de acesso e permissões de usuários.

- **GET /profiles**: Lista todos os perfis cadastrados.
- **POST /profiles**: Cria um novo perfil.
    - **Body**: 
        - `name` (String, obrigatório, único)
        - `description` (String)
        - `canControlDevices` (Boolean)
        - `canEditStructure` (Boolean)
        - `canViewLogs` (Boolean)
- **PUT /profiles/{id}**: Atualiza um perfil existente.
- **DELETE /profiles/{id}**: Remove um perfil.

## 2. Room
Gerenciamento de cômodos vinculados a uma casa.

- **GET /rooms**: Lista todos os cômodos.
- **POST /rooms**: Cria um novo cômodo.
    - **Body**:
        - `name` (String, obrigatório)
        - `type` (String)
        - `house` (Objeto `House`, obrigatório)
- **GET /rooms/house/{houseId}**: Lista todos os cômodos de uma casa específica.
- **DELETE /rooms/{id}**: Remove um cômodo.

## 3. Sensor
Gestão de dispositivos de sensoriamento.

- **GET /sensors**: Lista todos os sensores.
- **POST /sensors**: Cria um novo sensor.
    - **Body**:
        - `name` (String, obrigatório)
        - `mqttTopic` (String)
        - `deviceType` (Objeto `DeviceType`)
        - `room` (Objeto `Room`, obrigatório)
- **DELETE /sensors/{id}**: Remove um sensor.

## 4. SensorHistory
Registro de leituras históricas dos sensores.

- **GET /sensor-history/sensor/{sensorId}**: Lista o histórico de um sensor ordenado por timestamp decrescente.
- **POST /sensor-history**: Registra uma nova leitura.
    - **Body**:
        - `value` (String, obrigatório)
        - `timestamp` (LocalDateTime)
        - `sensor` (Objeto `Sensor`, obrigatório)

## 5. User
Gerenciamento de contas de usuário e autenticação.

- **POST /users/register**: Registra um novo usuário com senha criptografada (BCrypt).
    - **Body**:
        - `email` (String, obrigatório, único)
        - `password` (String, obrigatório)
        - `name` (String)
        - `profile` (Objeto `Profile`)
- **POST /users/login**: Autenticação de usuário.
    - **Body**: Map contendo `email` e `password`.

## 6. DeviceType
Categorização de dispositivos.

- **GET /device-types**: Lista todos os tipos de dispositivos.
- **POST /device-types**: Cria um novo tipo de dispositivo.
    - **Body**:
        - `name` (String, obrigatório, único)
        - `manufacturer` (String)
        - `unit` (String)
- **DELETE /device-types/{id}**: Remove um tipo de dispositivo.

## 7. EventLog
Logs de eventos do sistema.

- **GET /event-logs**: Lista todos os logs.
- **POST /event-logs**: Cria um novo registro de log.
    - **Body**:
        - `eventType` (String)
        - `message` (String)
        - `timestamp` (LocalDateTime)
        - `user` (Objeto `User`)
- **DELETE /event-logs/{id}**: Remove um log.

## 8. House
Gerenciamento de residências.

- **GET /houses**: Lista todas as casas.
- **POST /houses**: Cria uma nova casa.
    - **Body**:
        - `name` (String, obrigatório)
        - `address` (String)
        - `user` (Objeto `User`, obrigatório)
- **GET /houses/user/{userId}**: Lista casas vinculadas a um usuário.
- **DELETE /houses/{id}**: Remove uma casa.

## 9. IotDevice
Dispositivos IoT ativos.

- **GET /devices**: Lista todos os dispositivos.
- **POST /devices**: Cria um novo dispositivo.
    - **Body**:
        - `name` (String, obrigatório)
        - `deviceType` (Objeto `DeviceType`, obrigatório)
        - `topic` (String)
        - `status` (String, default: "OFF")
        - `room` (Objeto `Room`, obrigatório)
- **PATCH /devices/{id}/status**: Atualiza o status de um dispositivo.
    - **Body**: Map contendo a chave `status`.
- **DELETE /devices/{id}**: Remove um dispositivo.

## 10. Notification
Gerenciamento de avisos ao usuário.

- **GET /notifications**: Lista todas as notificações.
- **POST /notifications**: Cria uma notificação.
    - **Body**:
        - `message` (String)
        - `timestamp` (LocalDateTime)
        - `read` (Boolean)
        - `user` (Objeto `User`)
- **PUT /notifications/{id}**: Atualiza uma notificação.
- **DELETE /notifications/{id}**: Remove uma notificação.

## 11. Action
Ações configuráveis para dispositivos.

- **GET /actions**: Lista todas as ações.
- **POST /actions**: Cria uma nova ação.
    - **Body**:
        - `name` (String)
        - `device` (Objeto `IotDevice`)
        - `command` (String)
- **PUT /actions/{id}**: Atualiza uma ação.
- **DELETE /actions/{id}**: Remove uma ação.

## 12. Alert / AlertType
Gestão de alertas de sistema.

- **GET /alerts**: Lista todos os alertas.
- **POST /alerts**: Cria um novo alerta.
    - **Body**:
        - `message` (String)
        - `timestamp` (LocalDateTime)
        - `acknowledged` (Boolean)
        - `alertType` (Objeto `AlertType`)
        - `sensor` (Objeto `Sensor`, opcional)
        - `device` (Objeto `IotDevice`, opcional)
- **GET /alert-types**: Lista tipos de alerta.

## 13. AutomationRule
Regras de automação.

- **GET /automation-rules**: Lista todas as regras.
- **POST /automation-rules**: Cria uma nova regra.
    - **Body**:
        - `name` (String)
        - `condition` (String, ex: "temperature > 30")
        - `enabled` (Boolean)
        - `action` (Objeto `Action`)
- **PUT /automation-rules/{id}**: Atualiza uma regra.

## 14. Default
- **GET /**: Retorna "Running..." (Health Check).
