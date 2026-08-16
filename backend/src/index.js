import path from "path";
import express from "express";
import cors from "cors";
import cookieParser from "cookie-parser";
import dotenv from "dotenv";
import { fileURLToPath } from "url";

import { testConnection } from "./database/conexion.js";
import  userRoutes from "./user/user.route.js";

const app = express();

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const PORT = process.env.PORT || 3000;
const FRONTEND_ORIGIN = process.env.FRONTEND_ORIGIN

app.use(cors({
    origin: FRONTEND_ORIGIN, credentials: true
}));

app.use(express.json());
app.use(cookieParser());
app.use(express.static('public'))

// ruta del front
// app.use(express.static(path.join(__dirname, "")));

// RUTAS
app.use("/api/users", userRoutes);

app.use((req, res) => {
    res.status(404).json({ ok: false, result: "Ruta no encontrada" });
});

app.use((error, req, res, next) => {
    console.error('[ERROR]', error);
    res.status(500).json({
        ok: false,
        mensaje: "Error interno del servidor."
    });
});

const start = async () => {
  try {
    await testConnection();
    console.log("Conexión a MySQL verificada");
  } catch (err) {
    console.error("No se pudo conectar a MySQL:", err.message);
    console.error("Verifica las variables DB_* en .env y que el servidor MySQL esté activo.");
  }
  app.listen(PORT, () => {
    console.log(`Servidor ejecutándose en http://localhost:${PORT}`);
  });
};

start();