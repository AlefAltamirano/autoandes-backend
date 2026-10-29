-- Ejecutar en el esquema AUTOANDES_DB; no modifica datos.
SELECT 'CLIENTES' recurso,COUNT(*) cantidad FROM cliente
UNION ALL SELECT 'SERVICIOS',COUNT(*) FROM servicio
UNION ALL SELECT 'VEHICULOS',COUNT(*) FROM vehiculo;
SELECT o.numero,o.fecha,v.placa,o.kilometraje,o.estado,o.total,o.usuario_registro
FROM orden_servicio o JOIN vehiculo v ON v.id=o.vehiculo_id ORDER BY o.fecha DESC;
SELECT o.numero,s.codigo,d.cantidad,d.precio_unitario,d.subtotal
FROM detalle_orden d JOIN orden_servicio o ON o.id=d.orden_id JOIN servicio s ON s.id=d.servicio_id ORDER BY o.id,d.id;
SELECT o.numero,o.total,SUM(d.subtotal) suma_detalles
FROM orden_servicio o JOIN detalle_orden d ON d.orden_id=o.id
GROUP BY o.numero,o.total HAVING o.total<>SUM(d.subtotal);
SELECT COUNT(*) detalles_huerfanos FROM detalle_orden d
LEFT JOIN orden_servicio o ON o.id=d.orden_id WHERE o.id IS NULL;
SELECT s.codigo,s.nombre,SUM(d.cantidad) cantidad_total,SUM(d.subtotal) importe_total
FROM detalle_orden d JOIN orden_servicio o ON o.id=d.orden_id JOIN servicio s ON s.id=d.servicio_id
WHERE o.estado='REGISTRADA' GROUP BY s.codigo,s.nombre ORDER BY importe_total DESC;
SELECT u.username,u.activo,r.nombre,CASE WHEN u.password LIKE '$2%' THEN 'BCrypt' ELSE 'REVISAR' END cifrado
FROM usuarios u JOIN usuario_roles ur ON ur.usuarioid=u.id JOIN roles r ON r.id=ur.rolid;
