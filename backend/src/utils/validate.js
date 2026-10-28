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

// RUT chileno: quita puntos y espacios, deja "cuerpo-DV" en minúscula el DV k.
// Acepta "12.345.678-9", "123456789", "12345678-9" -> "12345678-9"
export const normalizeRut = (v) => {
  if (!isNonEmptyString(v)) return "";
  const clean = v.replace(/[.\s]/g, "").replace(/-/g, "").toUpperCase();
  if (clean.length < 2) return "";
  const cuerpo = clean.slice(0, -1);
  const dv = clean.slice(-1);
  return `${cuerpo}-${dv}`;
};

// Dígito verificador esperado (módulo 11) para el cuerpo numérico del RUT.
const rutCheckDigit = (cuerpo) => {
  let suma = 0;
  let mult = 2;
  for (let i = cuerpo.length - 1; i >= 0; i--) {
    suma += Number(cuerpo[i]) * mult;
    mult = mult === 7 ? 2 : mult + 1;
  }
  const resto = 11 - (suma % 11);
  if (resto === 11) return "0";
  if (resto === 10) return "K";
  return String(resto);
};

// Valida formato y dígito verificador de un RUT chileno.
export const isValidRut = (v) => {
  const rut = normalizeRut(v);
  const m = /^(\d+)-([\dK])$/.exec(rut);
  if (!m) return false;
  return rutCheckDigit(m[1]) === m[2];
};
