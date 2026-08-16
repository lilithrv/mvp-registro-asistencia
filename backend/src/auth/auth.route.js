import { Router } from "express";
import { authController } from "./auth.controller.js";
import { verifyToken, verifyUser } from "../middlewares/verify.js";

const router = Router();

router.post("/login", verifyUser, authController.getLogin);
router.post("/logout", authController.logout);
router.post("/change-password", verifyToken, authController.changePassword);

export default router;