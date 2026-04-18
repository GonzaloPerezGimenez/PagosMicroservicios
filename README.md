# 💳 PagosMicroservicios

Sistema de gestión de pagos entre usuarios registrados, construido con arquitectura de microservicios usando Spring Boot y Spring Cloud.

> ⚠️ **Proyecto en desarrollo activo** — algunas funcionalidades están aún siendo implementadas.

---

## 🏗️ Arquitectura

El sistema está compuesto por tres microservicios independientes orquestados a través de un API Gateway:

```
Cliente
   │
   ▼
Gateway Service  (puerto 8080)
   ├──▶ User Service     (gestión de usuarios y autenticación)
   └──▶ Payment Service  (gestión de pagos y transacciones)
```

| Servicio | Responsabilidad |
|---|---|
| `Gateway_Service` | Punto de entrada único. Enruta peticiones y aplica filtros |
| `User_Services` | Registro, login y gestión de usuarios. Emisión de JWT |
| `Payments_Services` | Creación y consulta de pagos entre usuarios |

---

## 🛠️ Tecnologías

- **Java** + **Spring Boot**
- **Spring Cloud Gateway** — API Gateway y enrutamiento
- **Spring Security + JWT** — Autenticación y autorización
- **Spring Data JPA** — Capa de persistencia
- **PostgreSQL / H2** — Base de datos (H2 para desarrollo, PostgreSQL para producción)
- **Maven multi-módulo** — Gestión del proyecto

---

## 🚀 Cómo ejecutar el proyecto

### Requisitos previos

- Java 17+
- Maven 3.8+
- PostgreSQL (opcional, se puede usar H2 en memoria)

### Pasos

1. Clona el repositorio:
   ```bash
   git clone https://github.com/GonzaloPerezGimenez/PagosMicroservicios.git
   cd PagosMicroservicios
   ```

2. Compila todos los módulos desde la raíz:
   ```bash
   mvn clean install
   ```

3. Arranca cada servicio en terminales separadas (en este orden):

   ```bash
   # 1. User Service
   cd User_Services
   mvn spring-boot:run

   # 2. Payments Service
   cd Payments_Services
   mvn spring-boot:run

   # 3. Gateway
   cd Gateway_Service
   mvn spring-boot:run
   ```

4. La API estará disponible en: `http://localhost:8080`

---

## 📡 Endpoints principales

> Todos los endpoints pasan por el Gateway en `http://localhost:8080`

### Autenticación
| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/users/register` | Registrar nuevo usuario |
| `POST` | `/users/login` | Login y obtención de JWT |

### Pagos *(requiere JWT)*
| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/payments` | Crear un nuevo pago |
| `GET` | `/payments/{id}` | Consultar un pago por ID |
| `GET` | `/payments/user/{userId}` | Listar pagos de un usuario |

---

## 📁 Estructura del proyecto

```
PagosMicroservicios/
├── pom.xml                  # POM raíz (Maven multi-módulo)
├── Gateway_Service/         # API Gateway (Spring Cloud Gateway)
├── User_Services/           # Microservicio de usuarios
└── Payments_Services/       # Microservicio de pagos
```

---

## 🗺️ Roadmap

- [x] Estructura base de microservicios
- [x] API Gateway con enrutamiento
- [x] Autenticación con JWT
- [ ] Comunicación entre servicios (Feign Client)
- [ ] Dockerización del proyecto
- [ ] Tests unitarios e integración
- [ ] Documentación de la API con Swagger/OpenAPI

---

## 👤 Autor

**Gonzalo Pérez Giménez**
[GitHub](https://github.com/GonzaloPerezGimenez)
