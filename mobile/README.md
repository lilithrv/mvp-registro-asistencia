# Presente — App móvil de consulta de asistencia (Etapa 3)

Aplicación Android de **solo consulta** de asistencia que consume la API REST del backend bajo el prefijo `/api/apiRest`. Forma parte del MVP de registro de asistencia; no marca entrada/salida ni gestiona usuarios (eso sigue en la aplicación web).

Según el rol que devuelve el login, la app muestra una pantalla distinta:

- **Administrador:** lista usuarios por estado (`GET /api/apiRest/empleados`) y consulta la asistencia de cualquier trabajador por RUT y fecha (`GET /api/apiRest/asistencia`).
- **Trabajador:** consulta únicamente su propia asistencia ingresando una fecha (el RUT se toma de su token).

## Tecnologías

- Kotlin + Jetpack Compose (Material 3)
- Retrofit para el consumo de la API
- `applicationId`: `cl.githappens.presente`

## Requisitos previos

- Android Studio (o el Android SDK por línea de comandos)
- JDK 11
- `minSdk 26` / `targetSdk 37`

## Configuración del backend (`API_BASE_URL`)

La URL base de la API se define por variante de build en `app/build.gradle.kts` mediante `buildConfigField`:

| Variante | `API_BASE_URL` |
|---|---|
| `release` | `https://portal-asistencia.chilecentral.cloudapp.azure.com/api/apiRest/` |

## Build y ejecución

Desde la carpeta `mobile/`:

```bash
# Compilar el APK de depuración
./gradlew assembleDebug

# Instalar en un emulador o dispositivo conectado
./gradlew installDebug
```

También puedes abrir la carpeta `mobile/` en Android Studio y ejecutar la app con el botón *Run*.

## Precondiciones de datos

Para que la consulta por RUT devuelva resultados, el backend debe estar corriendo y los usuarios deben tener RUT cargado. En desarrollo esto se logra inicializando la base de datos con el seed de la Etapa 3:

```bash
# En la carpeta backend/
EMPLOYEES_SEED=employees_etapa3.sql npm run db:seed
```

Además, el usuario debe haber cambiado su clave temporal en la aplicación web antes de poder iniciar sesión en la app móvil.

## Endpoints que consume

Documentados en el [README del proyecto](../README.md#api-móvil-etapa-3): `POST /api/apiRest/login`, `GET /api/apiRest/asistencia` y `GET /api/apiRest/empleados`.
