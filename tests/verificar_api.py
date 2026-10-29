"""Pruebas reales contra Oracle. Ejecutar con el backend iniciado: python tests/verificar_api.py.
Crea datos temporales con código SRV-099 y placa ZZT-999 y los elimina al terminar.
Una orden demostrativa queda ANULADA para conservar su trazabilidad.
"""
import json, urllib.request, urllib.error, os, base64, hmac, hashlib, time
from datetime import date
from pathlib import Path
BASE=os.environ.get('API_URL','http://localhost:8080/api/v1')
results=[]
def call(name,method,path,status,body=None,token=None):
    headers={'Content-Type':'application/json'}
    if token: headers['Authorization']='Bearer '+token
    request=urllib.request.Request(BASE+path,data=None if body is None else json.dumps(body).encode(),headers=headers,method=method)
    try:
        with urllib.request.urlopen(request,timeout=30) as response: code=response.status; raw=response.read()
    except urllib.error.HTTPError as e: code=e.code; raw=e.read()
    data=json.loads(raw) if raw else None
    assert code==status,(name,code,data)
    if code>=400:
        assert all(k in data for k in ['timestamp','status','error','mensaje','ruta']),data
        assert isinstance(data['timestamp'],str),data
        assert ('campos' not in data or isinstance(data['campos'],dict)),data
    results.append({'prueba':name,'status':code,'resultado':'OK','respuesta': data if '/auth/' not in path else {'autenticacion':code}})
    print(f'OK {code} {name}')
    return data

