---

# 💊 Botica Estrella - API REST Backend

Este repositorio contiene la arquitectura backend del **Sistema Integrado de Gestión de Inventarios y Ventas** para la "Botica Estrella". Desarrollado como proyecto central para el 7to ciclo de la carrera de Ingeniería de Sistemas en la Universidad Tecnológica del Perú (UTP).

El sistema ha sido construido bajo una arquitectura en capas, priorizando la mantenibilidad, la escalabilidad y la aplicación de patrones de diseño de software para optimizar las operaciones de una botica moderna.

---

## 🚀 Arquitectura del Sistema

El proyecto sigue una **Arquitectura en Capas (Layered Architecture)**, asegurando la separación de preocupaciones y desacoplando la lógica de negocio de la persistencia de datos.

### Capas Principales:

1. **Controller (`/api/v1/...`):** Capa de exposición que gestiona las peticiones HTTP y la comunicación con el frontend.
2. **Service:** Corazón de la lógica de negocio. Maneja las transacciones (@Transactional) y coordina los flujos de datos.
3. **Repository:** Interfaz de acceso a datos utilizando Spring Data JPA para abstraer las consultas SQL.
4. **Model:** Definición de entidades JPA que mapean el esquema de la base de datos MySQL.

---

## 🏗️ Patrones de Diseño Implementados

Hemos integrado patrones de diseño para resolver problemas comunes de ingeniería de software:

* **Strategy (Pagos):** Permite cambiar dinámicamente entre métodos de pago (Efectivo, Tarjeta, Yape) sin modificar el código del servicio.
* **Factory (Comprobantes):** Encapsula la creación de objetos (Boleta/Factura) eliminando bloques `if/else` innecesarios.
* **Observer (Stock):** Sistema de eventos desacoplado para alertar al personal (Administración/Logística) cuando el inventario alcanza niveles críticos.
* **Adapter (Gateway):** Permite la integración segura con servicios externos (pasarelas de pago) traduciendo sus interfaces a la nuestra.

---

## 📊 Estructura del Proyecto

```text
src/main/java/com/boticaestrella/
├── controlador/     # Endpoints REST (API)
├── servicio/        # Lógica de negocio transaccional
├── repository/      # Interfaces de acceso a datos
├── modelo/          # Entidades JPA (POJOs)
├── patrones/        # Implementación de patrones (Factory, Strategy, etc.)
├── dto/             # Objetos de transferencia de datos (Data Transfer Objects)
└── config/          # Configuración de seguridad y beans

```

---

## 🛠️ Flujo de Trabajo (GitFlow)

Para asegurar la calidad del código, el equipo trabaja bajo reglas estrictas:

1. **Issues:** Toda tarea es gestionada mediante Issues en GitHub.
2. **Ramas:** Se utiliza una rama por cada funcionalidad (`feature/nombre-de-tarea`).
3. **Pull Requests (PR):**
* Cualquier fusión (merge) a la rama `main` **debe ser aprobada** mediante un Pull Request.
* El equipo debe revisar el código antes de aprobar.


4. **Bypass:** El Administrador (Anderson) cuenta con permisos especiales para realizar *bypass* en caso de emergencias críticas, garantizando la continuidad operativa.

---

## 👥 Equipo de Desarrollo

Este proyecto es el resultado del esfuerzo colaborativo de:

* **Anderson B. Urrutia Moreyra** (Design Patterns)
* **Gonzalo Gerardo Correa Arenas**
* **Daniel Ricardo Canchanya Astuñaupa**
* **Joshua Samir Rioja Oroncoy**
* **Cristhian Manual Castro Quiñones**

---

## ⚙️ Requisitos de Instalación

1. **JDK:** Java 21 o superior.
2. **Base de Datos:** MySQL 8.0+.
3. **Configuración:** Configurar `application.properties` con las credenciales de tu base de datos local.
4. **Ejecución:**
```bash
mvn clean install
mvn spring-boot:run

```



> *"La excelencia en ingeniería no es hacer que funcione, es hacer que sea robusto, escalable y mantenible."*

---

*Documentación generada para Botica Estrella - 2026*
