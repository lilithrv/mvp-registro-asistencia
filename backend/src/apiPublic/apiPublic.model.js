import { pool } from "../database/conexion.js";

const listByEstado = async (estado) => {
    try {
        const text = `
        SELECT u.id, u.nombre, u.apellido, u.email, u.id_rol, r.nombre as nombre_rol, u.estado,
               u.created_at, u.updated_at
          FROM usuarios u
          JOIN roles r ON r.id = u.id_rol
         WHERE r.nombre = 'empleado'
           AND (u.estado = ? OR ? = 'todos')
         ORDER BY u.created_at
        `
        const [result] = await pool.execute(text, [estado, estado])
        return result
    } catch (error) {
        throw error
    }
}

export const apiPublicModel = {
    listByEstado
}