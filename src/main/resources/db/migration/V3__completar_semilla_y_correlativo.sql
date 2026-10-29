-- Completa la semilla sin alterar las migraciones aplicadas.
INSERT INTO cliente(documento,nombres,telefono) VALUES ('11111111','Carlos Quispe','987654321');
INSERT INTO cliente(documento,nombres,telefono) VALUES ('22222222','Rosa Flores','987654321');
INSERT INTO cliente(documento,nombres,telefono) VALUES ('33333333','Luis Mamani','987654321');
INSERT INTO cliente(documento,nombres,telefono) VALUES ('44444444','Ana Torres','987654321');
INSERT INTO servicio(codigo,nombre,precio,duracion_min,activo) VALUES ('SRV-003','Afinamiento',250,120,1);
INSERT INTO servicio(codigo,nombre,precio,duracion_min,activo) VALUES ('SRV-004','Revision de frenos',100,60,1);
INSERT INTO servicio(codigo,nombre,precio,duracion_min,activo) VALUES ('SRV-005','Cambio de pastillas',180,90,1);
INSERT INTO servicio(codigo,nombre,precio,duracion_min,activo) VALUES ('SRV-006','Balanceo',70,30,1);
INSERT INTO servicio(codigo,nombre,precio,duracion_min,activo) VALUES ('SRV-007','Diagnostico electronico',90,45,1);
INSERT INTO vehiculo(placa,marca,modelo,anio,cliente_id) SELECT 'CCC-333','Kia','Rio',2022,id FROM cliente WHERE documento='12345678';
INSERT INTO vehiculo(placa,marca,modelo,anio,cliente_id) SELECT 'DDD-444','Nissan','Versa',2022,id FROM cliente WHERE documento='11111111';
INSERT INTO vehiculo(placa,marca,modelo,anio,cliente_id) SELECT 'EEE-555','Toyota','Yaris',2022,id FROM cliente WHERE documento='22222222';
INSERT INTO vehiculo(placa,marca,modelo,anio,cliente_id) SELECT 'FFF-666','Suzuki','Swift',2022,id FROM cliente WHERE documento='33333333';
INSERT INTO vehiculo(placa,marca,modelo,anio,cliente_id) SELECT 'GGG-777','Kia','Picanto',2022,id FROM cliente WHERE documento='44444444';
INSERT INTO vehiculo(placa,marca,modelo,anio,cliente_id) SELECT 'HHH-888','Hyundai','Accent',2022,id FROM cliente WHERE documento='87654321';
INSERT INTO orden_servicio(numero,fecha,vehiculo_id,kilometraje,estado,total,usuario_registro,motivo_anulacion) SELECT 'OS-000002',CURRENT_TIMESTAMP,id,30000,'REGISTRADA',120,'asesor',NULL FROM vehiculo WHERE placa='BBB-222';
INSERT INTO orden_servicio(numero,fecha,vehiculo_id,kilometraje,estado,total,usuario_registro,motivo_anulacion) SELECT 'OS-000003',CURRENT_TIMESTAMP,id,20000,'REGISTRADA',250,'asesor',NULL FROM vehiculo WHERE placa='CCC-333';
INSERT INTO orden_servicio(numero,fecha,vehiculo_id,kilometraje,estado,total,usuario_registro,motivo_anulacion) SELECT 'OS-000004',CURRENT_TIMESTAMP,id,10000,'ANULADA',100,'asesor','Cancelacion del cliente' FROM vehiculo WHERE placa='DDD-444';
INSERT INTO detalle_orden(orden_id,servicio_id,cantidad,precio_unitario,subtotal) SELECT o.id,s.id,1,150,150 FROM orden_servicio o CROSS JOIN servicio s WHERE o.numero='OS-000001' AND s.codigo='SRV-001';
INSERT INTO detalle_orden(orden_id,servicio_id,cantidad,precio_unitario,subtotal) SELECT o.id,s.id,1,120,120 FROM orden_servicio o CROSS JOIN servicio s WHERE o.numero='OS-000002' AND s.codigo='SRV-002';
INSERT INTO detalle_orden(orden_id,servicio_id,cantidad,precio_unitario,subtotal) SELECT o.id,s.id,1,250,250 FROM orden_servicio o CROSS JOIN servicio s WHERE o.numero='OS-000003' AND s.codigo='SRV-003';
INSERT INTO detalle_orden(orden_id,servicio_id,cantidad,precio_unitario,subtotal) SELECT o.id,s.id,1,100,100 FROM orden_servicio o CROSS JOIN servicio s WHERE o.numero='OS-000004' AND s.codigo='SRV-004';
INSERT INTO roles(nombre) SELECT 'ASESOR' FROM dual WHERE NOT EXISTS(SELECT 1 FROM roles WHERE nombre='ASESOR');
INSERT INTO roles(nombre) SELECT 'JEFE_TALLER' FROM dual WHERE NOT EXISTS(SELECT 1 FROM roles WHERE nombre='JEFE_TALLER');
UPDATE usuarios SET password='$2b$12$Acp2SPxApevEhyYIRXvNjeEVkUod5.h.RWW4LT./xOSeA.gVdwd6C' WHERE username='admin';
MERGE INTO usuarios u USING (SELECT 'asesor' username, '$2b$12$Acp2SPxApevEhyYIRXvNjeEVkUod5.h.RWW4LT./xOSeA.gVdwd6C' password, 'Usuario asesor' nombrecompleto, 1 activo FROM dual) s ON (u.username=s.username) WHEN MATCHED THEN UPDATE SET u.password=s.password,u.activo=s.activo WHEN NOT MATCHED THEN INSERT(username,password,nombrecompleto,activo) VALUES(s.username,s.password,s.nombrecompleto,s.activo);
INSERT INTO usuario_roles(usuarioid,rolid) SELECT u.id,r.id FROM usuarios u CROSS JOIN roles r WHERE u.username='asesor' AND r.nombre='ASESOR' AND NOT EXISTS (SELECT 1 FROM usuario_roles ur WHERE ur.usuarioid=u.id AND ur.rolid=r.id);
MERGE INTO usuarios u USING (SELECT 'jefe' username, '$2b$12$Acp2SPxApevEhyYIRXvNjeEVkUod5.h.RWW4LT./xOSeA.gVdwd6C' password, 'Usuario jefe' nombrecompleto, 1 activo FROM dual) s ON (u.username=s.username) WHEN MATCHED THEN UPDATE SET u.password=s.password,u.activo=s.activo WHEN NOT MATCHED THEN INSERT(username,password,nombrecompleto,activo) VALUES(s.username,s.password,s.nombrecompleto,s.activo);
INSERT INTO usuario_roles(usuarioid,rolid) SELECT u.id,r.id FROM usuarios u CROSS JOIN roles r WHERE u.username='jefe' AND r.nombre='JEFE_TALLER' AND NOT EXISTS (SELECT 1 FROM usuario_roles ur WHERE ur.usuarioid=u.id AND ur.rolid=r.id);
MERGE INTO usuarios u USING (SELECT 'inactivo' username, '$2b$12$Acp2SPxApevEhyYIRXvNjeEVkUod5.h.RWW4LT./xOSeA.gVdwd6C' password, 'Usuario inactivo' nombrecompleto, 0 activo FROM dual) s ON (u.username=s.username) WHEN MATCHED THEN UPDATE SET u.password=s.password,u.activo=s.activo WHEN NOT MATCHED THEN INSERT(username,password,nombrecompleto,activo) VALUES(s.username,s.password,s.nombrecompleto,s.activo);
INSERT INTO usuario_roles(usuarioid,rolid) SELECT u.id,r.id FROM usuarios u CROSS JOIN roles r WHERE u.username='inactivo' AND r.nombre='ASESOR' AND NOT EXISTS (SELECT 1 FROM usuario_roles ur WHERE ur.usuarioid=u.id AND ur.rolid=r.id);
DECLARE
  siguiente NUMBER;
BEGIN
  SELECT NVL(MAX(TO_NUMBER(SUBSTR(numero,4))),0)+1 INTO siguiente FROM orden_servicio;
  EXECUTE IMMEDIATE 'CREATE SEQUENCE orden_numero_seq START WITH ' || siguiente || ' INCREMENT BY 1 NOCYCLE NOCACHE';
END;
/
