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

const findAuthById = async ({ id }) => {
    try {
        const text = `
        SELECT u.id, u.email, u.password_hash, u.estado, u.cambiar_pass, u.id_rol, r.nombre as nombre_rol
        FROM usuarios u
        JOIN roles r ON r.id = u.id_rol
        WHERE u.id = ? 
        LIMIT 1
        `;
        const [rows] = await pool.execute(text, [id]);
        return { rows, rowCount: rows.length };
    } catch (error) {
        throw error;
    }
};

const create = async ({ nombre, apellido, email, password_hash, id_rol }) => {
    try {
        const text = "INSERT INTO usuarios (nombre, apellido, email, password_hash, id_rol ) VALUES (?, ?, ?, ?, ?)"
        const [result] = await pool.execute(text, [nombre, apellido, email, password_hash, id_rol])
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

const update = async ({ id, nombre, apellido, email, id_rol }) => {
    try {
        const text = "UPDATE usuarios SET nombre = ?, apellido = ?, email = ?, id_rol = ? WHERE id = ?"
        const [result] = await pool.execute(text, [nombre, apellido, email, id_rol, id])
        return { id, nombre, apellido, email, id_rol };
    } catch (error) {
        throw error
    }
}

const list = async () => {
    try {
        const text = `
        SELECT u.id, u.nombre, u.apellido, u.email, u.id_rol, r.nombre as nombre_rol, u.estado, u.cambiar_pass,
        u.created_at, u.updated_at
        FROM usuarios u
        JOIN roles r ON r.id = u.id_rol
        ORDER BY u.created_at DESC
        `
        const [result] = await pool.execute(text)
        return result
    } catch (error) {
        throw error
    }
}

const softDelete = async ({ id }) => {
    try {
        const text = "UPDATE usuarios SET estado = 'inactivo' WHERE id = ?";
        const [result] = await pool.execute(text, [id]);
        return { id, estado: "inactivo" };
    } catch (error) {
        throw error;
    }
};

const hardDelete = async ({ id }) => {
    try {
        const text = "DELETE FROM usuarios WHERE id = ?";
        const [result] = await pool.execute(text, [id]);
        return { id, deleted: true };
    } catch (error) {
        throw error;
    }
};

export const userModel = {
    findUserByEmail,
    findAuthById,
    create,
    update,
    list,
    softDelete,
    hardDelete
}