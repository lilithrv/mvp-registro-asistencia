import { pool } from "../database/conexion.js";

const findUserByEmail = async ({ email }) => {
    try {
        const text = "SELECT * FROM usuarios WHERE UPPER(email) = UPPER(?)";
        const [rows] = await pool.query(text, [email]);
        return { rows, rowCount: rows.length };
    } catch (error) {
        throw error;
    }
};

const findAuthById = async (id) => {
    try {
        const text = "SELECT * FROM usuarios WHERE UPPER(email) = UPPER(?)";
        const [rows] = await pool.execute(text, [id])
        return rows[0]
    } catch (error) {
        throw error;
    }

};

const create = async ({ nombre, apellido, email, password_hash, id_rol }) => {
    try {
        const text = "INSERT INTO usuarios (nombre, apellido, email, password_hash, id_rol ) VALUES (?, ?, ?, ?, ?)"
        const [result] = await pool.execute(text, [nombre, apellido, email, password_hash, id_rol ])
        return {
            id: result.insertId,
            nombre,
            apellido,
            email,
        };
    } catch (error) {
        throw error
    }
};

export const userModel = {
    findUserByEmail,
    findAuthById,
    create
}