INSERT INTO roles (nombre, descripcion) VALUES
  ('admin',    'Administrador con gestión de usuarios y reportes'),
  ('empleado', 'Empleado que marca entrada/salida');

INSERT INTO permisos(codigo, descripcion) VALUES
  ('create_users',  'Crear usuarios'),
  ('read_users', 'Leer usuarios'),
  ('update_users',  'Modificar y listar usuarios'),
  ('delete_users',  'Eliminar (desactivar) usuarios'),
  ('read_reports',  'Ver reportes de atrasos, salidas anticipadas e inasistencias'),
  ('list_employees','Listar empleados vía API');

-- rol admin
INSERT INTO permisos_roles (id_rol, id_permiso)
SELECT r.id, p.id
FROM roles r
JOIN permisos p
WHERE r.nombre = 'admin'
ON DUPLICATE KEY UPDATE id_rol = permisos_roles.id_rol;

-- clave inicial Admin1234
INSERT INTO usuarios (nombre, apellido, email, password_hash, id_rol, cambiar_pass) VALUES
('admin', 'admin', 'admin@asistencia.cl', '$2b$10$HTxdx44NhesaKZ7f.Cj9hORQ5f9d18OfHJ2Bds.kigug0HD.73QXW', 1,true);