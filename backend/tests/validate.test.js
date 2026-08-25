// ─────────────────────────────────────────────────────────────
// Tests unitarios: src/utils/validate.js
// Patrón AAA: Arrange (preparar) → Act (ejecutar) → Assert (afirmar)
// ─────────────────────────────────────────────────────────────

import {
    isNonEmptyString,
    isValidEmail,
    isValidPassword,
    isValidMonth,
    isValidDate,
} from "../src/utils/validate.js";

describe("isNonEmptyString", () => {
    it("CP-01: devuelve true cuando recibe una cadena de texto válida", () => {
        const entrada = "Juan";
        const resultado = isNonEmptyString(entrada);
        expect(resultado).toBe(true);
    });

    it("CP-02: devuelve false cuando recibe solo espacios", () => {
        expect(isNonEmptyString("   ")).toBe(false);
    });

    it("CP-03: devuelve false cuando recibe un número", () => {
        expect(isNonEmptyString(123)).toBe(false);
    });

    it("CP-04: devuelve false cuando recibe una cadena vacía", () => {
        expect(isNonEmptyString("")).toBe(false);
    });

    it("CP-05: devuelve false cuando recibe null", () => {
        expect(isNonEmptyString(null)).toBe(false);
    });

    it("CP-06: devuelve false cuando recibe undefined", () => {
        expect(isNonEmptyString(undefined)).toBe(false);
    });

    it("CP-07: devuelve false cuando recibe solo tabulaciones o saltos de línea", () => {
        expect(isNonEmptyString("\t\n")).toBe(false);
    });

    it("CP-08: devuelve true cuando el texto tiene espacios a los lados", () => {
        expect(isNonEmptyString("  Juan  ")).toBe(true);
    });

    it("CP-09: devuelve true cuando el texto tiene espacios internos", () => {
        expect(isNonEmptyString("Juan Pérez")).toBe(true);
    });

    it("CP-10: devuelve true con un string que contiene cero", () => {
        expect(isNonEmptyString("0")).toBe(true);
    });

    it("CP-11: devuelve false cuando recibe un booleano", () => {
        expect(isNonEmptyString(false)).toBe(false);
    });


    it("CP-12: devuelve false cuando recibe un arreglo", () => {
        expect(isNonEmptyString(["J", "u", "a", "n"])).toBe(false);
    });
});


describe("isValidEmail", () => {

    it("CP-13: devuelve true cuando el correo tiene formato válido", () => {
        const entrada = "test@correo.com";
        const resultado = isValidEmail(entrada);

        expect(resultado).toBe(true);
    });

    it("CP-14: devuelve false cuando falta el arroba", () => {
        expect(isValidEmail("sin-arroba.com")).toBe(false);
    });

    it("CP-15: devuelve false cuando el correo contiene un espacio", () => {
        expect(isValidEmail("espacio @correo.com")).toBe(false);
    });
});

describe("isValidPassword", () => {
    it("CP-16: devuelve false cuando la contraseña tiene menos de 8 caracteres", () => {
        expect(isValidPassword("abc123!")).toBe(false);
    });

    it("CP-17: devuelve false cuando la contraseña está vacía", () => {
        expect(isValidPassword("")).toBe(false);
    });
});

describe("isValidMonth", () => {
    it("CP-18: devuelve true cuando el mes tiene formato YYYY-MM válido", () => {
        expect(isValidMonth("2026-08")).toBe(true);
    });

    it("CP-19: devuelve false cuando el mes está fuera de rango", () => {
        expect(isValidMonth("2026-13")).toBe(false);
    });

    it("CP-20: devuelve false cuando el mes no tiene cero inicial", () => {
        expect(isValidMonth("2026-8")).toBe(false);
    });
});

describe("isValidDate", () => {
    it("CP-21: devuelve true cuando la fecha tiene formato YYYY-MM-DD válido", () => {
        expect(isValidDate("2026-08-23")).toBe(true);
    });

    it("CP-22: devuelve false cuando la fecha tiene formato invertido", () => {
        expect(isValidDate("23-08-2026")).toBe(false);
    });

    it("CP-23: devuelve false cuando la entrada no es una fecha", () => {
        expect(isValidDate("no-es-fecha")).toBe(false);
    });
});

describe("Datos reales del administrador (seed.sql)", () => {
    it("CP-34: el email del administrador cumple el formato válido", () => {
        expect(isValidEmail("admin@asistencia.cl")).toBe(true);
    });

    it("CP-35: la contraseña del administrador cumple la política mínima", () => {
        expect(isValidPassword("Admin1234")).toBe(true);
    });
});