import { pool } from "../database/conexion.js";

const addMark = async (id_usuario, tipo_registro) => {
    const fecha_registro = new Date();
    const text = "INSERT INTO asistencia (id_usuario, tipo_registro, fecha_registro) VALUES (?, ?, ?)"
    const [result] = await pool.execute(text, [id_usuario, tipo_registro, fecha_registro]);
    return {
            id: result.insertId,
            id_usuario,
            tipo_registro,
            fecha_registro
        };
};

const getTodayMarks = async (id_usuario) => {
    const text = `
    SELECT
        MAX(CASE WHEN tipo_registro = 'entrada' THEN TIME(fecha_registro) END) AS entrada,
        MAX(CASE WHEN tipo_registro = 'salida'  THEN TIME(fecha_registro) END) AS salida
       FROM asistencia
      WHERE id_usuario = ? AND DATE(fecha_registro) = CURDATE()
    `
  const [rows] = await pool.execute(text, [id_usuario]);
  const row = rows[0] || {};
  return { entrada: row.entrada || null, salida: row.salida || null };
};

const monthSummary = async (id_usuario, mes, atraso, salidaAnticipada) => {
    // month = 'YYYY-MM'
    const text = `
    SELECT
        DATE(fecha_registro) AS dia,
        MIN(CASE WHEN tipo_registro = 'entrada' THEN TIME(fecha_registro) END) AS primera_entrada,
        MAX(CASE WHEN tipo_registro = 'salida'  THEN TIME(fecha_registro) END) AS ultima_salida
       FROM asistencia
      WHERE id_usuario = ?
        AND DATE_FORMAT(fecha_registro, '%Y-%m') = ?
      GROUP BY DATE(fecha_registro)
      ORDER BY dia
  `
    const [rows] = await pool.execute(text, [id_usuario, mes]);

    return rows;
};

export const attendanceModel = {
    addMark,
    getTodayMarks,
    monthSummary
}