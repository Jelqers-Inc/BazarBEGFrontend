-- Insertar usuario raíz con fecha_registro y contraseña encriptada
INSERT INTO usuario (nombre, email, contraseña, rol, fecha_registro)
SELECT * FROM (
    SELECT 'Administrador', 
           'admin@gmail.com',
           '$2a$10$Dow1mrcI5C5D/2rAq38k5uMdfWbt/sTb3mj7nA5i5rc8vEavzdiLa', -- admin123 en BCrypt
           'ADMIN',
           NOW()
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM usuario WHERE email = 'admin@gmail.com'
) LIMIT 1;

