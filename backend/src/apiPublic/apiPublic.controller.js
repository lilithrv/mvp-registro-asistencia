import { handleErrors } from "../database/error.js";
import { apiPublicModel } from "./apiPublic.model.js";

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
        const result = await apiPublicModel.listByEstado(estado);
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

export const apiPublicController = {
  listByEstado,
}
