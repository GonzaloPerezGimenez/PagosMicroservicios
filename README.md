# PagosMicroservicios

API de gestión de usuarios y pagos construida con arquitectura de microservicios, Spring Boot, Spring Cloud Gateway, Spring Security, JWT, Spring Data JPA, PostgreSQL y Docker.

El objetivo del proyecto es demostrar una arquitectura backend modular y cercana a un entorno real: autenticación centralizada, comunicación entre servicios, separación de responsabilidades y despliegue con Docker Compose.

## Arquitectura

```text
Cliente / Postman
      │
      ▼
API Gateway - puerto 8080
      │
      ├── User Service - puerto 8081
      │       └── Registro, login y emisión de JWT
      │
      └── Payment Service - puerto 8082
              └── Creación y consulta de pagos
```

| Servicio | Responsabilidad |
| --- | --- |
| `Gateway_Service` | Punto de entrada único. Enruta peticiones hacia los microservicios y valida JWT. |
| `User_Services` | Registro, login, gestión de usuarios y generación de tokens JWT. |
| `Payments_Services` | Gestión de pagos y consulta de transacciones entre usuarios. |

## Tecnologías

- Java 21
- Spring Boot
- Spring Cloud Gateway
- Spring Security
- JWT
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven multi-módulo
- Docker y Docker Compose

## Requisitos

Para ejecutarlo con Docker:

- Docker Desktop o Docker Engine
- Docker Compose
- Una base de datos PostgreSQL accesible, por ejemplo local, Neon, Railway, Supabase, etc.

Para ejecutarlo manualmente:

- Java 21
- Maven 3.9+
- PostgreSQL

## Configuración del entorno

El proyecto usa variables de entorno. Por seguridad, el archivo `.env` real no debe subirse al repositorio.

1. Copia el archivo de ejemplo:

```bash
cp .env.example .env
```

2. Edita `.env` con tus valores reales:

```env
JWT_SECRET=change-me-use-a-long-base64-secret-at-least-32-bytes
SPRING_USERDB_URL=jdbc:postgresql://localhost:5432/users_db
SPRING_PAYMENTDB_URL=jdbc:postgresql://localhost:5432/payments_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
USER_SERVICE_URL=http://user-service:8081
PAYMENT_SERVICE_URL=http://payment-service:8082
```

> Nota: para reclutadores, se recomienda crear credenciales de prueba o usar una base de datos temporal sin datos sensibles.

## Ejecución con Docker Compose

Desde la raíz del proyecto:

```bash
docker compose up --build
```

La API quedará disponible en:

```text
http://localhost:8080
```

Para detener los contenedores:

```bash
docker compose down
```

## Ejecución manual

Compila el proyecto desde la raíz:

```bash
mvn clean install
```

Arranca los servicios en terminales separadas:

```bash
cd User_Services
mvn spring-boot:run
```

```bash
cd Payments_Services
mvn spring-boot:run
```

```bash
cd Gateway_Service
mvn spring-boot:run
```

## Endpoints principales

Todos los endpoints deben consumirse desde el Gateway:

```text
http://localhost:8080
```

### Autenticación

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/users/register` | Registrar un nuevo usuario. |
| `POST` | `/users/login` | Iniciar sesión y obtener JWT. |

### Pagos

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/payments` | Crear un nuevo pago. Requiere JWT. |
| `GET` | `/payments/{id}` | Consultar un pago por ID. Requiere JWT. |
| `GET` | `/payments/user/{userId}` | Listar pagos de un usuario. Requiere JWT. |

## Prueba rápida con Postman o cURL

### 1. Registrar usuario

```bash
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo",
    "password": "demo123"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo",
    "password": "demo123"
  }'
```

Copia el token JWT devuelto por el login.

### 3. Crear pago

```bash
curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN_JWT" \
  -d '{
    "senderUserId": 1,
    "receiverUserId": 2,
    "amount": 25.50
  }'
```

> Los nombres exactos de los campos pueden variar según los DTO actuales del proyecto. Si una petición devuelve `400 Bad Request`, revisa el DTO correspondiente del controlador.

## Estructura del proyecto

```text
PagosMicroservicios/
├── Gateway_Service/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── User_Services/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── Payments_Services/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml
├── .env.example
├── .gitignore
├── README.md
└── pom.xml
```

## Buenas prácticas aplicadas

- Separación por microservicios.
- API Gateway como entrada única.
- Configuración mediante variables de entorno.
- Eliminación de credenciales del código fuente.
- Dockerización independiente por servicio.
- Maven multi-módulo para compilar desde la raíz.

## Seguridad

No se deben subir archivos `.env`, contraseñas, tokens ni URLs privadas con credenciales al repositorio. Si alguna credencial ha sido publicada, debe rotarse inmediatamente y eliminarse del historial de Git.

## Roadmap

- Añadir colección de Postman.
- Añadir Swagger/OpenAPI por servicio.
- Añadir tests unitarios e integración.
- Añadir base de datos local opcional en `docker-compose.yml` para demo completa.
- Añadir CI con GitHub Actions.

## Autor

Gonzalo Pérez Giménez

- GitHub: [GonzaloPerezGimenez](https://github.com/GonzaloPerezGimenez)
