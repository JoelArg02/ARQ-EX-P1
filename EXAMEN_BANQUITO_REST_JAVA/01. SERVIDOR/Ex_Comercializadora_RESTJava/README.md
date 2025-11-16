# COMERCIALIZADORA DE ELECTRODOMÉSTICOS - Servidor REST

## Descripción del Proyecto

Sistema REST desarrollado en **Java con Jakarta EE 10** que gestiona la comercialización de electrodomésticos con dos formas de pago:

1. **EFECTIVO**: Pago directo sin financiamiento
2. **CRÉDITO DIRECTO**: Integración con el **Banco BanQuito** para financiamiento

El sistema **consume** los servicios REST del Banco BanQuito para validar clientes, consultar montos máximos, otorgar créditos y obtener tablas de amortización.

---

## Arquitectura del Sistema

### Patrón de Diseño: MVC (Model-View-Controller)

```
┌─────────────────────────────────────────────────────────┐
│              CLIENTE REST (Postman, cURL)               │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP/JSON
┌────────────────────┴────────────────────────────────────┐
│                 CAPA CONTROLLER (JAX-RS)                │
│  - FacturaResource: POST /api/facturas                  │
│                     GET /api/facturas/{id}/amortizacion │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────┴────────────────────────────────────┐
│                   CAPA SERVICE (EJB)                    │
│  - FacturaService: Lógica de negocio de facturación    │
│                    Validaciones de stock y montos       │
│                    Integración con BanQuito (REST)      │
└────────────────┬───────────────────┬────────────────────┘
                 │                   │
    ┌────────────┴────────┐   ┌──────┴──────────────────┐
    │   REPOSITORIES      │   │  REST CLIENT            │
    │   (JPA EntityMgr)   │   │  BanquitoCreditoClient  │
    │  - ProductoRepo     │   │  - esSujetoCredito()    │
    │  - ClienteComRepo   │   │  - obtenerMontoMax()    │
    │  - FacturaRepo      │   │  - otorgarCredito()     │
    └──────────┬──────────┘   │  - obtenerAmortizacion()│
               │              └─────────┬─────────────────┘
    ┌──────────┴──────────┐            │ HTTP REST
    │    ENTITIES (JPA)   │            │
    │  - Producto         │   ┌────────┴─────────────────┐
    │  - ClienteCom       │   │   BANCO BANQUITO REST    │
    │  - Factura          │   │   localhost:8080         │
    │  - DetalleFactura   │   │   /Ex_Banquito_RESTJava  │
    └──────────┬──────────┘   └──────────────────────────┘
               │
    ┌──────────┴──────────┐
    │    MySQL Database   │
    │  comercializadora_db│
    └─────────────────────┘
```

---

## Stack Tecnológico

| Componente | Tecnología | Versión |
|------------|------------|---------|
| **Lenguaje** | Java | 17+ |
| **Framework** | Jakarta EE | 10 |
| **API REST** | JAX-RS (Jakarta RESTful WS) | 3.1 |
| **Persistencia** | JPA (Jakarta Persistence) | 3.1 |
| **ORM** | Hibernate | 6.2.5.Final |
| **Transacciones** | EJB (Enterprise JavaBeans) | 4.0 |
| **Base de Datos** | MySQL | 8.0+ |
| **Serialización JSON** | Jackson | 2.15.2 |
| **Servidor** | GlassFish / Payara | 7+ |
| **Gestión de Dependencias** | Maven | 3.8+ |

---

## Modelo de Datos

### Diagrama ER

