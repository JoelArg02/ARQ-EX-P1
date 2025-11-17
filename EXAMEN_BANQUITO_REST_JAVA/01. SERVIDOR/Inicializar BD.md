## 📌 1️⃣ Crear el contenedor MySQL con Docker

> 🔁 Ajusta el nombre del contenedor si ya lo creaste con otro (`mysql-db`, por ejemplo).  
> Aquí se asume que el contenedor se llama **`mysql-db`**.

```sh
docker run -d   --name mysql-db   -e MYSQL_ROOT_PASSWORD=1234   -p 3306:3306   mysql:8
```

🔹 **Nombre del contenedor:** `mysql-db`  
🔹 **Usuario:** `root`  
🔹 **Password:** `1234`  
🔹 **Puerto expuesto:** `3306`

> ⚠ **IMPORTANTE:**  
> En archivos de configuración (como `persistence.xml`, `.env`, etc.), usa:
> ```txt
> user = root
> password = 1234
> ```

---

## 📌 2️⃣ Verificar que MySQL está funcionando

```sh
docker ps
```

Deberías ver un contenedor con nombre `mysql-db` y la imagen `mysql:8`.

Conéctate al MySQL dentro del contenedor:

```sh
docker exec -it mysql-db mysql -u root -p1234
```

O, si prefieres que te pida la contraseña:

```sh
docker exec -it mysql-db mysql -u root -p
```

---

## 📌 3️⃣ Copiar los scripts SQL al contenedor

Asumiendo que estás en la raíz del proyecto y tienes estas rutas en tu máquina host:

- `01. SERVIDOR/Ex_Banquito_RESTJava/database_script.sql`
- `01. SERVIDOR/Ex_Comercializadora_RESTJava/database_comercializadora_script.sql`

Ejecuta:

```sh
docker cp "01. SERVIDOR/Ex_Banquito_RESTJava/database_script.sql" mysql-db:/tmp database_script.sql

docker cp "01. SERVIDOR/Ex_Comercializadora_RESTJava/database_comercializadora_script.sql" mysql-db:/tmp/database_comercializadora_script.sql
```

Con esto, en el contenedor los archivos quedarán en:

- `/tmp/database_script.sql`
- `/tmp/database_comercializadora_script.sql`

---

## 📌 4️⃣ Ejecutar los scripts dentro del contenedor

### 🔹 Opción A: Ejecutar de forma interactiva (recomendado)

1. Entra al MySQL del contenedor:

```sh
docker exec -it mysql-db mysql -u root -p1234
```

2. Dentro del prompt de MySQL, ejecuta:

```sql
SOURCE /tmp/database_script.sql;
SOURCE /tmp/database_comercializadora_script.sql;
```

Esto va a:

- Crear la base `banquito_db` y todas sus tablas/datos (BANQUITO)
- Crear la base `comercializadora_db` y todas sus tablas/datos (COMERCIALIZADORA)

### 🔹 Opción B: Ejecutar con un solo comando (no interactivo)

Si prefieres no entrar al cliente MySQL y hacerlo directo:

```sh
docker exec -i mysql-db mysql -u root -p1234 < "01. SERVIDOR/Ex_Banquito_RESTJava/database_script.sql"

docker exec -i mysql-db mysql -u root -p1234 < "01. SERVIDOR/Ex_Comercializadora_RESTJava/database_comercializadora_script.sql"
```

> ✅ En esta opción **no necesitas `docker cp`**, porque el contenido se envía directamente al comando `mysql` dentro del contenedor.

---

## 📌 5️⃣ Consultas de verificación

Una vez ejecutados los scripts, valida que los datos estén cargados.

### 🏦 BANQUITO (Base: `banquito_db`)

Dentro de MySQL:

```sql
USE banquito_db;

SELECT 'CLIENTES' AS TABLA, COUNT(*) AS TOTAL FROM CLIENTE
UNION ALL SELECT 'CUENTAS', COUNT(*) FROM CUENTA
UNION ALL SELECT 'MOVIMIENTOS', COUNT(*) FROM MOVIMIENTO
UNION ALL SELECT 'CREDITOS', COUNT(*) FROM CREDITO;
```

Ver clientes y cuentas:

```sql
SELECT c.CEDULA, c.NOMBRE, c.ESTADO_CIVIL, cu.NUM_CUENTA, cu.SALDO
FROM CLIENTE c
INNER JOIN CUENTA cu ON c.CEDULA = cu.CEDULA
ORDER BY c.NOMBRE;
```

### 🏬 COMERCIALIZADORA (Base: `comercializadora_db`)

```sql
USE comercializadora_db;

SELECT * FROM Producto ORDER BY codigo;

SELECT 
    f.idFactura,
    c.nombre AS cliente,
    f.fecha,
    f.total,
    f.formaPago,
    f.idCreditoBanco
FROM Factura f
INNER JOIN ClienteCom c ON f.idCliente = c.idCliente
ORDER BY f.fecha DESC;
```

---

## 🔗 Relación entre ambas bases

- Las **cédulas** de `ClienteCom` (comercializadora) coinciden con las de `CLIENTE` (BanQuito).
- En `Factura.idCreditoBanco` se guarda el ID del crédito generado en BanQuito (`CREDITO.ID_CREDITO`).
- El servidor de la comercializadora consulta al servicio REST del banco para:
  - Validar crédito
  - Consultar tabla de amortización
  - Registrar pagos, etc.

---

## 🧪 Pruebas de los servicios REST

Una vez que las BD están listas y el servidor Java está desplegado, puedes probar (ejemplos):

- BANQUITO:
  - `GET /api/clientes`
  - `GET /api/cuentas/{cedula}`
  - `GET /api/movimientos/{numCuenta}`
  - `POST /api/creditos/otorgar`

- COMERCIALIZADORA:
  - `GET /api/productos`
  - `POST /api/facturas`
  - `GET /api/facturas/{id}`

---

## 🚨 Errores comunes y soluciones

| Error | Causa probable | Solución |
|------|----------------|----------|
| `Access denied for user 'root'` | Password incorrecta | Verificar `MYSQL_ROOT_PASSWORD` (1234) y/o reiniciar contenedor |
| `Unknown database 'banquito_db'` | Script no ejecutado o falló | Volver a ejecutar `SOURCE /tmp/database_script.sql;` |
| `Unknown database 'comercializadora_db'` | Script no ejecutado | Ejecutar `SOURCE /tmp/database_comercializadora_script.sql;` |
| `Table already exists` | Script ejecutado dos veces | Borrar BD con `DROP DATABASE ...;` o ignorar si es esperado |
| `Cannot connect to MySQL on 3306` | Contenedor caído o puerto ocupado | Revisar `docker ps`, logs con `docker logs mysql-db`, y puertos |

---

## 🎯 Resultado esperado

Al final de estos pasos deberías tener:

- ✅ Contenedor Docker `mysql-db` corriendo con MySQL 8  
- ✅ Base de datos `banquito_db` creada y poblada  
- ✅ Base de datos `comercializadora_db` creada y poblada  
- ✅ Scripts listos para que los servicios REST Java funcionen correctamente

