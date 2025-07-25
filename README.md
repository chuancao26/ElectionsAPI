# JustVote - REST API para Votaciones en Línea 🗳️

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![OAuth2](https://img.shields.io/badge/OAuth2-Google-red)](https://developers.google.com/identity/protocols/oauth2)

API desarrollada en **Spring Boot** que permite crear, gestionar y participar en votaciones en línea de forma segura, utilizando autenticación con Google OAuth2.

## 📌 Características Principales
- **Creación y gestión de eventos de votación** (abiertos/cerrados).
- **Autenticación integrada con Google** (OAuth2).
- **CRUD completo** para opciones, votos y participantes.
- **Endpoints RESTful** con validación de permisos (solo creadores pueden modificar sus eventos).
- **Base de datos relacional** (PostgreSQL + JPA/Hibernate).

## 🛠️ Tecnologías
- **Backend**: 
  - Java 17 + Spring Boot
  - Spring Security + OAuth2 (Google) + JWT 
- **Base de datos**: PostgreSQL + JPA/Hibernate
- **Gestión de dependencias**: Maven

---
## 📌 Diagramas del Sistema

### ✅ Diagrama de Casos de Uso
<img src="diagramas/casos_uso.png" alt="Diagrama de casos de uso" width="450px">

---

### ✅ Diagramas de Actividades

#### 🔹 Crear cuenta
<img src="diagramas/crear_cuenta.png" alt="Diagrama de actividad - Crear cuenta" width="450px">

#### 🔹 Iniciar sesión
<img src="diagramas/iniciar_sesion.png" alt="Diagrama de actividad - Iniciar sesión" width="450px">

#### 🔹 Emitir voto
<img src="diagramas/emitir_voto.png" alt="Diagrama de actividad - Emitir voto" width="450px">

---

### ✅ Diagrama de Arquitectura
<img src="diagramas/arquitecture.png" alt="Diagrama de arquitectura" width="450px">

---

### ✅ Diagrama de Clases
<img src="diagramas/clases.png" alt="Diagrama de clases" width="450px">

---

### ✅ Diagrama de Base de Datos
<img src="diagramas/base_datos.png" alt="Diagrama de base de datos" width="450px">

## 📌 Interfaz Gráfica
<img src="diagramas/unnamed (1).png" alt="Interfaz 1" width="450px">

<img src="diagramas/unnamed (2).png" alt="Interfaz 2" width="450px">

<img src="diagramas/unnamed (3).png" alt="Interfaz 3" width="450px">

<img src="diagramas/unnamed (4).png" alt="Interfaz 4" width="450px">

<img src="diagramas/unnamed (5).png" alt="Interfaz 5" width="450px">

<img src="diagramas/unnamed (6).png" alt="Interfaz 6" width="450px">

<img src="diagramas/unnamed (7).png" alt="Interfaz 7" width="450px">

## 📌 Pruebas