```
┌─────────────────┐
│   ClienteCom    │
├─────────────────┤
│ idCliente (PK)  │
│ cedula (UNIQUE) │
│ nombre          │
│ direccion       │
│ telefono        │
└────────┬────────┘
         │ 1
         │
         │ N
┌────────┴────────┐       N ┌──────────────────┐
│    Factura      │─────────│ DetalleFactura   │
├─────────────────┤         ├──────────────────┤
│ idFactura (PK)  │         │ idDetalle (PK)   │
│ idCliente (FK)  │         │ idFactura (FK)   │
│ fecha           │         │ idProducto (FK)  │
│ total           │         │ cantidad         │
│ formaPago       │         │ precioUnitario   │
│ idCreditoBanco  │         │ subtotal         │
└─────────────────┘         └─────────┬────────┘
                                      │ N
                                      │
                                      │ 1
                            ┌─────────┴────────┐
                            │    Producto      │
                            ├──────────────────┤
                            │ idProducto (PK)  │
                            │ codigo (UNIQUE)  │
                            │ nombre           │
                            │ precio           │
                            │ stock            │
                            └──────────────────┘
```

---

## API REST Endpoints

### Base URL
```
http://localhost:8080/Ex_Comercializadora_RESTJava/api
```

### 1. Health Check
```http
GET /health
```

**Respuesta 200 OK:**
```json
{
  "status": "OK",
  "message": "Comercializadora REST Server is running"
}
```

---

### 2. Crear Factura (Efectivo o Crédito)
```http
POST /facturas
Content-Type: application/json
```

**Body - Pago en EFECTIVO:**
```json
{
  "cedulaCliente": "1234567890",
  "formaPago": "EFECTIVO",
  "items": [
    {
      "idProducto": 1,
      "cantidad": 1
    },
    {
      "idProducto": 4,
      "cantidad": 2
    }
  ]
}
```

**Body - Pago a CRÉDITO:**
```json
{
  "cedulaCliente": "1234567890",
  "formaPago": "CREDITO_DIRECTO",
  "numeroCuotas": 9,
  "items": [
    {
      "idProducto": 6,
      "cantidad": 1
    }
  ]
}
```

**Respuesta 201 Created (Efectivo):**
```json
{
  "exitoso": true,
  "mensaje": "Factura creada exitosamente (Pago en efectivo)",
  "idFactura": 1,
  "cedulaCliente": "1234567890",
  "nombreCliente": "Juan Pérez",
  "fecha": "2024-01-15",
  "total": 1217.00,
  "formaPago": "EFECTIVO",
  "detalles": [
    {
      "codigoProducto": "REF001",
      "nombreProducto": "Refrigeradora LG 14 pies",
      "cantidad": 1,
      "precioUnitario": 899.00,
      "subtotal": 899.00
    },
    {
      "codigoProducto": "MIC001",
      "nombreProducto": "Microondas Panasonic 1.2 cu ft",
      "cantidad": 2,
      "precioUnitario": 159.00,
      "subtotal": 318.00
    }
  ]
}
```

**Respuesta 201 Created (Crédito):**
```json
{
  "exitoso": true,
  "mensaje": "Factura creada exitosamente (Pago a crédito)",
  "idFactura": 2,
  "cedulaCliente": "1234567890",
  "nombreCliente": "Juan Pérez",
  "fecha": "2024-01-15",
  "total": 1299.00,
  "formaPago": "CREDITO_DIRECTO",
  "idCreditoBanco": 105,
  "infoCredito": {
    "idCredito": 105,
    "numeroCuotas": 9,
    "tasaInteres": 16.00,
    "tablaAmortizacion": [
      {
        "numeroCuota": 1,
        "valorCuota": 153.84,
        "interesPagado": 17.32,
        "capitalPagado": 136.52,
        "saldoRestante": 1162.48
      }
    ]
  },
  "detalles": [...]
}
```

**Respuesta 400 Bad Request (Error):**
```json
{
  "exitoso": false,
  "mensaje": "Cliente no es sujeto de crédito: No cumple con historial de 6 meses"
}
```

---

### 3. Obtener Tabla de Amortización
```http
GET /facturas/{idFactura}/amortizacion
```

**Respuesta 200 OK:**
```json
[
  {
    "numeroCuota": 1,
    "valorCuota": 153.84,
    "interesPagado": 17.32,
    "capitalPagado": 136.52,
    "saldoRestante": 1162.48
  },
  {
    "numeroCuota": 2,
    "valorCuota": 153.84,
    "interesPagado": 15.50,
    "capitalPagado": 138.34,
    "saldoRestante": 1024.14
  }
]
```

