INSERT INTO roles (nombre, descripcion) VALUES
  ('admin',    'Administrador con gestión de usuarios y reportes'),
  ('empleado', 'Empleado que marca entrada/salida');

INSERT INTO permisos(codigo, descripcion) VALUES
  ('create_users',  'Crear usuarios'),
  ('read_users', 'Leer usuarios'),
  ('update_users',  'Modificar y listar usuarios'),
  ('delete_users',  'Eliminar (desactivar) usuarios'),
  ('read_reports',  'Ver reportes de atrasos, salidas anticipadas e inasistencias'),
  ('list_employees','Listar empleados vía API'),
  ('manage_holidays', 'Gestionar feriados (agregar/eliminar)');

INSERT INTO feriados (fecha, descripcion) VALUES
  ('2026-01-01', 'Año Nuevo'),
  ('2026-04-03', 'Viernes Santo'),
  ('2026-04-04', 'Sábado Santo'),
  ('2026-05-01', 'Día del Trabajador'),
  ('2026-05-21', 'Día de las Glorias Navales'),
  ('2026-06-21', 'Día Nacional de los Pueblos Indígenas'),
  ('2026-06-29', 'San Pedro y San Pablo'),
  ('2026-07-16', 'Virgen del Carmen'),
  ('2026-08-15', 'Asunción de la Virgen'),
  ('2026-09-18', 'Independencia Nacional'),
  ('2026-09-19', 'Día de las Glorias del Ejército'),
  ('2026-10-12', 'Encuentro de Dos Mundos'),
  ('2026-10-31', 'Día de las Iglesias Evangélicas y Protestantes'),
  ('2026-11-01', 'Día de Todos los Santos'),
  ('2026-12-08', 'Inmaculada Concepción'),
  ('2026-12-25', 'Navidad');

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