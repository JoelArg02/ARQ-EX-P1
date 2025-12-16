# 🐳 Docker Compose - Sistema Completo Banquito + Comercializadora

Este archivo Docker Compose levanta el sistema completo con bases de datos y backends.

## 📋 Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Network: app_network               │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────┐         ┌──────────────────────────┐  │
│  │  mysql_banquito  │         │  mysql_comercializadora  │  │
│  │  Puerto: 3306    │         │  Puerto: 3307 -> 3306    │  │
│  │  BD: banquito_db │         │  BD: comercializadora_db │  │
│  └────────┬─────────┘         └──────────┬───────────────┘  │
│           │                               │                  │
│           │                               │                  │
│  ┌────────▼──────────┐         ┌─────────▼──────────────┐  │
│  │ backend_banquito  │◄────────│backend_comercializadora│  │
│  │ Puerto: 8080      │  REST   │ Puerto: 8081           │  │
│  │ Admin: 4848       │  API    │ Admin: 4849            │  │
│  │ Payara Server     │         │ Payara Server          │  │
│  └───────────────────┘         └────────────────────────┘  │
│           ▲                              ▲                  │
│           │                              │                  │
└───────────┼──────────────────────────────┼──────────────────┘
            │                              │
         localhost:8080              localhost:8081
            │                              │
       ┌────▼──────┐                  ┌───▼───────┐
       │  Clientes │                  │  Clientes │
       │  Externos │                  │  Externos │
       └───────────┘                  └───────────┘
```

## 🚀 Comandos Principales

### Iniciar todo el sistema
```bash
docker-compose up -d
```

### Ver logs de todos los servicios
```bash
docker-compose logs -f
```

### Ver logs de un servicio específico
```bash
# Logs del backend banquito
docker-compose logs -f backend_banquito

# Logs del backend comercializadora
docker-compose logs -f backend_comercializadora

# Logs de las bases de datos
docker-compose logs -f mysql_banquito
docker-compose logs -f mysql_comercializadora
```

### Detener todos los servicios
```bash
docker-compose down
```

### Detener y eliminar todo (incluyendo volúmenes)
```bash
docker-compose down -v
```

### Reconstruir las imágenes
```bash
docker-compose build --no-cache
docker-compose up -d
```

### Reiniciar un servicio específico
```bash
docker-compose restart backend_banquito
docker-compose restart backend_comercializadora
```

## 📡 Endpoints Expuestos

### Backend Banquito
- **API REST:** http://localhost:8080/Ex_Banquito_RESTJava/api
- **Admin Console:** http://localhost:4848
- **Credenciales Admin:** admin / admin

### Backend Comercializadora
- **API REST:** http://localhost:8081/Ex_Comercializadora_RESTJava/api
- **Admin Console:** http://localhost:4849
- **Credenciales Admin:** admin / admin

### Bases de Datos MySQL

#### Banquito
- **Host:** localhost
- **Puerto:** 3306
- **Base de datos:** banquito_db
- **Usuario:** root
- **Contraseña:** root

#### Comercializadora
- **Host:** localhost
- **Puerto:** 3307
- **Base de datos:** comercializadora_db
- **Usuario:** root
- **Contraseña:** root

## 🔗 Comunicación entre Backends

El backend de **Comercializadora** se comunica con el backend de **Banquito** para:
- Verificar si un cliente es sujeto de crédito
- Consultar monto máximo de crédito
- Otorgar créditos

La comunicación se realiza mediante:
- **Variable de entorno:** `BANQUITO_HOST=backend_banquito:8080`
- **URL interna:** `http://backend_banquito:8080/Ex_Banquito_RESTJava/api/creditos`

## ⏱️ Tiempos de Inicio

Los servicios se inician en el siguiente orden:

1. **Bases de datos** (15-20 segundos)
   - Incluye healthchecks para garantizar disponibilidad
   
2. **Backend Banquito** (45-60 segundos)
   - Espera a que mysql_banquito esté healthy
   - Configura datasource automáticamente
   
