# Mínimo Producto Viable (MVP): sistema de registro de asistencia de empleados

Aplicación web que permite gestionar la entrada y salida de los trabajadores y trabajadoras de un pequeña empresa dedidacada a la compra y venta de productos químimos. 

## Objetivo
Mejorar la organización interna y asegurar el cumplimiento de las normativas laborales, además de optimizar la administración del tiempo, los recursos humanos y dar disponibilidad de datos para integraciones con otros dispositivos en implementaciones futuras. 

## Tecnologías utilizadas

**Backend**
- Node.js + Express 5
- MySQL (mysql2)
- JWT para autenticación
- cookie-parser (manejo de sesión)
- bcrypt (hash de contraseñas)
- express-rate-limit para protección contra fuerza bruta

**Frontend**



## Requisitos previos

- [Node.js](https://nodejs.org/) v18 o superior
- [MySQL](https://www.mysql.com/) v8 o superior 
- Un cliente para administrar la base de datos (MySQL Workbench, DBeaver, línea de comandos, etc.)
- npm (incluido con Node.js)


## Backend


### Instalación

1. Entra a la carpeta del backend:

   ```bash
   cd backend
   ```

2. Instala las dependencias:

   ```bash
   npm install
   ```

3. Crea un archivo `.env` en la raíz de `backend` (puedes copiar `.env.example` si existe) con las siguientes variables:

   ```env
   DB_HOST=
   DB_PORT=3306
   DB_USER=
   DB_PASSWORD=
   DB_NAME=asistencia
   ```

   Ajusta `DB_USER` y `DB_PASSWORD` según tu configuración local de MySQL.

4. Inicializa la base de datos (crea las tablas y carga los datos iniciales, incluyendo el usuario administrador):

   ```bash
   npm run db:seed
   ```

   Este comando ejecuta `query.sql` (creación de la base de datos y tablas) y luego `seed.sql` (roles, permisos y el usuario administrador inicial). Las credenciales del admin están definidas dentro de `seed.sql`.

5. Levanta el servidor:

   ```bash
   npm run dev
   ```

El backend debería quedar corriendo en el puerto configurado (revisa la consola para confirmar la URL).

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


### Asistencia
| Método | Ruta | Acceso | Requerimiento |
|---|---|---|---|
| POST | `/api/attendance/check-in` | autenticado | CA-01 |
| POST | `/api/attendance/check-out` | autenticado| CA-01|
| GET| `/api/attendance/today` | autenticado| Evitar duplicados|
| GET  | `/api/attendance//my-summary` | autenticado | Resumen propio mensual |


### Reportes
| Método | Ruta | Acceso | Requerimiento |
|---|---|---|---|
| GET | `/api/report/late-arrivals` | autenticado | RE-01 |
| GET | `/api/report/early-departures` | autenticado| RE-02|
| GET| `/api/report/absences` | autenticado| RE-03 |


### Feriados
| Método | Ruta | Acceso | Requerimiento |
|---|---|---|---|
| GET | `/api/holiday` | autenticado | Regla: feriados no se registra marcaje |
| POST | `/api/holiday` | autenticado| |
| DELETE| `/api/holiday/:id` | autenticado|  |
