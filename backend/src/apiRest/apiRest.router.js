import { Router } from "express";
import { apiRestController } from "./apiRest.controller.js";
import { verifyBearerToken } from "../middlewares/verify.js";

const router = Router();

router.get("/public/empleados", apiRestController.listByEstado);

router.get("/empleados", verifyBearerToken, apiRestController.listByRut);

router.post("/login", apiRestController.login);

router.get("/asistencia", verifyBearerToken, apiRestController.asistencia);

export default router;
