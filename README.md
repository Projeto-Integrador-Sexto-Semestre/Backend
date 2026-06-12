# Smart Home API

Backend do projeto **PI Smart Home**, desenvolvido em Kotlin com Spring Boot. A API centraliza cadastro de usuários, casas, cômodos, dispositivos, sensores, automações, alertas, notificações e histórico de leituras, além de integrar comandos MQTT para comunicação com o ESP32.

## Visão Geral

Este serviço é a camada principal de negócio e persistência da solução. Ele oferece:

- API REST para os 14 CRUDs do sistema;
- autenticação por login e emissão de JWT;
- persistência com Spring Data JPA;
- banco PostgreSQL;
- integração MQTT para enviar comandos ao módulo ESP32;
- estrutura de testes para controllers, services, repositories e modelos.

## Tecnologias

- Kotlin JVM
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT com `jjwt`
- PostgreSQL
- HiveMQ MQTT Client
- Gradle Kotlin DSL
- JUnit 5

## Estrutura

```text
.
|-- build.gradle.kts
|-- Dockerfile
|-- src
|   |-- main
|   |   |-- kotlin/smarthouse/com
|   |   |   |-- main
|   |   |   |   |-- config
|   |   |   |   |-- controller
|   |   |   |   |-- dto
|   |   |   |   |-- model
|   |   |   |   |-- repository
|   |   |   |   `-- secutiry
|   |   |   `-- mqtt
|   |   `-- resources/application.properties
|   `-- test/kotlin/smarthouse/com/main
|       |-- config
|       |-- controller
|       |-- model
|       |-- repository
|       `-- service
`-- RELATORIO_TESTES.md
```

> Observação: o pacote `secutiry` parece conter um erro de grafia, mas foi mantido assim no código.

## Como Rodar

Requisitos:

- Java 17
- PostgreSQL acessível
- Gradle Wrapper do próprio projeto

No Windows:

```bash
gradlew.bat bootRun
```

No Linux/macOS:

```bash
./gradlew bootRun
```

A API sobe por padrão em:

```text
http://localhost:8080
```

Endpoint raiz:

```text
GET /
```

## Configuração

As configurações ficam em `src/main/resources/application.properties`.

Principais propriedades:

```properties
spring.application.name=smart-home-api
server.port=8080

spring.datasource.url=jdbc:postgresql://...
spring.datasource.username=...
spring.datasource.password=...

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=sua-chave-secreta-com-pelo-menos-32-caracteres
jwt.expiration=86400000
```

Para uso real ou público, evite manter credenciais no arquivo versionado. Prefira variáveis de ambiente ou perfis locais do Spring.

## Variáveis de Ambiente Necessárias

| Variável | Descrição | Tipo | Obrigatória |
|---|---|---|---|
| `SPRING_DATASOURCE_URL` | URL do banco de dados PostgreSQL | String | ✅ Sim |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco de dados | String | ✅ Sim |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco de dados | String | ✅ Sim |
| `JWT_SECRET` | Chave secreta para assinar JWT (mín. 32 caracteres) | String | ✅ Sim |
| `JWT_EXPIRATION` | Tempo de expiração do token em ms | Inteiro | ⭕ Não (padrão: 86400000) |
| `MQTT_BROKER` | Endereço do broker MQTT | String | ✅ Sim |
| `MQTT_PORT` | Porta do broker MQTT | Inteiro | ⭕ Não (padrão: 1883) |
| `MQTT_USER` | Usuário MQTT (se necessário) | String | ⭕ Não |
| `MQTT_PASSWORD` | Senha MQTT | String | ✅ Sim |

## Exemplos de Usuários/Senhas para Teste

**Usuários Padrão (para testes):**

| Email | Senha | Propósito |
|---|---|---|
| `usuario@teste.com` | `Teste@123` | Teste básico |
| `admin@teste.com` | `Admin@123` | Teste com privilégios |

**Nota:** Registre novos usuários via endpoint `/users/register`. Os exemplos acima servem apenas para testes; use valores seguros em produção.

**Padrão de Senha Recomendado:**
- Mínimo 8 caracteres
- Incluir maiúsculas, minúsculas, números e caracteres especiais

## Autenticação

### Registrar usuário

```http
POST /users/register
Content-Type: application/json
```

```json
{
  "email": "usuario@email.com",
  "password": "senhaSegura123",
  "name": "Nome do Usuario",
  "profileId": 1
}
```

### Login

```http
POST /users/login
Content-Type: application/json
```

```json
{
  "email": "usuario@email.com",
  "password": "senhaSegura123"
}
```

O login gera um token JWT quando as credenciais são válidas.

## Principais Entidades

| Entidade | Papel |
|---|---|
| `User` | Usuário do sistema |
| `Profile` | Perfil e permissões |
| `House` | Casa vinculada a usuário |
| `Room` | Cômodo de uma casa |
| `DeviceType` | Tipo/categoria de dispositivo |
| `IotDevice` | Dispositivo controlável |
| `Sensor` | Sensor associado a tópico MQTT |
| `SensorHistory` | Histórico de leituras |
| `AlertType` | Categoria de alerta |
| `Alert` | Alerta gerado por sensor/dispositivo |
| `AutomationRule` | Regra de automação |
| `Action` | Ação executada por uma regra |
| `Notification` | Notificação para usuários |
| `EventLog` | Registro de eventos do sistema |

