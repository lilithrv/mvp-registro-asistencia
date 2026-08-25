import jwt from "jsonwebtoken";
import randomstring from "randomstring";
import { userModel } from "./user.model.js";
import { handleErrors } from "../database/error.js";
import { isNonEmptyString, isValidPassword, isValidEmail } from "../utils/validate.js";
import { hashPassword, comparePassword } from "../utils/hash.js";

const addUser = async (req, res) => {
  const { nombre, apellido, email, password, id_rol } = req.body;

  try {
    if (!isNonEmptyString(nombre) || !isNonEmptyString(apellido) ||
      !isValidEmail(email) || !id_rol) {
      throw { code: "401" };
    }

    const existing = await userModel.findUserByEmail(email.trim());

    if (existing.rowCount !== 0) throw { code: "409" };

    // El admin puede definir la contraseña o se autogenera una temporal.
    const tempPassword = isNonEmptyString(password) ? password : randomstring.generate(15);;

    const password_hash = await hashPassword(tempPassword);

    const user = await userModel.create({
      nombre: nombre.trim(),
      apellido: apellido.trim(),
      email: email.trim(),
      password_hash,
      id_rol
    });


    // Se devuelve la contraseña temporal UNA vez para que el admin la comunique
    return res.status(201).json({
      ok: true,
      message: "Usuario creado correctamente",
      result: { ...user, temp_password: tempPassword },
    });
  } catch (error) {
    console.log(error)
    const { status, message } = handleErrors(error.code);
    return res.status(status).json({ ok: false, result: message });
  }
}

const updateUser = async (req, res) => {
  const { id } = req.params;
  const { nombre, apellido, email, id_rol } = req.body;

  try {
    if (!isNonEmptyString(nombre) || !isNonEmptyString(apellido) ||
      !isValidEmail(email) || !id_rol) {
      throw { code: "401" };
    }

    const existing = await userModel.findUserByEmail({ email: email.trim() });

    if (existing.rowCount !== 0 && existing.rows[0].id !== Number(id)) {
      throw { code: "409" };
    }

    const user = await userModel.update({
      id,
      nombre: nombre.trim(),
      apellido: apellido.trim(),
      email: email.trim(),
      id_rol,
    });

    return res.status(200).json({
      ok: true,
      message: "Usuario actualizado correctamente",
      result: user,
    });
  } catch (error) {
    console.log(error);
    const { status, message } = handleErrors(error.code);
    return res.status(status).json({ ok: false, result: message });
  }
};

const listUsers = async (req, res) => {
  try {
    const users = await userModel.list();
    return res.status(200).json({ ok: true, result: users });
  } catch (error) {
    console.log(error);
    const { status, message } = handleErrors(error.code);
    return res.status(status).json({ ok: false, result: message });
  }
}

const deleteUser = async (req, res) => {
  const { id } = req.params;

  try {
    if (Number(id) === Number(req.user?.id)) {
      throw { code: "416" };
    }

    const { rows: [user], rowCount } = await userModel.findAuthById({ id });
    if (!rowCount)
      throw { code: "405" };

    if (user.cambiar_pass) {
      // Nunca terminó su primer login, que incluye cambio de contraseña
      await userModel.hardDelete({ id });
      return res.status(200).json({ ok: true, message: "Usuario eliminado correctamente" });
    } else {
      // Ya usó el sistema 
      await userModel.softDelete({ id });
      return res.status(200).json({ ok: true, message: "Usuario desactivado (ya tenía actividad)" });
    }
  } catch (error) {
    console.log(error);
    const { status, message } = handleErrors(error.code);
    return res.status(status).json({ ok: false, result: message });
  }
};

export const userController = {
  addUser,
  updateUser,
  listUsers,
  deleteUser
}
