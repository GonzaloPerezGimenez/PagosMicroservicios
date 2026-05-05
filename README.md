# PagosMicroservicios

API de gestión de usuarios y pagos construida con arquitectura de microservicios, Spring Boot, Spring Cloud Gateway, Spring Security, JWT, Spring Data JPA, PostgreSQL y Docker.

El objetivo del proyecto es demostrar una arquitectura backend modular y cercana a un entorno real: autenticación centralizada, comunicación entre servicios, separación de responsabilidades y despliegue con Docker Compose.

---

## Arquitectura

```text
Cliente / Postman
      │
      ▼
API Gateway ── puerto 8080  (único punto de entrada)
      │
      ├── User Service ── red interna :8081
      │       └── Registro, login, gestión de usuarios y emisión de JWT
      │
      └── Payment Service ── red interna :8082
              └── Creación y consulta de pagos entre usuarios
```

> Los puertos 8081 y 8082 **no están expuestos al host**. Solo el Gateway (8080) es accesible desde fuera. Los microservicios se comunican por la red interna de Docker.

### Flujo de autenticación y pago

```mermaid
sequenceDiagram
    autonumber
    actor Cliente as Cliente / Postman
    participant Gateway as API Gateway :8080
    participant User as User Service :8081
    participant Payment as Payment Service :8082
    participant DB as PostgreSQL (Neon)

    Cliente->>Gateway: POST /users/login
    Gateway->>User: Reenvía credenciales
    User->>DB: Valida usuario
    User-->>Gateway: Devuelve JWT
    Gateway-->>Cliente: Token JWT

    Cliente->>Gateway: POST /payments + Bearer Token
    Gateway->>Gateway: Valida JWT
    Gateway->>Payment: Reenvía petición de pago
    Payment->>User: Comprueba usuarios / actualiza balances
    User->>DB: Débito y crédito de balances
    Payment->>DB: Guarda transacción
    Payment-->>Gateway: Resultado del pago
    Gateway-->>Cliente: Respuesta HTTP
```

---

## Tecnologías

- Java 21
- Spring Boot
- Spring Cloud Gateway
- Spring Security + JWT (jjwt)
- Spring Data JPA / Hibernate
- OpenFeign (comunicación entre servicios)
- PostgreSQL (Neon)
- Docker y Docker Compose
- Maven

---

## Requisitos

**Con Docker (recomendado):**
- Docker Desktop o Docker Engine con Docker Compose

**Sin Docker:**
- Java 21
- Maven 3.9+
- PostgreSQL accesible

---

## Inicio rápido con Docker

### 1. Clona el repositorio

```bash
git clone https://github.com/GonzaloPerezGimenez/PagosMicroservicios.git
cd PagosMicroservicios
```

### 2. Crea el archivo `.env`

```bash
cp .env.example .env
```

Abre `.env` y rellena tus credenciales de base de datos. Ver sección [Configuración del entorno](#configuración-del-entorno).

### 3. Arranca todo

```bash
docker compose up --build
```

La API queda disponible en `http://localhost:8080`.

Para detener:

```bash
docker compose down
```

---

## Configuración del entorno

El proyecto usa variables de entorno para no exponer credenciales en el código. El archivo `.env` **nunca debe subirse al repositorio** (está en `.gitignore`).

Copia `.env.example` como `.env` y rellena los valores:

```env
# JWT
JWT_SECRET=vcGaq5k1m0VMQrjqzNoCRtHhS/+HecujQ30kr8PfSXc=

# Base de datos – User Service
SPRING_USERDB_URL=jdbc:postgresql://<host_neon>/users_db?sslmode=require&channel_binding=require
SPRING_DATASOURCE_USERNAME=neondb_owner
SPRING_DATASOURCE_PASSWORD=tu_password

# Base de datos – Payment Service
SPRING_PAYMENTDB_URL=jdbc:postgresql://<host_neon>/db_payments?sslmode=require&channel_binding=require

# No cambiar
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
USER_SERVICE_URL=http://user-service:8081
PAYMENT_SERVICE_URL=http://payment-service:8082
```

---

## Endpoints

Todos los endpoints se consumen desde el Gateway en `http://localhost:8080`.

### Usuarios y autenticación

| Método | Endpoint | Auth | Body | Descripción |
|--------|----------|------|------|-------------|
| `POST` | `/users` | No | `nombre`, `username`, `password` | Registra un nuevo usuario |
| `POST` | `/users/login` | No | `username`, `password` | Login – devuelve JWT |
| `GET` | `/users` | JWT | — | Lista todos los usuarios |
| `GET` | `/users/{id}` | JWT | — | Consulta usuario por ID |
| `PUT` | `/users/{id}/update` | JWT | Campos a actualizar | Actualiza datos de usuario |
| `DELETE` | `/users/{id}` | JWT | — | Elimina (soft delete) un usuario |

### Pagos

| Método | Endpoint | Auth | Body | Descripción |
|--------|----------|------|------|-------------|
| `POST` | `/payments` | JWT | `amount`, `sendId`, `receiveId` | Crea un pago entre dos usuarios |
| `GET` | `/payments` | JWT | — | Lista todos los pagos |
| `GET` | `/payments/{id}` | JWT | — | Pagos asociados a un usuario |
| `GET` | `/payments/users` | JWT | — | Lista usuarios desde Payment Service |
| `GET` | `/payments/users/{id}` | JWT | — | Consulta usuario desde Payment Service |
| `PUT` | `/payments/users/{id}/update` | JWT | Campos a actualizar | Actualiza usuario desde Payment Service |

---

## Prueba rápida

### 1. Crear dos usuarios

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Demo Uno", "username": "demo1", "password": "demo1234"}'

curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Demo Dos", "username": "demo2", "password": "demo1234"}'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"username": "demo1", "password": "demo1234"}'
```

Copia el token JWT devuelto.

### 3. Añadir saldo al usuario 1

```bash
curl -X POST "http://localhost:8080/users/1/credit?amount=100" \
  -H "Authorization: Bearer TU_TOKEN"
```

### 4. Crear un pago

```bash
curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TU_TOKEN" \
  -d '{"amount": 25.50, "sendId": 1, "receiveId": 2}'
```

### 5. Ver pagos del usuario 1

```bash
curl -X GET http://localhost:8080/payments/1 \
  -H "Authorization: Bearer TU_TOKEN"
```

---

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

---

## Buenas prácticas aplicadas

- API Gateway como único punto de entrada
- Autenticación centralizada con JWT
- Microservicios sin puertos expuestos (solo accesibles por red interna Docker)
- Comunicación entre servicios con OpenFeign
- Manejo global de excepciones con `@RestControllerAdvice`
- DTOs para no exponer entidades ni contraseñas
- Soft delete de usuarios con `@SoftDelete`
- Variables de entorno para toda la configuración sensible
- Dockerización independiente por servicio

---

## Demo

¿Quieres probarlo sin configurar base de datos ni variables de entorno?

Escríbeme y te envío las credenciales de demo en menos de 24h:

- 💼 [LinkedIn – Gonzalo Pérez Giménez](https://www.linkedin.com/in/gonzalo-perez-gimenez)
- 📧 [GitHub – GonzaloPerezGimenez](https://github.com/GonzaloPerezGimenez)

---

## Roadmap

- [ ] Colección de Postman
- [ ] Swagger / OpenAPI por servicio
- [ ] Tests unitarios e integración
- [ ] CI con GitHub Actions

---

## Autor

Gonzalo Pérez Giménez · [GitHub](https://github.com/GonzaloPerezGimenez)
