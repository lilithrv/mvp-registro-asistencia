import { handleErrors } from "../src/database/error.js";

describe("handleErrors", () => {
    it("CP-24: mapea el código 402 a credenciales inválidas", () => {
        expect(handleErrors("402")).toEqual({
            status: 401,
            message: "Credenciales inválidas",
        });
    });

    it("CP-25: mapea ER_DUP_ENTRY a registro duplicado", () => {
        expect(handleErrors("ER_DUP_ENTRY")).toEqual({
            status: 409,
            message: "El registro ya existe (valor duplicado)",
        });
    });

    it("CP-26: mapea el código 410 a entrada ya registrada", () => {
        expect(handleErrors("410")).toEqual({
            status: 409,
            message: "Entrada ya registrada",
        });
    });

    it("CP-27: devuelve error de servidor cuando el código es null", () => {
        expect(handleErrors(null)).toEqual({
            status: 500,
            message: "Error de servidor: código desconocido",
        });
    });

    it("CP-28: devuelve error interno para códigos no registrados", () => {
        expect(handleErrors("CODIGO_INEXISTENTE")).toEqual({
            status: 500,
            message: "Error interno del servidor",
        });
    });
});
