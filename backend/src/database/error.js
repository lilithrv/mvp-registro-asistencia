export const handleErrors = (code) => {
  if (!code) {
    return {
      status: 500,
      message: "Error de servidor: código desconocido",
    };
  }
  switch (code) {
    case "ER_DUP_ENTRY":
      return {
        status: 409,
        message: "El registro ya existe (valor duplicado)",
      };
    case "ER_NO_REFERENCED_ROW_2":
    case "ER_NO_REFERENCED_ROW":
      return {
        status: 404,
        message: "Referencia inválida: el objeto no existe",
      };
    case "ER_ROW_IS_REFERENCED_2":
      return {
        status: 409,
        message: "No se puede eliminar: el registro está referenciado",
      };
    case "ER_BAD_NULL_ERROR":
      return {
        status: 400,
        message: "Falta un campo obligatorio",
      };
    case "ER_TRUNCATED_WRONG_VALUE":
    case "WARN_DATA_TRUNCATED":
      return {
        status: 400,
        message: "Formato de dato inválido",
      };
    case "ER_BAD_DB_ERROR":
      return {
        status: 404,
        message: "La base de datos no existe",
      };
    case "ER_ACCESS_DENIED_ERROR":
      return {
        status: 401,
        message: "Acceso denegado a la base de datos",
      };
    case "ECONNREFUSED":
    case "ETIMEDOUT":
    case "PROTOCOL_CONNECTION_LOST":
      return {
        status: 503,
        message: "No se pudo conectar a la base de datos",
      };
    case "401":
      return {
        status: 400,
        message: "Datos inválidos o faltan campos obligatorios",
      };
    case "402":
      return {
        status: 401,
        message: "Credenciales inválidas",
      };
    case "403":
      return {
        status: 401,
        message: "No autenticado",
      };
    case "404":
      return {
        status: 403,
        message: "No tiene permisos para esta acción",
      };
    case "405":
      return {
        status: 404,
        message: "Recurso no encontrado",
      };
    case "406":
      return {
        status: 400,
        message: "Debe registrar su entrada antes de la salida",
      };
    case "407":
      return {
        status: 400,
        message: "La contraseña no cumple los requisitos mínimos",
      };
    case "408":
      return {
        status: 403,
        message:
          "Debe cambiar su contraseña temporal antes de continuar",
      };
    case "409":
      return {
        status: 409,
        message: "El correo ya está registrado",
      };
    case "410":
      return {
        status: 409,
        message: "Entrada ya registrada",
      };
    case "411":
      return {
        status: 409,
        message: "Salida ya registrada",
      };
    case "412":
      return {
        status: 400,
        message: "Fechas inválidas o rango incorrecto",
      };
    case "413":
      return {
        status: 401,
        message: "Usuario inválido",
      };
    case "414":
      return {
        status: 404,
        message: "Feriado no encontrado",
      };
    case "415":
      return {
        status: 400,
        message: "Día no laboral",
      };
    case "416":
      return {
        status: 409,
        message: "No puedes eliminar ni desactivar tu propia cuenta",
      };
    case "417":
      return {
        status: 400,
        message: "Nombre y apellido solo pueden contener letras (mínimo 3)",
      };
    default:
      return {
        status: 500,
        message: "Error interno del servidor",
      };
  }
};

