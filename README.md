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
- React + Vite
- Bootstrap 5 + Bootstrap Icons

**Mobile** (app Android "Presente", Etapa 3)
- Kotlin + Jetpack Compose
- Retrofit (consumo de la API `/api/apiRest`)


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
   PORT=3000
   NODE_ENV=development
   FRONTEND_ORIGIN=
   DB_HOST=
   DB_PORT=3306
   DB_USER=
   DB_PASSWORD=
   DB_NAME=asistencia
   JWT_SECRET=
   JWT_EXPIRES_IN=2h
   LATE_ARRIVAL_TIME=09:30:00
   EARLY_DEPARTURE_TIME=17:30:00
   EMPLOYEES_SEED=employees_etapa2.sql o employees_etapa1.sql
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

## Frontend

1. Entra a `frontend` e instala las dependencias con `npm install`.
2. Copia `.env.example` como `.env` y ajusta `VITE_API_URL` si el backend no corre en el puerto 3000.
3. Ejecuta `npm run dev` y abre la dirección que muestra Vite.

El backend debe definir `FRONTEND_ORIGIN=http://localhost:5173` para aceptar las cookies de sesión durante el desarrollo.

## Mobile (Etapa 3)

Aplicación Android **"Presente"** de solo consulta de asistencia, que consume la API `/api/apiRest`. Las instrucciones de instalación, build y configuración están en su propio archivo: [`mobile/README.md`](mobile/README.md).

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


## Ejemplos de solicitudes con JSON

### Login

**Endpoint:**
`POST /api/auth/login`

**Body:**

```json
{
    "email":"admin@asistencia.cl",
    "password":"Admin1234"
}
```

### Cambiar contraseña

**Endpoint:**
`POST /api/auth/change-password`

**Body:**

```json
{
    "current_password":"Admin1234",
    "new_password":"nuevacontrasenia123"
}
```

### Crear usuario

**Endpoint:**
`POST /api/users`

**Body:**

```json
{
    "nombre":"Ana",
    "apellido":"Soto",
    "email":"ana@ej.com",
    "id_rol":2,
    "rut": "174567891"
}
```

### Actualizar usuario

**Endpoint:**
`PUT /api/users/:id`

**Body:**

```json
{
    "nombre":"Ana",
    "apellido":"Soto",
    "email":"ana@mail.com",
    "id_rol":2,
    "rut": "174567891"
}
```


### Atrasos
#### Por defecto, atrasos del día:

**Endpoint:**
`POST /api/report/late-arrivals/`

#### Para un día o rango específico:

**Endpoint:**

`POST /api/report/late-arrivals?from=2026-08-15&to=2026-08-19`

### Salidas anticipadas
#### Por defecto, salidas anticipadas del día:

**Endpoint:**
`POST /api/report/early-departures`

#### Para un día o rango específico:

**Endpoint:**
`POST /api/report/early-departures?from=2026-08-15&to=2026-08-19`


### Inasistencias

#### Por defecto, ausencias del mes en curso:

**Endpoint:**
`POST /api/report/absences`

#### Para un día específico:

**Endpoint:**
`POST /api/report/absences?from=2026-08-25&to=2026-08-25`


### API Pública
**Consumo externo — sin autenticación de sesión interna**

| Método | Ruta | Acceso | Requerimiento |
|---|---|---|---|
| GET | `/api/apiRest/public/empleados` | público / plataformas externas | AP-01 |

### Listar empleados (API pública)

#### Filtrar por estado (obligatorio: activo, inactivo o todos)
`GET /api/apiRest/public/empleados?estado=activo`

`GET /api/apiRest/public/empleados?estado=inactivo`

`GET /api/apiRest/public/empleados?estado=todos`

Ejemplo de respuesta:

**Endpoint:**
`GET /api/apiRest/public/empleados?estado=activo`

**Respuesta:**

