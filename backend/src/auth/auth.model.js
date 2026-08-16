import { pool } from "../database/conexion.js";

const updatePassword = async (id, password_hash, mustChange = 0) => {
    try {
        const text = "UPDATE usuarios SET password_hash = ?, cambiar_pass = ? WHERE id = ?"
        const [result] = await pool.execute(text, [password_hash, mustChange ? 1 : 0, id])
        return result
    } catch (error) {
        throw error
    }
};

const permission = async ({ userId, permissionCode }) => {
    try {
        const text = `
        SELECT 1
         FROM usuarios u
         JOIN permisos_roles rp ON rp.id_rol = u.id_rol
         JOIN permisos p ON p.id = rp.id_permiso
        WHERE u.id = ? AND p.codigo = ? AND u.estado = 'activo'
        LIMIT 1
        `
        const [rows] = await pool.execute(text, [userId, permissionCode]);
        return { rows, rowCount: rows.length };
    } catch (error) {
        throw error
    }
}

export const authModel = {
    updatePassword,
    permission
}