---

## Flujo de Negocio: Pago a Crédito

```
1. Cliente solicita compra a crédito
           ↓
2. Sistema valida cliente en BD local
           ↓
3. REST Client → BanQuito: esSujetoCredito(cedula)
           ↓
4. REST Client → BanQuito: obtenerMontoMaximo(cedula)
           ↓
5. Validar: total ≤ montoMaximo
           ↓
6. REST Client → BanQuito: otorgarCredito(cedula, monto, plazo)
           ↓
7. Guardar Factura con idCreditoBanco
           ↓
8. Actualizar stock de productos
           ↓
9. Retornar factura con tabla de amortización
```

---

## Configuración y Despliegue

### Requisitos Previos

1. **JDK 17+** instalado
2. **Maven 3.8+** instalado
3. **MySQL 8.0+** en ejecución
4. **GlassFish 7+ / Payara 6+** instalado
5. **Servidor BanQuito** ejecutándose en `http://localhost:8080/Ex_Banquito_RESTJava`

### Paso 1: Configurar Base de Datos

```bash
mysql -u root -p < database_comercializadora_script.sql
```

Esto crea:
- Base de datos `comercializadora_db`
- 4 tablas (Producto, ClienteCom, Factura, DetalleFactura)
- Datos de prueba

### Paso 2: Configurar DataSource en GlassFish

