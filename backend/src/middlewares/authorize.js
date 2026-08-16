import { authModel } from "../auth/auth.model.js";
import { handleErrors } from "../database/error.js";

export const authorize = (permissionCode) => async (req, res, next) => {
    try {

        if (!req.user?.id) 
            throw { code: "403" };

        const { rowCount } = await authModel.permission({
            userId: req.user.id,
            permissionCode,
        });

        if (!rowCount) 
            throw { code: "404" };

        next();
    } catch (error) {
        const code = error.code || "404";
        const { status, message } = handleErrors(code);
        return res.status(status).json({ ok: false, result: message });
    }
};