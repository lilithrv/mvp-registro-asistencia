import { pool } from "../database/conexion.js";

const isWorkingToday = async () => {
    try {
        const text = `
    SELECT (
        DAYOFWEEK(CURDATE()) NOT IN (1, 7)   -- 1=domingo, 7=sábado
        AND NOT EXISTS (SELECT 1 FROM feriados WHERE fecha = CURDATE())
      ) AS laborable
    `
        const [rows] = await pool.execute(text);
        return rows[0].laborable === 1;
    } catch (error) {
        throw error
    }
};

const list = async () => {
    try {
        const text = "SELECT id, fecha, descripcion FROM feriados ORDER BY fecha";
        const [rows] = await pool.execute(text);
        return rows;
    } catch (error) {
        throw error
    }
};

// agregar un feriado
const add = async (fecha, descripcion) => {
    try {
        const text = "INSERT INTO feriados (fecha, descripcion) VALUES (?, ?)"
        const [result] = await pool.execute(text, [fecha, descripcion]);
        return {
            id: result.insertId,
            fecha,
            descripcion
        };
    } catch (error) {
        throw error
    }
};

// eliminar un feriado por id
const remove = async (id) => {
    try {
        const text = "DELETE FROM feriados WHERE id = ?"
        await pool.execute(text, [id]);
        return { id, deleted: true };
    } catch (error) {
        throw error
    }
};

const findById = async (id) => {
    try {
        const text = "SELECT * FROM feriados WHERE id = ?"
        const [rows] = await pool.execute(text,[id]);
        return rows[0]
    } catch (error) {
        throw error
    }
}

export const holidayModel = {
    isWorkingToday,
    list,
    add,
    remove,
    findById
};