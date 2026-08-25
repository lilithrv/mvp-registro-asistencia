import { handleErrors } from "../database/error.js";
import { holidayModel } from "../holiday/holiday.model.js";
import { attendanceModel } from "./attendance.model.js";

const LATE_TIME = process.env.LATE_ARRIVAL_TIME || "09:30:00";
const EARLY_TIME = process.env.EARLY_DEPARTURE_TIME || "17:30:00";

// feriados y fin de semana: no laboral

const checkIn = async (req, res) => {
    try {
        if (!(await holidayModel.isWorkingToday())) 
            throw { code: "415" };

        // no se pueden ingresar 2 entradas para un mismo día
        const today = await attendanceModel.getTodayMarks(req.user.id);
        if (today.entrada) 
            throw { code: "410" };

        const mark = await attendanceModel.addMark(req.user.id, "entrada");
        return res.status(201).json({ ok: true, message: "Entrada registrada", result: mark });
    } catch (error) {
        console.log(error)
        const { status, message } = handleErrors(error.code);
        return res.status(status).json({ ok: false, result: message });
    }
};

const checkOut = async (req, res) => {
    try {
        if (!(await holidayModel.isWorkingToday())) 
            throw { code: "415" };
        
        // se requiere entrada previa y no se permite una segunda salida
        const today = await attendanceModel.getTodayMarks(req.user.id);
        if (!today.entrada)
            throw { code: "406" };
        if (today.salida)
            throw { code: "411" };

        const mark = await attendanceModel.addMark(req.user.id, "salida");
        return res.status(201).json({ ok: true, message: "Salida registrada", result: mark });
    } catch (error) {
        const { status, message } = handleErrors(error.code);
        return res.status(status).json({ ok: false, result: message });
    }
};

// estado de asistencia del día
const today = async (req, res) => {
    try {
        const state = await attendanceModel.getTodayMarks(req.user.id);
        return res.status(200).json({ ok: true, result: state });
    } catch (error) {
        const { status, message } = handleErrors(error.code);
        return res.status(status).json({ ok: false, result: message });
    }
};

const mySummary = async (req, res) => {
    try {
        const now = new Date();
        const month = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;

        const data = await attendanceModel.monthSummary(req.user.id, month, LATE_TIME, EARLY_TIME);
        return res.status(200).json({ ok: true, result: { month, rows: data } });
    } catch (error) {
        const { status, message } = handleErrors(error.code);
        return res.status(status).json({ ok: false, result: message });
    }
};


export const attendanceController = {
    checkIn,
    checkOut,
    today,
    mySummary
}
