import { Router } from "express";
import { apiRestController } from "./apiRest.controller.js";
import { verifyBearerToken } from "../middlewares/verify.js";

const router = Router();

router.get("/empleados", verifyBearerToken, apiRestController.listByEstado);

router.post("/login", apiRestController.login);

router.get("/asistencia", verifyBearerToken, apiRestController.asistencia);

export default router;