**Via Admin Console (http://localhost:4848):**

1. **JDBC Connection Pool:**
   - Nombre: `ComercializadoraPool`
   - Resource Type: `javax.sql.DataSource`
   - Driver: `com.mysql.cj.jdbc.Driver`
   - URL: `jdbc:mysql://localhost:3306/comercializadora_db`
   - User: `root`
   - Password: `[tu_password]`

2. **JDBC Resource:**
   - JNDI Name: `java:/comercializadoraDS`
   - Pool Name: `ComercializadoraPool`

**Via asadmin CLI:**
```bash
asadmin create-jdbc-connection-pool --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource \
  --restype javax.sql.DataSource \
  --property user=root:password=tu_password:url=jdbc\\:mysql\\://localhost\\:3306/comercializadora_db \
  ComercializadoraPool

asadmin create-jdbc-resource --connectionpoolid ComercializadoraPool java:/comercializadoraDS
```

### Paso 3: Compilar Proyecto

```bash
cd Ex_Comercializadora_RESTJava
mvn clean package
```

Genera: `target/Ex_Comercializadora_RESTJava-1.0-SNAPSHOT.war`

### Paso 4: Desplegar en GlassFish

**Opción 1 - Admin Console:**
1. Ir a `Applications` → `Deploy`
2. Seleccionar el `.war`
3. Context Root: `/Ex_Comercializadora_RESTJava`
4. Desplegar

**Opción 2 - asadmin CLI:**
```bash
asadmin deploy --contextroot /Ex_Comercializadora_RESTJava target/Ex_Comercializadora_RESTJava-1.0-SNAPSHOT.war
```

### Paso 5: Verificar

```bash
curl http://localhost:8080/Ex_Comercializadora_RESTJava/api/health
```

Respuesta esperada:
```json
{"status": "OK", "message": "Comercializadora REST Server is running"}
```

---

## Casos de Prueba

### Caso 1: Factura en Efectivo

```bash
curl -X POST http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaCliente": "1234567890",
    "formaPago": "EFECTIVO",
    "items": [
      {"idProducto": 1, "cantidad": 1}
    ]
  }'
```

### Caso 2: Factura a Crédito (9 meses)

```bash
curl -X POST http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas \
  -H "Content-Type: application/json" \
  -d '{
    "cedulaCliente": "1234567890",
    "formaPago": "CREDITO_DIRECTO",
    "numeroCuotas": 9,
    "items": [
      {"idProducto": 6, "cantidad": 1}
    ]
  }'
```

### Caso 3: Obtener Amortización

```bash
curl http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas/2/amortizacion
```

---

## Reglas de Negocio

### Validaciones de Facturación

1. **Cliente Existente**: Debe estar registrado en `ClienteCom`
2. **Stock Disponible**: Cada producto debe tener stock suficiente
3. **Monto Positivo**: El total debe ser mayor a cero

### Validaciones de Crédito (delegadas a BanQuito)

1. **Sujeto de Crédito**:
   - Historial de movimientos ≥ 6 meses
   - Promedio depósitos > promedio retiros
   - No tener crédito activo
   - No estar en mora

2. **Monto Máximo**:
   - `MontoMax = ((PromDep - PromRet) * 60%) * 9 meses`

3. **Amortización**:
   - Sistema francés (cuota fija)
   - Tasa: 16% anual
   - Plazo: 6, 9 o 12 meses

---

## Integración con BanQuito

### Cliente REST: `BanquitoCreditoClient`

**Clase:** `ec.edu.pinza.ex_comercializadora_restjava.client.BanquitoCreditoClient`

**Métodos:**

```java
// 1. Validar sujeto de crédito
SujetoCreditoResponseDTO esSujetoCredito(String cedula)

// 2. Obtener monto máximo
MontoMaximoResponseDTO obtenerMontoMaximo(String cedula)

// 3. Otorgar crédito
OtorgarCreditoResponseDTO otorgarCredito(String cedula, BigDecimal monto, int plazo)

// 4. Tabla de amortización
List<CuotaDTO> obtenerTablaAmortizacion(Integer idCredito)
```

**Base URL:** `http://localhost:8080/Ex_Banquito_RESTJava/api/creditos`

---

## Estructura del Proyecto

```
Ex_Comercializadora_RESTJava/
├── pom.xml
├── database_comercializadora_script.sql
├── README.md
└── src/main/
    ├── java/ec/edu/pinza/ex_comercializadora_restjava/
    │   ├── JakartaRestConfiguration.java    # Config JAX-RS
    │   ├── entities/
    │   │   ├── Producto.java
    │   │   ├── ClienteCom.java
    │   │   ├── Factura.java
    │   │   └── DetalleFactura.java
    │   ├── repositories/
    │   │   ├── ProductoRepository.java
    │   │   ├── ClienteComRepository.java
    │   │   └── FacturaRepository.java
    │   ├── service/
    │   │   └── FacturaService.java          # Lógica de negocio
    │   ├── client/
    │   │   └── BanquitoCreditoClient.java   # REST client
    │   ├── dto/
    │   │   ├── CrearFacturaRequest.java
    │   │   ├── FacturaResponse.java
    │   │   ├── SujetoCreditoResponseDTO.java
    │   │   ├── MontoMaximoResponseDTO.java
    │   │   └── OtorgarCreditoResponseDTO.java
    │   └── resources/
    │       └── FacturaResource.java          # Controlador REST
    ├── resources/META-INF/
    │   └── persistence.xml
    └── webapp/
        ├── index.html
        └── WEB-INF/
            ├── beans.xml
            └── web.xml
```

---

## Troubleshooting

### Error: "Cliente no es sujeto de crédito"

**Causa:** El cliente no cumple las reglas del banco  
**Solución:** Verificar que el cliente tenga:
- Cuenta en BanQuito
- ≥ 6 meses de historial
- Promedio depósitos > retiros
- Sin crédito activo

### Error: "Monto solicitado supera el monto máximo"

**Causa:** La compra excede la capacidad crediticia  
**Solución:** 
1. Consultar monto máximo: `GET /api/creditos/monto-maximo/{cedula}`
2. Reducir items o cantidad
3. Sugerir pago parcial en efectivo

### Error: "Stock insuficiente"

**Causa:** Producto sin existencias  
**Solución:** Consultar stock en BD: `SELECT stock FROM Producto WHERE idProducto = ?`

### Error: "Connection refused - BanQuito"

**Causa:** Servidor BanQuito no está ejecutándose  
**Solución:** Iniciar servidor BanQuito en puerto 8080

---

## Autor

**Ing. Fernando Pinza**  
Arquitectura de Software - Examen Práctico  
Universidad: [Tu Universidad]

---

## Licencia

Proyecto académico - Uso educativo exclusivamente
