import { Router } from "express";
import { userController } from "./user.controller.js";
import { verifyToken, verifyUser, requirePasswordChanged } from "../middlewares/verify.js";
import { authorize } from "../middlewares/authorize.js";

const router = Router();

//ADMIN
router.get("/", verifyToken, requirePasswordChanged, authorize('read_users'), userController.listUsers);
router.post("/", verifyToken, requirePasswordChanged, authorize('create_users'), userController.addUser);
router.put("/:id", verifyToken, requirePasswordChanged, authorize('update_users'), userController.updateUser);
router.delete("/:id", verifyToken, requirePasswordChanged, authorize('delete_users'), userController.deleteUser);

export default router;