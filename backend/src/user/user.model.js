import { pool } from "../database/conexion.js";

const findUserByEmail = async ({ email }) => {
    try {
        const text = "SELECT * FROM usuarios WHERE UPPER(email) = UPPER(?)";
        const [rows] = await pool.query(text, [email]);
        return {rows,  rowCount: rows.length };
    } catch (error) {
        console.log(error);
        throw error;
    }
};


const findAuthById = async (id) => {
    const text = "SELECT * FROM usuarios WHERE UPPER(email) = UPPER(?)";
    const [rows] = await pool.execute(text, [id])
    return rows[0]
};


const updatePassword = async (id, password_hash, mustChange = 0) => {
    const text = "UPDATE usuarios SET password_hash = ?, cambiar_pass = ? WHERE id = ?"
    const [result] = await pool.execute(text, [password_hash, mustChange ? 1 : 0, id])
    return result
};


export const userModel = {
    findUserByEmail,
    findAuthById,
    updatePassword
}