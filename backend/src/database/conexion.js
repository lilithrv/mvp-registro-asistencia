import * as dotenv from "dotenv";
import mysql from "mysql2/promise";

dotenv.config();


export const pool = mysql.createPool({
    host: process.env.DB_HOST,
    port: Number(process.env.DB_PORT),
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAM,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0,
    dateStrings: true,
});

export const testConnection = async () => {
    try {
        await pool.query("SELECT 1");
        console.log("Base de datos conectada");
    } catch (error) {
        console.log("Error conectando a la base de datos:", error);
        throw error;
    }
};