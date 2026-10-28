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

// Busca un usuario por su RUT (normalizado "cuerpo-DV"). Devuelve también su rol.
const findUserByRut = async (rut) => {
    try {
        const text = `
        SELECT u.id, u.nombre, u.apellido, u.email, u.rut, u.estado,
               u.id_rol, r.nombre AS nombre_rol
          FROM usuarios u
          JOIN roles r ON r.id = u.id_rol
         WHERE u.rut = ?
         LIMIT 1
        `
        const [rows] = await pool.execute(text, [rut])
        return rows[0] || null
    } catch (error) {
        throw error
    }
}

// Entradas y salidas de un usuario en un día concreto (fecha YYYY-MM-DD).
const attendanceByUserAndDate = async (id_usuario, fecha) => {
    try {
        const text = `
        SELECT
            MIN(CASE WHEN tipo_registro = 'entrada' THEN TIME(fecha_registro) END) AS entrada,
            MAX(CASE WHEN tipo_registro = 'salida'  THEN TIME(fecha_registro) END) AS salida
           FROM asistencia
          WHERE id_usuario = ?
            AND dia_registro = ?
        `
        const [rows] = await pool.execute(text, [id_usuario, fecha])
        const row = rows[0] || {}
        return { entrada: row.entrada || null, salida: row.salida || null }
    } catch (error) {
        throw error
    }
}

// Devuelve el feriado de una fecha (YYYY-MM-DD) o null si no lo es
const getFeriado = async (fecha) => {
    try {
        const text = `SELECT fecha, descripcion FROM feriados WHERE fecha = ? LIMIT 1`
        const [rows] = await pool.execute(text, [fecha])
        return rows[0] || null
    } catch (error) {
        throw error
    }
}

export const apiRestModel = {
    listByEstado,
    findUserByRut,
    attendanceByUserAndDate,
    getFeriado
}