```json
{
    "ok": true,
    "result": {
        "estado": "inactivo",
        "total": 1,
        "empleados": [
            {
                "id": 2,
                "nombre": "Juan",
                "apellido": "Pérez",
                "email": "juan.perez@asistencia.cl",
                "id_rol": 2,
                "nombre_rol": "empleado",
                "estado": "inactivo",
                "created_at": "2026-07-31 08:00:00",
                "updated_at": "2026-09-03 11:19:21"
            }
        ]
    }
}
```

## API Móvil (Etapa 3)

API REST de **solo consulta** que utiliza la app Android "Presente". Todas las rutas se montan bajo el prefijo `/api/apiRest`.

| Método | Ruta | Acceso | Requerimiento |
|---|---|---|---|
| POST | `/api/apiRest/login` | público | AP-03 (login móvil) |
| GET | `/api/apiRest/asistencia` | Bearer | AP-02 (asistencia por RUT y fecha) |
| GET | `/api/apiRest/empleados` | Bearer (solo admin) | GU-05 (listar usuarios por estado) |

**Autenticación**
- El login móvil está separado del login web: devuelve el **token JWT en el cuerpo** de la respuesta (no en cookie).
- Las rutas protegidas requieren la cabecera `Authorization: Bearer <token>`.
- El token es *stateless* (no hay endpoint de logout): para cerrar sesión la app borra el token guardado. El token caduca según `JWT_EXPIRES_IN` (2h por defecto); al expirar, la app debe volver a iniciar sesión.
- Precondición: el usuario debe haber cambiado su clave temporal en la aplicación web antes de poder usar la app móvil.

### Login (móvil)

**Endpoint:**
`POST /api/apiRest/login`

**Body:**

```json
{
    "email": "juan.perez@asistencia.cl",
    "password": "clave123456"
}
```

**Respuesta (200):**

```json
{
    "ok": true,
    "mensaje": "Autenticación exitosa",
    "result": {
        "token": "<jwt>",
        "id": 2,
        "nombre": "Juan Pérez",
        "rol": "empleado",
        "rut": "18234567-9"
    }
}
```

Errores posibles: credenciales inválidas, usuario inactivo (`"Usuario inválido"`) o usuario con clave temporal pendiente (`"Debe cambiar su contraseña en el sistema web antes de usar la app"`).

### Consultar asistencia

**Endpoint:**
`GET /api/apiRest/asistencia?rut=<rut>&fecha=<YYYY-MM-DD>`

Requiere cabecera `Authorization: Bearer <token>`.

- **Administrador:** debe enviar `rut` y `fecha` (puede consultar a cualquier trabajador).
- **Trabajador:** envía solo `fecha`; el RUT se toma de su token (si manda otro, se ignora).
- El RUT acepta con o sin puntos (`18.234.567-9` o `18234567-9`); se valida el dígito verificador.

**Respuesta (200) con marcas:**

```json
{
    "ok": true,
    "result": {
        "rut": "18234567-9",
        "fecha": "2026-09-04",
        "nombre": "Juan Pérez",
        "entrada": "09:29:49",
        "salida": "18:16:45"
    }
}
```

**Casos especiales:**

| Situación | Respuesta |
|---|---|
| Día hábil pasado sin marcas | 200 con `entrada: null` y `salida: null` (inasistencia) |
| Fecha futura | 400 — `"La fecha consultada aún no ha ocurrido"` |
| Feriado sin marcas | 400 — `"Día no laboral: <descripción del feriado>"` |
| Fin de semana sin marcas | 400 — `"Día no laboral (fin de semana)"` |

### Listar empleados por estado (admin)

**Endpoint:**
`GET /api/apiRest/empleados?estado=activo|inactivo|todos`

Requiere cabecera `Authorization: Bearer <token>` y rol administrador. Devuelve el listado de usuarios filtrado por estado (misma estructura que la API pública). El parámetro `estado` es obligatorio.