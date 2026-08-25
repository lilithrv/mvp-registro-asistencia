import jwt from "jsonwebtoken";
import randomstring from "randomstring";
import { userModel } from "../user/user.model.js";
import { handleErrors } from "../database/error.js";
import { isNonEmptyString, isValidPassword } from "../utils/validate.js";
import { hashPassword, comparePassword } from "../utils/hash.js";
import { authModel } from "./auth.model.js";

const getLogin = async (req, res) => {
    const { email } = req.body;
    
    const user = await userModel.findUserByEmail({ email })
    try {
        const token = jwt.sign({ id: user.rows[0].id, email:email }, process.env.JWT_SECRET, {
            expiresIn: process.env.JWT_EXPIRES_IN, 
        });

        res.cookie('accessToken', token, {
            httpOnly: false,
            secure: false, // true solo para https
            sameSite: "Lax", //None solo para https, sino Lax
        });
 
        res.status(200).json({
            id: user.rows[0].id,
            email: user.rows[0].email,
            id_rol: user.rows[0].id_rol,
            login: user.rows[0].cambiar_pass
        });

    } catch (error) {
        const { status, message } = handleErrors(error.code);
        console.log(error, message);
        return res.status(status).json({ ok: false, result: message });
    }
};

const logout = async (req, res) => {
    try {
        res.clearCookie("accessToken");
        res.sendStatus(204);
    } catch (error) {
        const { status, message } = handleErrors(error.name || error.code);
        console.log(error, message);
        return res.status(status).json({ ok: false, result: message });
    }
}

const changePassword = async (req, res) => {
  const { current_password, new_password } = req.body;
  try {

    if (!isNonEmptyString(current_password) || !isValidPassword(new_password)) {
      throw { code: "407" };
    }

    const user = await userModel.findUserByEmail({email:req.user.email})

    if (!user) {
        throw { code: "405" }
    }

    const match = await comparePassword(current_password, user.rows[0].password_hash);

    if (!match){
        throw { code: "402" };
    }

    const newHash = await hashPassword(new_password);

    await authModel.updatePassword(user.rows[0].id, newHash, 0); 

    return res.status(200).json({ ok: true, result: "Contraseña actualizada" });
  } catch (error) {
    const { status, message } = handleErrors(error.code);
    return res.status(status).json({ ok: false, result: message });
  }
};


export const authController = {
    getLogin,
    logout,
    changePassword
}
