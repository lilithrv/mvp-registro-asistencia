import { pool } from "../database/conexion.js";

// usuarios nuevos, no se incluyen en entrada atrasada en su día de creación
const lateArrivals = async (from, to, lateTime) => {
    try {
        const text = `
    SELECT u.id AS id_user, u.nombre, u.apellido, u.email,
            a.dia_registro AS dia,
            TIME(a.fecha_registro) AS primera_entrada
       FROM asistencia a
       JOIN usuarios u ON u.id = a.id_usuario
      WHERE a.tipo_registro = 'entrada'
        AND a.dia_registro BETWEEN ? AND ?
        AND TIME(a.fecha_registro) > ?
        AND a.dia_registro > DATE(u.created_at)
      ORDER BY u.id, a.dia_registro
    `
        const [rows] = await pool.execute(text, [from, to, lateTime]);
        return rows;
    } catch (error) {
        throw error
    }
};

const earlyDepartures = async (from, to, earlyTime) => {
    try {
        const text = `
    SELECT u.id AS id_user, u.nombre, u.apellido, u.email,
            a.dia_registro AS dia,
            TIME(a.fecha_registro) AS ultima_salida
       FROM asistencia a
       JOIN usuarios u ON u.id = a.id_usuario
      WHERE a.tipo_registro = 'salida'
        AND a.dia_registro BETWEEN ? AND ?
        AND TIME(a.fecha_registro) < ?
        AND u.estado = 'activo'
        AND a.dia_registro >= DATE(u.created_at)
      ORDER BY u.id, a.dia_registro
    `
        const [rows] = await pool.execute(text, [from, to, earlyTime]);
        return rows;
    } catch (error) {
        throw error
    }
};

// usuario nuevo, no sale como ausente los días antes de su creación
const absences = async (from, to) => {
    try {
        const text = `
        WITH RECURSIVE cal AS (
        SELECT CAST(? AS DATE) AS dia
        UNION ALL
        SELECT dia + INTERVAL 1 DAY FROM cal WHERE dia < ?
        )
        SELECT u.id AS id_user, u.nombre, u.apellido, u.email, c.dia
        FROM cal c
        CROSS JOIN usuarios u
        JOIN roles r ON r.id = u.id_rol
        WHERE u.estado = 'activo'
          AND r.nombre <> 'admin'
          AND DAYOFWEEK(c.dia) NOT IN (1, 7)
          AND c.dia >= DATE(u.created_at)
          AND NOT EXISTS (
            SELECT 1 FROM feriados f WHERE f.fecha = c.dia
          )
          AND NOT EXISTS (
                  SELECT 1 FROM asistencia a
                  WHERE a.id_usuario = u.id
                      AND a.dia_registro = c.dia
                      AND a.tipo_registro = 'entrada'
              )
        ORDER BY u.id, c.dia;
        `
        const [rows] = await pool.execute(text, [from, to]);
        return rows;
    } catch (error) {
        throw error
    }
}

export const reportModel = {
    lateArrivals,
    earlyDepartures,
    absences
}