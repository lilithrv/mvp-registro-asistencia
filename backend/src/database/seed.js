// Script de inicialización de la base de datos.
// Ejecuta query.sql (creación de BD + tablas) y luego seed.sql
// el seed ya trae el password_hash y el email del admin

// Uso: npm run db:seed

import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";
import mysql from "mysql2/promise";
import dotenv from "dotenv";

dotenv.config();

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const runSqlFile = async (conn, file) => {
  const sql = fs.readFileSync(path.join(__dirname, file), "utf8");
  await conn.query(sql);
};

const main = async () => {
  const conn = await mysql.createConnection({
    host: process.env.DB_HOST || "localhost",
    port: Number(process.env.DB_PORT) || 3306,
    user: process.env.DB_USER || "root",
    password: process.env.DB_PASSWORD || "",
    multipleStatements: true,
  });

  try {
    console.log("→ Ejecutando query.sql (creación de BD y tablas) ...");
    await runSqlFile(conn, "query.sql");

    const dbName = process.env.DB_NAME || "asistencia";
    await conn.changeUser({ database: dbName });

    console.log("→ Ejecutando seed.sql (roles, permisos, usuario admin) ...");
    await runSqlFile(conn, "seed.sql");

    console.log("\n Base de datos inicializada correctamente.");
    console.log(
      "   Revisa seed.sql para las credenciales del admin (email/password ya definidos ahí)."
    );
  } catch (err) {
    console.error("\n Error al inicializar la base de datos:", err.message);
    process.exitCode = 1;
  } finally {
    await conn.end();
  }
};

main();