def main():
    admin=call('Login administrador','POST','/auth/login',200,{'username':'admin','password':'Auto2026*'})
    asesor=call('Login asesor','POST','/auth/login',200,{'username':'asesor','password':'Auto2026*'})
    jefe=call('Login jefe','POST','/auth/login',200,{'username':'jefe','password':'Auto2026*'})
    assert len(admin['permisos'])==8 and 'ORDEN_REGISTRAR' in asesor['permisos'] and 'ORDEN_REGISTRAR' not in jefe['permisos']
    a=admin['token'];s=asesor['token'];j=jefe['token']
    for username,password in [('inactivo','Auto2026*'),('nadie','Auto2026*'),('admin','incorrecta')]:
        call('Login rechazado '+username,'POST','/auth/login',401,{'username':username,'password':password})
    call('Login incompleto','POST','/auth/login',400,{})
    call('Usuario actual','GET','/auth/me',200,token=a)
    call('Sin token','GET','/servicios',401)
    call('Token alterado','GET','/servicios',401,token=a[:-8]+'xxxxxxxx')
    encode=lambda x:base64.urlsafe_b64encode(json.dumps(x,separators=(',',':')).encode()).decode().rstrip('=')
    unsigned=encode({'alg':'HS256'})+'.'+encode({'sub':'admin','iat':int(time.time())-7200,'exp':int(time.time())-3600})
    secret=os.environ.get('APP_SECURITY_JWT_SECRET','AutoAndes-secret-key-change-in-production-2026')
    expired=unsigned+'.'+base64.urlsafe_b64encode(hmac.new(secret.encode(),unsigned.encode(),hashlib.sha256).digest()).decode().rstrip('=')
    call('Token vencido','GET','/servicios',401,token=expired)
    cors=urllib.request.Request(BASE+'/ordenes',method='OPTIONS',headers={'Origin':'http://localhost:4200','Access-Control-Request-Method':'POST','Access-Control-Request-Headers':'Authorization,Content-Type'})
    with urllib.request.urlopen(cors) as r:
        assert r.headers.get('Access-Control-Allow-Origin')=='http://localhost:4200'
        assert 'POST' in r.headers.get('Access-Control-Allow-Methods','')
    results.append({'prueba':'CORS preflight Authorization','status':200,'resultado':'OK','respuesta':{'origen':'http://localhost:4200'}})
    print('OK 200 CORS preflight Authorization')
    call('Reporte prohibido asesor','GET','/reportes/ingresos-por-servicio',403,token=s)
    call('Anulacion prohibida asesor','PATCH','/ordenes/1/anular',403,{'motivo':'Cancelacion de prueba'},s)
    call('Registro prohibido jefe','POST','/ordenes',403,{'vehiculoId':1,'kilometraje':45000,'detalles':[{'servicioId':1,'cantidad':1}]},j)
    clientes=call('Clientes','GET','/clientes',200,token=a)
    servicios=call('Servicios','GET','/servicios',200,token=a)
    vehiculos=call('Vehiculos','GET','/vehiculos',200,token=a)
    assert len(clientes)>=6 and len(servicios)>=8 and len(vehiculos)>=8
    ids={x['codigo']:x['id'] for x in servicios};v=next(x for x in vehiculos if x['placa']=='AAA-111')
    call('Servicio inexistente','GET','/servicios/999999',404,token=a)
    call('Servicio invalido','POST','/servicios',400,{'codigo':'MAL','nombre':'x','precio':0,'duracionMin':1},a)
    service={'codigo':'SRV-099','nombre':'Servicio temporal de prueba','precio':12.35,'duracionMin':30}
    nuevo=call('Alta servicio','POST','/servicios',201,service,a);sid=nuevo['id']
    call('Codigo duplicado','POST','/servicios',409,service,a)
    service['precio']=20.15
    call('Editar servicio','PUT',f'/servicios/{sid}',200,service,a)
    call('Baja servicio','PATCH',f'/servicios/{sid}/estado',200,{'activo':False},a)
    call('Eliminar servicio sin uso','DELETE',f'/servicios/{sid}',204,token=a)
    call('Eliminar servicio usado','DELETE',f"/servicios/{ids['SRV-001']}",409,token=a)
    vehicle={'placa':'ZZT-999','marca':'Prueba','modelo':'Temporal','anio':2026,'clienteId':clientes[0]['id']}
    nuevo=call('Alta vehiculo','POST','/vehiculos',201,vehicle,a);vid=nuevo['id']
    call('Placa duplicada','POST','/vehiculos',409,vehicle,a)
    vehicle['modelo']='Editado';call('Editar vehiculo','PUT',f'/vehiculos/{vid}',200,vehicle,a)
    vehicle['clienteId']=999999;vehicle['placa']='ZZT-998';call('Cliente inexistente','POST','/vehiculos',404,vehicle,a)
    call('Eliminar vehiculo sin orden','DELETE',f'/vehiculos/{vid}',204,token=a)
    call('Eliminar vehiculo con orden','DELETE',f"/vehiculos/{v['id']}",409,token=a)
    call('Filtro cliente','GET',f"/vehiculos?clienteId={v['clienteId']}",200,token=a)
    orden={'vehiculoId':v['id'],'kilometraje':45000,'detalles':[{'servicioId':ids['SRV-001'],'cantidad':2}]}
    anterior=call('Conteo antes de rollback','GET','/ordenes',200,token=a)
    invalida={**orden,'detalles':[{'servicioId':ids['SRV-001'],'cantidad':1},{'servicioId':ids['SRV-002'],'cantidad':1},{'servicioId':ids['SRV-008'],'cantidad':1}]}
    call('Tercer detalle inactivo','POST','/ordenes',409,invalida,s)
    posterior=call('Conteo despues de rollback','GET','/ordenes',200,token=a)
    assert len(anterior)==len(posterior),'Se guardo una orden parcial'
    call('Kilometraje menor','POST','/ordenes',409,{**orden,'kilometraje':44000},s)
    call('Servicio repetido','POST','/ordenes',400,{**orden,'detalles':orden['detalles']*2},s)
    call('Cantidad invalida','POST','/ordenes',400,{**orden,'detalles':[{'servicioId':ids['SRV-001'],'cantidad':6}]},s)
    call('Orden vacia','POST','/ordenes',400,{**orden,'detalles':[]},s)
    report_before=call('Reporte previo','GET','/reportes/ingresos-por-servicio',200,token=j)
    creada=call('Registro atomico','POST','/ordenes',201,orden,s);oid=creada['id']
    assert creada['total']==next(x['precio'] for x in servicios if x['codigo']=='SRV-001')*2
    call('Detalle orden','GET',f'/ordenes/{oid}',200,token=s)
    today=date.today().isoformat()
    filtradas=call('Filtros combinados','GET',f'/ordenes?placa=AAA-111&estado=REGISTRADA&desde={today}&hasta={today}',200,token=s)
    assert any(o['id']==oid for o in filtradas)
    call('Motivo corto','PATCH',f'/ordenes/{oid}/anular',400,{'motivo':'No'},j)
    anulada=call('Anular mismo dia','PATCH',f'/ordenes/{oid}/anular',200,{'motivo':'Cancelacion de prueba automatizada'},j)
    assert anulada['estado']=='ANULADA'
    call('Reanular orden','PATCH',f'/ordenes/{oid}/anular',409,{'motivo':'Cancelacion de prueba automatizada'},j)
    report_after=call('Reporte excluye anuladas','GET','/reportes/ingresos-por-servicio',200,token=j)
    assert report_before==report_after
    call('Fecha invalida','GET','/ordenes?desde=incorrecto',400,token=a)
    call('Ruta inexistente','GET','/inexistente',404,token=a)
    Path('tests/resultados-api.json').write_text(json.dumps(results,ensure_ascii=False,indent=2),encoding='utf-8')
    print(f'{len(results)} comprobaciones correctas; rollback y total verificados.')
if __name__=='__main__':main()