## Endpoints REST

### Usuários

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/users` | Lista usuários |
| `GET` | `/users/{id}` | Busca usuário por ID |
| `POST` | `/users/register` | Registra usuário |
| `POST` | `/users/login` | Autentica usuário |

### Perfis

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/profiles` | Lista perfis |
| `POST` | `/profiles` | Cria perfil |
| `PUT` | `/profiles/{id}` | Atualiza perfil |
| `DELETE` | `/profiles/{id}` | Remove perfil |

### Casas e Cômodos

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/houses` | Lista casas |
| `POST` | `/houses` | Cria casa |
| `GET` | `/houses/user/{userId}` | Lista casas de um usuário |
| `DELETE` | `/houses/{id}` | Remove casa |
| `GET` | `/rooms` | Lista cômodos |
| `POST` | `/rooms` | Cria cômodo |
| `GET` | `/rooms/house/{houseId}` | Lista cômodos de uma casa |
| `DELETE` | `/rooms/{id}` | Remove cômodo |

### Dispositivos e Sensores

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/device-types` | Lista tipos de dispositivo |
| `POST` | `/device-types` | Cria tipo de dispositivo |
| `DELETE` | `/device-types/{id}` | Remove tipo |
| `GET` | `/devices` | Lista dispositivos |
| `POST` | `/devices` | Cria dispositivo |
| `GET` | `/devices/room/{roomId}` | Lista dispositivos por cômodo |
| `PATCH` | `/devices/{id}/status` | Atualiza status do dispositivo |
| `DELETE` | `/devices/{id}` | Remove dispositivo |
| `GET` | `/sensors` | Lista sensores |
| `POST` | `/sensors` | Cria sensor |
| `DELETE` | `/sensors/{id}` | Remove sensor |
| `GET` | `/sensor-history/sensor/{sensorId}` | Lista histórico por sensor |
| `POST` | `/sensor-history` | Registra leitura |

### Alertas, Automações e Monitoramento

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/alert-types` | Lista tipos de alerta |
| `POST` | `/alert-types` | Cria tipo de alerta |
| `DELETE` | `/alert-types/{id}` | Remove tipo de alerta |
| `GET` | `/alerts` | Lista alertas |
| `POST` | `/alerts` | Cria alerta |
| `DELETE` | `/alerts/{id}` | Remove alerta |
| `GET` | `/actions` | Lista ações |
| `POST` | `/actions` | Cria ação |
| `DELETE` | `/actions/{id}` | Remove ação |
| `GET` | `/automation-rules` | Lista regras |
| `POST` | `/automation-rules` | Cria regra |
| `DELETE` | `/automation-rules/{id}` | Remove regra |
| `GET` | `/notifications` | Lista notificações |
| `POST` | `/notifications` | Cria notificação |
| `DELETE` | `/notifications/{id}` | Remove notificação |
| `GET` | `/event-logs` | Lista logs |
| `POST` | `/event-logs` | Cria log |
| `DELETE` | `/event-logs/{id}` | Remove log |

## MQTT

O backend possui um `MqttService` para publicar comandos nos tópicos esperados pelo ESP32.

| Método | Rota | Tópico MQTT | Payload |
|---|---|---|---|
| `POST` | `/mqtt/ventilador/ligar` | `casa/command/ventilador` | `ON` |
| `POST` | `/mqtt/ventilador/desligar` | `casa/command/ventilador` | `OFF` |
| `POST` | `/mqtt/luz/ligar` | `casa/command/led_branco` | `ON` |
| `POST` | `/mqtt/luz/desligar` | `casa/command/led_branco` | `OFF` |
| `POST` | `/mqtt/threshold/temp` | `casa/command/threshold/temp` | valor numérico |
| `POST` | `/mqtt/threshold/ldr` | `casa/command/threshold/ldr` | valor numérico |
| `POST` | `/mqtt/bloqueio/temp` | `casa/command/bloqueio_sensor_temp` | `ON` ou `OFF` |
| `POST` | `/mqtt/bloqueio/luz` | `casa/command/bloqueio_sensor_luz` | `ON` ou `OFF` |

## Testes

Execute:

```bash
./gradlew test
```

No Windows:

```bash
gradlew.bat test
```

O projeto inclui testes para controllers REST, configuração de segurança, repositories, modelos, serviços de telemetria/MQTT e inicialização da aplicação.

## Docker

O repositório possui `Dockerfile`. Um fluxo comum de uso é:

```bash
docker build -t smart-home-api .
docker run --rm -p 8080:8080 smart-home-api
```

Verifique as variáveis de banco antes de executar em container.

## Relação com os Outros Repositórios

```text
IOT      -> envia leituras e recebe comandos via MQTT
Backend  -> persiste dados, autentica usuários e publica comandos MQTT
Web      -> consome a API REST em um painel administrativo
Mobile   -> consome ou simula a mesma API em uma experiência Android
```

## Cuidados de Segurança

- Externalize credenciais de banco e chaves JWT.
- Revise a configuração de segurança antes de produção. O arquivo atual exclui a auto-configuração servlet padrão de segurança.
- Defina uma política de CORS caso o frontend rode em outro domínio.
- Evite retornar entidades com campos sensíveis, especialmente senhas.
