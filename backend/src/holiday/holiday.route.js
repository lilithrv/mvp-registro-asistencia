import { Router } from "express";
import { holidayController } from "./holiday.controller.js";
import { verifyToken, requirePasswordChanged } from "../middlewares/verify.js";
import { authorize } from "../middlewares/authorize.js";

const router = Router();

router.get("/", verifyToken, requirePasswordChanged, authorize("manage_holidays"), holidayController.list);
router.post("/", verifyToken, requirePasswordChanged, authorize("manage_holidays"), holidayController.add);
router.delete("/:id", verifyToken, requirePasswordChanged, authorize("manage_holidays"), holidayController.remove);

export default router;