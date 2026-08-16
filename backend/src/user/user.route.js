import { Router } from "express";
import { userController } from "./user.controller.js";
import { verifyToken, verifyUser } from "../middlewares/verify.js";

const router = Router();

router.post("/login", verifyUser, userController.getLogin);
router.post("/logout", userController.logout);
router.post("/change-password", verifyToken, userController.changePassword);

export default router;