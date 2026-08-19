import { Router } from "express";
import { attendanceController } from "./attendance.controller.js";
import { verifyToken, requirePasswordChanged } from "../middlewares/verify.js";
import { authorize } from "../middlewares/authorize.js";

const router = Router();

router.post("/check-in", verifyToken, requirePasswordChanged, attendanceController.checkIn);
router.post("/check-out", verifyToken, requirePasswordChanged, attendanceController.checkOut);
router.get("/today", verifyToken, requirePasswordChanged, attendanceController.today);
router.get("/my-summary", verifyToken, requirePasswordChanged, attendanceController.mySummary);

export default router;