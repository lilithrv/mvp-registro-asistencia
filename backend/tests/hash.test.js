import { hashPassword, comparePassword } from "../src/utils/hash.js";

const SEED_HASH =
    "$2b$10$HTxdx44NhesaKZ7f.Cj9hORQ5f9d18OfHJ2Bds.kigug0HD.73QXW";

describe("hashPassword", () => {
    it("CP-37: genera un hash distinto al texto plano", async () => {
        const hash = await hashPassword("Admin1234");
        expect(hash).not.toBe("Admin1234");
    });
});

describe("comparePassword", () => {
    it("CP-38: valida la contraseña correcta contra su hash", async () => {
        const hash = await hashPassword("Admin1234");
        expect(await comparePassword("Admin1234", hash)).toBe(true);
    });

    it("CP-39: rechaza una contraseña incorrecta contra su hash", async () => {
        const hash = await hashPassword("Admin1234");
        expect(await comparePassword("otraClave", hash)).toBe(false);
    });

    it("CP-40: valida la contraseña del administrador contra el hash del seed", async () => {
        expect(await comparePassword("Admin1234", SEED_HASH)).toBe(true);
    });

    it("CP-41: rechaza la contraseña del administrador escrita en minúsculas", async () => {
        expect(await comparePassword("admin1234", SEED_HASH)).toBe(false);
    });
});
