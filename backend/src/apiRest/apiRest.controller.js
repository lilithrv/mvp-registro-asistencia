import jwt from "jsonwebtoken";
import { handleErrors } from "../database/error.js";
import { apiRestModel } from "./apiRest.model.js";
import { userModel } from "../user/user.model.js";
import { comparePassword } from "../utils/hash.js";
import { isNonEmptyString, isValidDate, isValidRut, normalizeRut } from "../utils/validate.js";

const ESTADOS_VALIDOS = ['activo', 'inactivo', 'todos'];

const resolveEstado = (req) => {
    const { estado } = req.query;

    if (!estado || !ESTADOS_VALIDOS.includes(estado.toLowerCase())) {
         throw { code: "419" };
    }

   return estado
};

const listByEstado = async (req, res) => {
    try {
        const estado = resolveEstado(req);
        const result = await apiRestModel.listByEstado(estado);
        return res.status(200).json({
            ok: true,
            result: {
                estado,
                total: result.length,
                empleados: result
            }
        });
    } catch (error) {
        const { status, message } = handleErrors(error.code);
        return res.status(status).json({ ok: false, result: message });
    }
};

// AP-03: autentica al usuario desde la app móvil y devuelve su rol + token (en el body)
const login = async (req, res) => {
    const { email, password } = req.body;
    try {
        if (!isNonEmptyString(email) || !isNonEmptyString(password)) {
            throw { code: "401" };
        }

        const { rows: [userDB], rowCount } = await userModel.findUserByEmail({ email });

        if (!rowCount) {
            throw { code: "402" };
        }

        if (userDB.estado === "inactivo") {
            throw { code: "413" };
        }

        const match = await comparePassword(password, userDB.password_hash);
        if (!match) {
            throw { code: "402" };
        }

        // Usuario que nunca terminó su primer ingreso en la web (clave temporal).
        if (userDB.cambiar_pass) {
            throw { code: "422" };
        }

        const { rows: [auth] } = await userModel.findAuthById({ id: userDB.id });
        const rol = auth.nombre_rol;

        const token = jwt.sign(
            { id: userDB.id, email: userDB.email, id_rol: userDB.id_rol, rut: userDB.rut, rol },
            process.env.JWT_SECRET,
            { expiresIn: process.env.JWT_EXPIRES_IN }
        );

        return res.status(200).json({
            ok: true,
            mensaje: "Autenticación exitosa",
            result: {
                token,
                id: userDB.id,
                nombre: `${userDB.nombre} ${userDB.apellido}`,
                rol,
                rut: userDB.rut,
            },
        });
    } catch (error) {
        const { status, message } = handleErrors(error.code);
        return res.status(status).json({ ok: false, result: message });
    }
};

// AP-02: entradas y salidas de un trabajador por RUT y fecha.
// admin: consulta cualquier RUT (requerido en query)
// empleado: solo su propio RUT (el de su token); ignora el RUT de la query
const asistencia = async (req, res) => {
    try {
        const { rows: [actual], rowCount } = await userModel.findAuthById({ id: req.user?.id });
        if (!rowCount) {
            throw { code: "405" };
        }
        if (actual.estado === "inactivo") {
            throw { code: "413" };
        }

        const { fecha } = req.query;
        if (!isValidDate(fecha)) {
            throw { code: "412" };
        }
        const fechaStr = fecha.trim();

        // Fecha futura: no puede existir asistencia para un día que aún no ocurre
        const hoy = new Date();
        const hoyStr = `${hoy.getFullYear()}-${String(hoy.getMonth() + 1).padStart(2, "0")}-${String(hoy.getDate()).padStart(2, "0")}`;
        if (fechaStr > hoyStr) {
            throw { code: "423" };
        }

        let rutObjetivo;
        if (actual.nombre_rol === "admin") {
            if (!isNonEmptyString(req.query.rut)) {
                throw { code: "421" };
            }
            if (!isValidRut(req.query.rut)) {
                throw { code: "420" };
            }
            rutObjetivo = normalizeRut(req.query.rut);
        } else {
            // Empleado: siempre su propio RUT, sin importar lo que venga en la query
            rutObjetivo = req.user?.rut;
            if (!rutObjetivo) {
                // Su cuenta no tiene RUT cargado (p. ej. creada desde la web sin RUT)
                throw { code: "420" };
            }
        }

        const usuario = await apiRestModel.findUserByRut(rutObjetivo);
        if (!usuario) {
            throw { code: "405" };
        }

        const marcas = await apiRestModel.attendanceByUserAndDate(usuario.id, fechaStr);

        // Distinguir día no laboral (feriado o fin de semana) de una inasistencia
        if (!marcas.entrada && !marcas.salida) {
            const feriado = await apiRestModel.getFeriado(fechaStr);
            if (feriado) {
                return res.status(400).json({ ok: false, result: `Día no laboral: ${feriado.descripcion}` });
            }
            const [y, m, d] = fechaStr.split("-").map(Number);
            const dow = new Date(y, m - 1, d).getDay(); // 0=domingo, 6=sábado
            if (dow === 0 || dow === 6) {
                return res.status(400).json({ ok: false, result: "Día no laboral (fin de semana)" });
            }
        }

        return res.status(200).json({
            ok: true,
            result: {
                rut: usuario.rut,
                fecha: fechaStr,
                nombre: `${usuario.nombre} ${usuario.apellido}`,
                entrada: marcas.entrada,
                salida: marcas.salida,
            },
        });
    } catch (error) {
        const { status, message } = handleErrors(error.code);
        return res.status(status).json({ ok: false, result: message });
    }
};

export const apiRestController = {
  listByEstado,
  login,
  asistencia,
}
