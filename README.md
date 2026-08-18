# Mínimo Producto Viable (MVP): sistema de registro de asistencia de empleados

Aplicación web que permite gestionar la entrada y salida de los trabajadores y trabajadoras de un pequeña empresa dedidacada a la compra y venta de productos químimos. 

## Objetivo
Mejorar la organización interna y asegurar el cumplimiento de las normativas laborales, además de optimizar la administración del tiempo, los recursos humanos y dar disponibilidad de datos para integraciones con otros dispositivos en implementaciones futuras. 

## Tecnologías utilizadas

**Backend**
- Node.js + Express 5
- MySQL (mysql2)
- cookie-parser (manejo de sesión)
- bcrypt (hash de contraseñas)
- dotenv

**Frontend**



## Requisitos previos

- [Node.js](https://nodejs.org/) v18 o superior
- [MySQL](https://www.mysql.com/) v8 o superior 
- Un cliente para administrar la base de datos (MySQL Workbench, DBeaver, línea de comandos, etc.)
- npm (incluido con Node.js)

## Endpoints

### Auth
| Método | Ruta | Acceso | Requerimiento |
|---|---|---|---|
| POST | `/api/auth/login` | público | — |
| POST | `/api/auth/logout` | autenticado | — |
| PUT  | `/api/auth/change-password` | autenticado | — |

### Usuarios
**Acceso solo admin**


| Método | Ruta | Acceso | Requerimiento |
|---|---|---|---|
| GET | `/api/users` | autenticado | — |
| POST | `/api/users` | autenticado| GU-01|
| PUT| `/api/users/:id` | autenticado| GU-02|
| DELETE  | `/api/users/:id` | autenticado | GU-03 |

