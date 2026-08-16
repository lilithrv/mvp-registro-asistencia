import jwt from "jsonwebtoken";
import bcrypt from "bcrypt";
import { userModel } from "../user/user.model.js";
import { handleErrors } from "../database/error.js";
import { comparePassword } from "../utils/hash.js";

export const verifyToken = (req, res, next) => {
    try {
        const token = req.cookies['accessToken']

        if (!token) {
            throw { code: "403" };
        }
   
        const payload =  jwt.verify(token, process.env.JWT_SECRET);

        req.user = payload;
        next();
    } catch (error) {
        if (error instanceof jwt.TokenExpiredError) {
            const { status, message } = handleErrors("407");
            return res.status(status).json({ ok: false, result: message });
        } else {
            const { status, message } = handleErrors(error.code);
            return res.status(status).json({ ok: false, result: message });
        }
    }
};

export const verifyUser = async (req, res, next) => {
    const { email, password } = req.body;
    try {
        if (!email || !password) {
            throw { code: "401" };
        }

        const {
            rows: [userDB],
            rowCount,
        } = await userModel.findUserByEmail({ email });

        if (!rowCount) {
            throw { code: "402" };
        }

        if (userDB.cambiar_pass) {
            const isValidTempPassword = await comparePassword(password,userDB.password_hash);

            if (!isValidTempPassword) {
                throw { code: "402" };
            }

            return next();
        } else {
            const validatePassword = await comparePassword(password, userDB.password_hash)
            if (validatePassword == false) {
                throw { code: "402" }
            }

            const user = await userModel.findUserByEmail({ email })
            if (!user.rows[0].estado) {
                throw { code: "404" }
            }


            console.log("Usuario correctamente autenticado: ", userDB.email);
            return next();
        }
    } catch (error) {
        const { status, message } = handleErrors(error.code);
        console.log(error, message);
        return res.status(status).json({ ok: false, result: message });
    }
};