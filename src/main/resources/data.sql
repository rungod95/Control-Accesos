INSERT INTO user_account (username, password, role, full_name, enabled)
VALUES ('admin', '$2a$10$.JAPy7Dt9nAsFY7Wmv4M7OViQmFbwKUfKcxipu7zY4oZInHKEy/oi', 'ADMIN', 'Administrador Mina', true);

INSERT INTO user_account (username, password, role, full_name, enabled)
VALUES ('operario1', '$2a$10$AQsGrAh0dLffSewUzcnCjuQtGSaZTwmGD6P719eSB52VCaCvVWbRm', 'TRABAJADOR', 'Operario Cargadero', true);

INSERT INTO access_log (nombre_persona, tipo_usuario, motivo, fecha_hora_entrada, qr_code)
VALUES ('Juan Pérez', 'trabajador', 'Turno mañana', '2025-10-20T08:00:00', 'QR-JP-001');

INSERT INTO access_log (nombre_persona, tipo_usuario, motivo, fecha_hora_entrada, fecha_hora_salida, qr_code)
VALUES ('María López', 'visitante', 'Mantenimiento', '2025-10-20T09:15:00', '2025-10-20T11:45:00', 'QR-ML-002');
