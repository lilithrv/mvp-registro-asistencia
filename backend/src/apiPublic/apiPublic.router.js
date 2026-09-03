import { Router } from "express";
import { apiPublicController } from "./apiPublic.controller.js";

const router = Router();

router.get("/empleados", apiPublicController.listByEstado);

export default router;