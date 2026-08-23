import { holidayModel } from "./holiday.model.js";
import { isValidDate, isNonEmptyString } from "../utils/validate.js";
import { handleErrors } from "../database/error.js";

const list = async (req, res) => {
  try {
    const result = await holidayModel.list();
    return res.status(200).json({ ok: true, result });
  } catch (error) {
    const { status, message } = handleErrors(error.code);
    return res.status(status).json({ ok: false, result: message });
  }
};

const add = async (req, res) => {
  try {
    // formato correcto fecha: YYYY-MM-DD
    const fecha = (req.body?.fecha || "").trim();
    const descripcion = (req.body?.descripcion || "").trim();
    if (!isValidDate(fecha) || !isNonEmptyString(descripcion)) {
      throw error;
    }
    const result = await holidayModel.add(fecha, descripcion);
    return res.status(201).json({ ok: true, message: "Feriado agregado", result });
  } catch (error) {
    const { status, message } = handleErrors(error.code);
    return res.status(status).json({ ok: false, result: message });
  }
};

const remove = async (req, res) => {
  try {
    const exist = await holidayModel.findById(req.params.id);
    if (!exist) {
      throw { code: "414" };
    }
    const result = await holidayModel.remove(req.params.id);
    return res.status(200).json({ ok: true, message: "Feriado eliminado", result });
  } catch (error) {
    const { status, message } = handleErrors(error.code);
    return res.status(status).json({ ok: false, result: message });
  }
};

export const holidayController = { 
    list, 
    add, 
    remove 
};