3. **Backend Comercializadora** (45-60 segundos)
   - Espera a que mysql_comercializadora esté healthy
   - Espera a que backend_banquito esté iniciado
   - Configura datasource automáticamente

**Tiempo total estimado:** 2-3 minutos

## 🧪 Verificar que todo funciona

### 1. Verificar contenedores activos
```bash
docker-compose ps
```

Deberías ver 4 servicios en estado "Up":
- mysql_banquito
- mysql_comercializadora
- backend_banquito
- backend_comercializadora

### 2. Probar API Banquito
```bash
# Listar clientes
curl http://localhost:8080/Ex_Banquito_RESTJava/api/clientes

# Verificar sujeto de crédito
curl http://localhost:8080/Ex_Banquito_RESTJava/api/creditos/sujeto-credito/1234567890
```

### 3. Probar API Comercializadora
```bash
# Listar productos
curl http://localhost:8081/Ex_Comercializadora_RESTJava/api/productos

# Verificar comunicación con Banquito (a través de Comercializadora)
curl http://localhost:8081/Ex_Comercializadora_RESTJava/api/facturas/sujeto-credito/1234567890
```

## 🔍 Troubleshooting

### El backend no inicia
```bash
# Ver logs detallados
docker-compose logs -f backend_banquito
docker-compose logs -f backend_comercializadora

# Entrar al contenedor para debugging
docker exec -it backend_banquito bash
docker exec -it backend_comercializadora bash
```

### Error de conexión a la base de datos
```bash
# Verificar que las bases de datos estén healthy
docker-compose ps

# Reiniciar servicios
docker-compose restart mysql_banquito backend_banquito
docker-compose restart mysql_comercializadora backend_comercializadora
```

### Limpiar y empezar de cero
```bash
# Detener todo
docker-compose down -v

# Limpiar imágenes antiguas
docker system prune -a

# Reconstruir y levantar
docker-compose build --no-cache
docker-compose up -d
```

## 📊 Monitoreo

### Ver uso de recursos
```bash
docker stats
```

### Ver procesos dentro de un contenedor
```bash
docker top backend_banquito
docker top backend_comercializadora
```

## 🔧 Desarrollo

### Modificar código del backend

1. Editar archivos en `Ex_Banquito_RESTJava/src` o `Ex_Comercializadora_RESTJava/src`
2. Reconstruir la imagen:
   ```bash
   docker-compose build backend_banquito
   # o
   docker-compose build backend_comercializadora
   ```
3. Reiniciar el servicio:
   ```bash
   docker-compose up -d backend_banquito
   # o
   docker-compose up -d backend_comercializadora
   ```

### Hot reload (desarrollo local)

Para desarrollo activo, considera usar volúmenes de código:
```yaml
volumes:
  - ./Ex_Banquito_RESTJava/src:/app/src
```

## 📝 Notas Importantes

1. **Puertos diferentes:** Cada backend usa un puerto diferente (8080 vs 8081) para evitar conflictos
2. **Health checks:** Las bases de datos tienen health checks para garantizar que estén listas antes de iniciar los backends
3. **Depends on:** Los backends esperan a que sus bases de datos estén healthy
4. **Restart policy:** Los backends se reinician automáticamente si fallan
5. **Network compartida:** Todos los servicios están en la misma red Docker (app_network) para comunicarse

## 🎯 Casos de Uso

### Desarrollo
```bash
# Levantar solo bases de datos
docker-compose up -d mysql_banquito mysql_comercializadora

# Desarrollar localmente con tu IDE conectado a las bases de datos
```

### Testing
```bash
# Levantar todo el sistema
docker-compose up -d

# Ejecutar pruebas contra los endpoints
```

### Producción (con ajustes)
```bash
# Usar variables de entorno externas
# Configurar secrets para contraseñas
# Usar redes externas
# Configurar reverse proxy (nginx/traefik)
```

## 📞 Soporte

Si encuentras problemas:
1. Revisa los logs: `docker-compose logs -f`
2. Verifica el estado: `docker-compose ps`
3. Verifica la red: `docker network inspect 01servidor_app_network`
4. Verifica conectividad: `docker exec backend_comercializadora ping backend_banquito`
