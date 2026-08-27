export const isNonEmptyString = (v) =>
  typeof v === "string" && v.trim().length > 0;

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export const isValidEmail = (v) => isNonEmptyString(v) && EMAIL_RE.test(v.trim());

// Política mínima de contraseña: al menos 8 caracteres.
export const isValidPassword = (v) =>
    isNonEmptyString(v) && v.length >= 8;

// Nombre/apellido: solo letras (con acentos y ñ) y espacios internos; mínimo 3 letras
const NAME_RE = /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+(?: [A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+)*$/;
export const isValidName = (v) =>
  isNonEmptyString(v) &&
  v.trim().replace(/\s/g, "").length >= 3 &&
  NAME_RE.test(v.trim());

// Estados válidos para el filtro de API
export const VALID_STATUSES = ["activo", "inactivo", "todos"];

// month en formato YYYY-MM
export const isValidMonth = (v) =>
  isNonEmptyString(v) && /^\d{4}-(0[1-9]|1[0-2])$/.test(v.trim());

// fecha en formato YYYY-MM-DD
export const isValidDate = (v) =>
  isNonEmptyString(v) && /^\d{4}-\d{2}-\d{2}$/.test(v.trim());
