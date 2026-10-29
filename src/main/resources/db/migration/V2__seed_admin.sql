MERGE INTO roles r
USING (SELECT 'ADMIN' AS nombre FROM dual) s
ON (r.nombre = s.nombre)
WHEN NOT MATCHED THEN
    INSERT (nombre) VALUES (s.nombre);

MERGE INTO usuarios u
USING (
    SELECT 'admin' AS username,
           '$2a$10$Ei1B2TDYOVwL169n4DBPdeKGrO9VU5gOTATqdo/51xpqMhprgkKR6' AS password,
           'Administrador AutoAndes' AS nombrecompleto,
           1 AS activo
    FROM dual
) s
ON (LOWER(u.username) = LOWER(s.username))
WHEN MATCHED THEN
    UPDATE SET u.password = s.password,
               u.nombrecompleto = s.nombrecompleto,
               u.activo = s.activo
WHEN NOT MATCHED THEN
    INSERT (username, password, nombrecompleto, activo)
    VALUES (s.username, s.password, s.nombrecompleto, s.activo);

INSERT INTO usuario_roles (usuarioid, rolid)
SELECT u.id, r.id
FROM usuarios u
JOIN roles r ON r.nombre = 'ADMIN'
WHERE LOWER(u.username) = 'admin'
  AND NOT EXISTS (
      SELECT 1
      FROM usuario_roles ur
      WHERE ur.usuarioid = u.id
        AND ur.rolid = r.id
  );
