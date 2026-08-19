import { Router } from "express";
import { reportController } from "./report.controller.js";
import { verifyToken, requirePasswordChanged } from "../middlewares/verify.js";
import { authorize } from "../middlewares/authorize.js";

const router = Router();

router.get("/late-arrivals", verifyToken, requirePasswordChanged, authorize('read_reports'), reportController.lateArrivals);
router.get("/early-departures", verifyToken, requirePasswordChanged, authorize('read_reports'), reportController.earlyDepartures);
router.get("/absences", verifyToken, requirePasswordChanged, authorize('read_reports'), reportController.absences);

export default router;