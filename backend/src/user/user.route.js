import { Router } from "express";
import { userController } from "./user.controller.js";
import { verifyToken, verifyUser } from "../middlewares/verify.js";
import { authorize } from "../middlewares/authorize.js";

const router = Router();

//ADMIN
router.post("/users", verifyToken, authorize('create_users'), userController.addUser);

export default router;