import { reportModel } from "./report.model.js";
import { isValidDate } from "../utils/validate.js";
import { handleErrors } from "../database/error.js";

const LATE_TIME = process.env.LATE_ARRIVAL_TIME
const EARLY_TIME = process.env.EARLY_DEPARTURE_TIME

// Rango por defecto: mes actual
const defaultRange = () => {
    const now = new Date();
    const y = now.getFullYear();
    const m = String(now.getMonth() + 1).padStart(2, "0");
    const first = `${y}-${m}-01`;
    const today = `${y}-${m}-${String(now.getDate()).padStart(2, "0")}`;
    return { from: first, to: today };
};

// Lee y valida ?from=&to= (o aplica el rango por defecto)
const resolveRange = (req) => {
    const def = defaultRange();
    const from = req.query.from || def.from;
    const to = req.query.to || def.to;
    if (!isValidDate(from) || !isValidDate(to)) {
        throw { code: "412" };
    }
    if (from > to) 
        throw { code: "412" };
    return { from, to };
};

const lateArrivals = async (req, res) => {
    try {
        const { from, to } = resolveRange(req);
        const result = await reportModel.lateArrivals(from, to, LATE_TIME);
        return res.status(200).json({ ok: true, result: { from, to, rows: result } });
    } catch (error) {
        const { status, message } = handleErrors(error.code);
        return res.status(status).json({ ok: false, result: message });
    }
};

const earlyDepartures = async (req, res) => {
    try {
        const { from, to } = resolveRange(req);
        const result = await reportModel.earlyDepartures(from, to, EARLY_TIME);
        return res.status(200).json({ ok: true, result: { from, to, rows: result } });
    } catch (error) {
        const { status, message } = handleErrors(error.code);
        return res.status(status).json({ ok: false, result: message });
    }
};

const absences = async (req, res) => {
  try {
    const { from, to } = resolveRange(req);
    const result = await reportModel.absences(from, to);
    return res.status(200).json({ ok: true, result: { from, to, rows: result } });
  } catch (error) {
    const { status, message } = handleErrors(error.code);
    return res.status(status).json({ ok: false, result: message });
  }
};

export const reportController = {
    lateArrivals,
    earlyDepartures,
    absences
}