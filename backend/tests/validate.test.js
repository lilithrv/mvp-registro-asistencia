// ─────────────────────────────────────────────────────────────
// Tests unitarios: src/utils/validate.js
// Patrón AAA: Arrange (preparar) → Act (ejecutar) → Assert (afirmar)
// ─────────────────────────────────────────────────────────────

import { isNonEmptyString } from "../src/utils/validate.js";

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