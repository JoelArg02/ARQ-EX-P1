# 🎯 GUÍA COMPLETA DE PRUEBAS - SISTEMA BANQUITO Y COMERCIALIZADORA

Esta guía te explica paso a paso cómo funciona el sistema completo y cómo probar todos los endpoints.

---

## 📋 ÍNDICE

1. [Arquitectura del Sistema](#arquitectura-del-sistema)
2. [Servidor BanQuito - API REST](#servidor-banquito---api-rest)
3. [Servidor Comercializadora - API REST](#servidor-comercializadora---api-rest)
4. [Flujos de Prueba Completos](#flujos-de-prueba-completos)
5. [Casos de Uso Reales](#casos-de-uso-reales)
6. [Troubleshooting](#troubleshooting)

---

## 🏗️ ARQUITECTURA DEL SISTEMA

### Componentes

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENTE (Postman/Browser)                 │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ HTTP/JSON
                         ▼
┌─────────────────────────────────────────────────────────────┐
│         COMERCIALIZADORA REST API (Puerto 8080)             │
│  Contexto: /Ex_Comercializadora_RESTJava                    │
│  • Gestión de facturas (efectivo/crédito)                   │
│  • Gestión de inventario (productos)                        │
│  • Clientes de la tienda                                    │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ Consume servicios REST
                         ▼
┌─────────────────────────────────────────────────────────────┐
│            BANQUITO REST API (Puerto 8080)                  │
│  Contexto: /Ex_Banquito_RESTJava                            │
│  • Validación de sujeto de crédito                          │
│  • Cálculo de monto máximo autorizado                       │
│  • Otorgamiento de créditos                                 │
│  • Generación de tabla de amortización                      │
└────────────────────────┬────────────────────────────────────┘
                         │
                         │ JDBC
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              MySQL (Docker - Puerto 3306)                   │
│  • banquito_bd (clientes, cuentas, movimientos, créditos)   │
│  • comercializadora_db (productos, facturas, clientes)      │
└─────────────────────────────────────────────────────────────┘
```

### Flujo de Datos

1. **Cliente navega** en la tienda de electrodomésticos
2. **Comercializadora valida** stock y precios
3. Si es **crédito**, llama a **BanQuito** para:
   - ✅ Validar si es sujeto de crédito
   - 💰 Calcular monto máximo autorizado
   - 📄 Solicitar otorgamiento del crédito
4. **BanQuito** retorna ID del crédito y tabla de amortización
5. **Comercializadora** guarda factura con referencia al crédito
6. **Stock se actualiza** automáticamente

---

## 🏦 SERVIDOR BANQUITO - API REST

**Base URL**: `http://localhost:8080/Ex_Banquito_RESTJava/api`

### 1️⃣ VALIDAR SUJETO DE CRÉDITO

**Endpoint**: `GET /creditos/sujeto-credito/{cedula}`

**¿Qué hace?** Valida si un cliente califica para un crédito según reglas de negocio.

**Reglas de validación**:
- ✅ Cliente debe estar registrado en el banco
- ✅ Debe tener al menos 1 depósito en el último mes
- ✅ Si es casado, debe tener 25+ años
- ✅ NO debe tener créditos activos (solo CANCELADO está permitido)

**Ejemplo**:
```http
GET http://localhost:8080/Ex_Banquito_RESTJava/api/creditos/sujeto-credito/1750123456
```

**Respuesta exitosa**:
```json
{
  "esSujetoCredito": true,
  "cedula": "1750123456",
  "nombreCliente": "Juan Carlos Pérez López",
  "motivo": "Cliente calificado para crédito"
}
```

**Respuesta cuando NO califica**:
```json
{
  "esSujetoCredito": false,
  "cedula": "1750123456",
  "nombreCliente": "Juan Carlos Pérez López",
  "motivo": "El cliente ya tiene un crédito activo"
}
```

---

### 2️⃣ OBTENER MONTO MÁXIMO DE CRÉDITO

**Endpoint**: `GET /creditos/monto-maximo/{cedula}`

**¿Qué hace?** Calcula el monto máximo que el banco puede prestar al cliente.

**Fórmula**:
```
MontoMax = ((Promedio_Depósitos_3M - Promedio_Retiros_3M) × 60%) × 9
```

**Ejemplo**:
```http
GET http://localhost:8080/Ex_Banquito_RESTJava/api/creditos/monto-maximo/1750123456
```

**Respuesta**:
```json
{
  "cedula": "1750123456",
  "nombreCliente": "Juan Carlos Pérez López",
  "montoMaximo": 6007.50,
  "promedioDepositos": 1850.00,
  "promedioRetiros": 740.00,
  "calculoBasado": "Últimos 3 meses de movimientos"
}
```

**Interpretación**:
- Cliente tiene depósitos promedio de $1,850
- Cliente tiene retiros promedio de $740
- Diferencia: $1,110
- Base: $1,110 × 60% = $666
- **Monto máximo**: $666 × 9 = **$6,007.50**

---

### 3️⃣ OTORGAR CRÉDITO

**Endpoint**: `POST /creditos/otorgar`

**¿Qué hace?** Crea un crédito en el sistema bancario y genera tabla de amortización.

**Body (JSON)**:
```json
{
  "cedula": "1750123456",
  "precioElectrodomestico": 850.00,
  "numeroCuotas": 6
}
```

**Parámetros**:
- `cedula`: Cédula del cliente (debe existir en BD)
- `precioElectrodomestico`: Monto del crédito solicitado
- `numeroCuotas`: Plazo en meses (mínimo 3, máximo 24)

**Ejemplo completo**:
```http
POST http://localhost:8080/Ex_Banquito_RESTJava/api/creditos/otorgar
Content-Type: application/json

{
  "cedula": "1750456789",
  "precioElectrodomestico": 850,
  "numeroCuotas": 6
}
```

**Respuesta exitosa**:
```json
{
  "aprobado": true,
  "cedula": "1750456789",
  "fechaOtorgamiento": "2025-11-16",
  "idCredito": 9,
  "mensaje": "Crédito aprobado exitosamente",
  "montoAprobado": 850.00,
  "montoMaximoAutorizado": 6007.50,
  "plazoMeses": 6,
  "tasaInteresAnual": 16.00,
  "tablaAmortizacion": [
    {
      "numeroCuota": 1,
      "valorCuota": 148.35,
      "interesPagado": 11.33,
      "capitalPagado": 137.02,
      "saldoRestante": 712.98
    },
    {
      "numeroCuota": 2,
      "valorCuota": 148.35,
      "interesPagado": 9.51,
      "capitalPagado": 138.84,
      "saldoRestante": 574.14
    },
    ...
  ]
}
```

**Fórmula de cuota (sistema francés)**:
```
Cuota = Monto × (i × (1 + i)^n) / ((1 + i)^n - 1)

Donde:
  i = Tasa mensual = 16% / 12 = 1.333%
  n = Número de cuotas
```

**Validaciones**:
- ❌ Cliente no existe → Error 400
- ❌ Cliente no es sujeto de crédito → Error 400
- ❌ Monto excede límite autorizado → Error 400
- ❌ Número de cuotas inválido (< 3 o > 24) → Error 400
- ❌ Cliente ya tiene crédito activo → Error 400

---

### 4️⃣ CONSULTAR TABLA DE AMORTIZACIÓN

**Endpoint**: `GET /creditos/{idCredito}/amortizacion`

**¿Qué hace?** Obtiene la tabla de amortización de un crédito ya otorgado.

**Ejemplo**:
```http
GET http://localhost:8080/Ex_Banquito_RESTJava/api/creditos/1/amortizacion
```

**Respuesta**:
```json
{
  "idCredito": 1,
  "cedula": "1750345678",
  "nombreCliente": "Pedro Antonio Morales Cruz",
  "montoAprobado": 15000.00,
  "plazoMeses": 18,
  "tasaInteresAnual": 16.00,
  "fechaOtorgamiento": "2024-08-15",
  "estado": "CANCELADO",
  "tablaAmortizacion": [...]
}
```

---

## 🛒 SERVIDOR COMERCIALIZADORA - API REST

**Base URL**: `http://localhost:8080/Ex_Comercializadora_RESTJava/api`

### 1️⃣ CREAR FACTURA - PAGO EN EFECTIVO

**Endpoint**: `POST /facturas`

**¿Qué hace?** Registra una venta pagada en efectivo.

**Body (JSON)**:
```json
{
  "cedulaCliente": "1750123456",
  "formaPago": "EFECTIVO",
  "items": [
    {
      "idProducto": 1,
      "cantidad": 1
    },
    {
      "idProducto": 9,
      "cantidad": 1
    }
  ]
}
```

**Parámetros**:
- `cedulaCliente`: Cédula del cliente (debe existir en `ClienteCom`)
- `formaPago`: "EFECTIVO" o "CREDITO_DIRECTO"
- `items`: Array de productos con cantidad

**Ejemplo completo**:
```http
POST http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas
Content-Type: application/json

{
  "cedulaCliente": "1750123456",
  "formaPago": "EFECTIVO",
  "items": [
    {
      "idProducto": 1,
      "cantidad": 1
    }
  ]
}
```

**Respuesta exitosa**:
```json
{
  "exitoso": true,
  "mensaje": "Factura creada exitosamente (Pago en efectivo)",
  "idFactura": 5,
  "cedulaCliente": "1750123456",
  "nombreCliente": "Juan Carlos Pérez López",
  "fecha": "2025-11-16",
  "total": 899.00,
  "formaPago": "EFECTIVO",
  "detalles": [
    {
      "codigoProducto": "REF001",
      "nombreProducto": "Refrigeradora LG 14 pies",
      "cantidad": 1,
      "precioUnitario": 899.00,
      "subtotal": 899.00
    }
  ]
}
```

**Proceso interno**:
1. ✅ Valida que cliente exista
2. ✅ Valida que productos existan
3. ✅ Valida stock suficiente
4. ✅ Calcula total
5. ✅ Crea factura y detalles
6. ✅ **Actualiza stock automáticamente**

---

### 2️⃣ CREAR FACTURA - PAGO A CRÉDITO

**Endpoint**: `POST /facturas`

**¿Qué hace?** Registra una venta a crédito integrando con BanQuito.

**Body (JSON)**:
```json
{
  "cedulaCliente": "1750456789",
  "formaPago": "CREDITO_DIRECTO",
  "numeroCuotas": 6,
  "items": [
    {
      "idProducto": 6,
      "cantidad": 1
    }
  ]
}
```

**Parámetros adicionales**:
- `numeroCuotas`: Plazo del crédito (3-24 meses)

**Ejemplo completo**:
```http
POST http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas
Content-Type: application/json

{
  "cedulaCliente": "1750456789",
  "formaPago": "CREDITO_DIRECTO",
  "numeroCuotas": 12,
  "items": [
    {
      "idProducto": 6,
      "cantidad": 1
    }
  ]
}
```

**Respuesta exitosa**:
```json
{
  "exitoso": true,
  "mensaje": "Factura creada exitosamente (Pago a crédito)",
  "idFactura": 6,
  "cedulaCliente": "1750456789",
  "nombreCliente": "Ana Sofía Ramírez Flores",
  "fecha": "2025-11-16",
  "total": 1299.00,
  "formaPago": "CREDITO_DIRECTO",
  "idCreditoBanco": 10,
  "detalles": [
    {
      "codigoProducto": "TV001",
      "nombreProducto": "Smart TV LG 55 pulgadas",
      "cantidad": 1,
      "precioUnitario": 1299.00,
      "subtotal": 1299.00
    }
  ],
  "infoCredito": {
    "idCredito": 10,
    "numeroCuotas": 12,
    "tasaInteres": 16.00,
    "tablaAmortizacion": [...]
  }
}
```

**Proceso interno (flujo completo)**:
1. ✅ Valida cliente y productos
2. ✅ Calcula total de la factura
3. 🏦 **Llama a BanQuito**: `GET /creditos/sujeto-credito/{cedula}`
   - Si NO califica → Error 400
4. 🏦 **Llama a BanQuito**: `GET /creditos/monto-maximo/{cedula}`
   - Si total > montoMaximo → Error 400
5. 🏦 **Llama a BanQuito**: `POST /creditos/otorgar`
   - Si no aprueba → Error 400
6. ✅ Crea factura con `idCreditoBanco` referenciado
7. ✅ Crea detalles de factura
8. ✅ **Actualiza stock automáticamente**
9. ✅ Retorna factura + tabla amortización

---

### 3️⃣ CONSULTAR TABLA DE AMORTIZACIÓN

**Endpoint**: `GET /facturas/{idFactura}/amortizacion`

**¿Qué hace?** Obtiene la tabla de amortización de una factura a crédito.

**Ejemplo**:
```http
GET http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas/3/amortizacion
```

**Respuesta**:
```json
[
  {
    "numeroCuota": 1,
    "valorCuota": 148.35,
    "interesPagado": 11.33,
    "capitalPagado": 137.02,
    "saldoRestante": 712.98
  },
  ...
]
```

**Validaciones**:
- ❌ Factura no existe → Error 404
- ❌ Factura es de EFECTIVO (no tiene crédito) → Error 404

---

## 🧪 FLUJOS DE PRUEBA COMPLETOS

### FLUJO 1: COMPRA SIMPLE EN EFECTIVO

**Historia**: "Como cliente quiero comprar una plancha pagando en efectivo"

```http
### PASO 1: Verificar que el servidor esté activo
GET http://localhost:8080/Ex_Comercializadora_RESTJava/api/health

### PASO 2: Crear factura
POST http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas
Content-Type: application/json

{
  "cedulaCliente": "1750123456",
  "formaPago": "EFECTIVO",
  "items": [
    {
      "idProducto": 9,
      "cantidad": 1
    }
  ]
}
```

**Resultado esperado**:
- ✅ Status 201 Created
- ✅ Factura guardada en BD
- ✅ Stock actualizado: Plancha (50 → 49)
- ✅ Total: $45.00

---

### FLUJO 2: COMPRA A CRÉDITO (CASO EXITOSO)

**Historia**: "Como cliente quiero comprar un Smart TV a crédito en 12 cuotas"

```http
### PASO 1: Validar que el cliente sea sujeto de crédito
GET http://localhost:8080/Ex_Banquito_RESTJava/api/creditos/sujeto-credito/1750123456

# Debe retornar: "esSujetoCredito": true

### PASO 2: Consultar monto máximo autorizado
GET http://localhost:8080/Ex_Banquito_RESTJava/api/creditos/monto-maximo/1750123456

# Debe retornar montoMaximo > 1299 (precio del TV)

### PASO 3: Crear factura a crédito (esto internamente hace los pasos 1 y 2)
POST http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas
Content-Type: application/json

{
  "cedulaCliente": "1750123456",
  "formaPago": "CREDITO_DIRECTO",
  "numeroCuotas": 12,
  "items": [
    {
      "idProducto": 6,
      "cantidad": 1
    }
  ]
}

### PASO 4: Consultar tabla de amortización
# Usar el idFactura retornado en el paso 3
GET http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas/7/amortizacion
```

**Resultado esperado**:
- ✅ Crédito creado en BanQuito (tabla `CREDITO`)
- ✅ Cuotas creadas en BanQuito (tabla `CUOTA_AMORTIZACION`)
- ✅ Factura creada en Comercializadora con `idCreditoBanco`
- ✅ Stock actualizado: Smart TV 55" (8 → 7)
- ✅ Total: $1,299.00
- ✅ Cuota mensual: ~$119.81 × 12 meses

---

### FLUJO 3: COMPRA A CRÉDITO (MONTO EXCEDE LÍMITE)

**Historia**: "Cliente intenta comprar 10 TVs pero no tiene capacidad crediticia"

```http
POST http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas
Content-Type: application/json

{
  "cedulaCliente": "1750123456",
  "formaPago": "CREDITO_DIRECTO",
  "numeroCuotas": 12,
  "items": [
    {
      "idProducto": 6,
      "cantidad": 10
    }
  ]
}
```

**Resultado esperado**:
- ❌ Status 400 Bad Request
- ❌ Mensaje: "Monto solicitado ($12,990) supera el monto máximo disponible ($6,007.50)"
- ❌ No se crea factura
- ❌ No se actualiza stock

---

### FLUJO 4: COMPRA CON STOCK INSUFICIENTE

**Historia**: "Cliente intenta comprar 100 refrigeradoras pero solo hay 15"

```http
POST http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas
Content-Type: application/json

{
  "cedulaCliente": "1750123456",
  "formaPago": "EFECTIVO",
  "items": [
    {
      "idProducto": 1,
      "cantidad": 100
    }
  ]
}
```

**Resultado esperado**:
- ❌ Status 400 Bad Request
- ❌ Mensaje: "Stock insuficiente para: Refrigeradora LG 14 pies (disponible: 15, solicitado: 100)"
- ❌ No se crea factura
- ❌ No se actualiza stock

---

## 📊 CASOS DE USO REALES

### CASO 1: FAMILIA COMPRA ELECTRODOMÉSTICOS PARA CASA NUEVA

**Contexto**: Juan Carlos quiere comprar refrigeradora, cocina y lavadora a crédito.

```http
POST http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas
Content-Type: application/json

{
  "cedulaCliente": "1750123456",
  "formaPago": "CREDITO_DIRECTO",
  "numeroCuotas": 18,
  "items": [
    {
      "idProducto": 1,
      "cantidad": 1
    },
    {
      "idProducto": 3,
      "cantidad": 1
    },
    {
      "idProducto": 2,
      "cantidad": 1
    }
  ]
}
```

**Total**: $899 + $489 + $749 = **$2,137**

**Validaciones automáticas**:
1. ¿Es sujeto de crédito? → SÍ (tiene depósitos recientes, no tiene crédito activo)
2. ¿Monto máximo? → $6,007.50 > $2,137 ✅
3. ¿Stock disponible? → Todos tienen stock ✅
4. **Resultado**: Aprobado → Cuota ~$137/mes × 18

---

### CASO 2: CLIENTE SIN HISTORIAL BANCARIO INTENTA CRÉDITO

**Contexto**: Luis Rodríguez no es cliente del banco, intenta comprar a crédito.

```http
POST http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas
Content-Type: application/json

{
  "cedulaCliente": "1750567890",
  "formaPago": "CREDITO_DIRECTO",
  "numeroCuotas": 6,
  "items": [
    {
      "idProducto": 4,
      "cantidad": 1
    }
  ]
}
```

**Resultado**:
- ❌ BanQuito responde: "Cliente no es sujeto de crédito: El cliente no está registrado en el banco"
- ❌ Comercializadora rechaza la compra
- 💡 **Solución**: Cliente debe pagar en EFECTIVO o registrarse primero en el banco

---

### CASO 3: CLIENTE CON CRÉDITO ACTIVO INTENTA OTRO CRÉDITO

**Contexto**: María ya tiene un crédito vigente, intenta otro.

```http
### 1. María obtiene un crédito
POST http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas
Content-Type: application/json

{
  "cedulaCliente": "1750234567",
  "formaPago": "CREDITO_DIRECTO",
  "numeroCuotas": 12,
  "items": [
    {
      "idProducto": 7,
      "cantidad": 1
    }
  ]
}

### 2. María intenta OTRO crédito inmediatamente
POST http://localhost:8080/Ex_Comercializadora_RESTJava/api/facturas
Content-Type: application/json

{
  "cedulaCliente": "1750234567",
  "formaPago": "CREDITO_DIRECTO",
  "numeroCuotas": 6,
  "items": [
    {
      "idProducto": 5,
      "cantidad": 1
    }
  ]
}
```

**Resultado del segundo intento**:
- ❌ BanQuito responde: "El cliente ya tiene un crédito activo"
- ❌ Comercializadora rechaza la compra
- 💡 **Solución**: Debe terminar de pagar el primer crédito (o marcarlo como CANCELADO en BD)

---

## 🔧 TROUBLESHOOTING

### ❌ Error: "Cliente no encontrado"

**Causa**: La cédula no existe en la tabla `ClienteCom` de la comercializadora.

**Solución**:
```sql
-- Verificar si existe
SELECT * FROM ClienteCom WHERE cedula = '1750123456';

-- Si no existe, agregarlo
INSERT INTO ClienteCom (cedula, nombre, direccion, telefono) 
VALUES ('1750123456', 'Juan Carlos Pérez López', 'Dirección X', '0991234567');
```

---

### ❌ Error: "Cliente no es sujeto de crédito: Cliente no está registrado en el banco"

**Causa**: La cédula existe en Comercializadora pero NO en BanQuito.

**Solución**: Las cédulas DEBEN ser las mismas en ambas BD:
```sql
-- En banquito_bd
SELECT * FROM CLIENTE WHERE CEDULA = '1750123456';

-- Si no existe, usar los datos de prueba del script
```

---

### ❌ Error: "Cliente no tiene depósitos en el último mes"

**Causa**: No hay movimientos tipo 'DEP' recientes.

**Solución**:
```sql
-- Agregar depósito reciente
INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA)
SELECT NUM_CUENTA, 'DEP', 1000.00, CURDATE()
FROM CUENTA WHERE CEDULA = '1750123456';
```

---

### ❌ Error: "Stock insuficiente"

**Causa**: El stock en BD es menor a la cantidad solicitada.

**Solución**:
```sql
-- Verificar stock actual
SELECT * FROM Producto WHERE idProducto = 6;

-- Aumentar stock
UPDATE Producto SET stock = 50 WHERE idProducto = 6;
```

---

### ❌ Error: "Monto solicitado excede el límite autorizado"

**Causa**: El cliente no tiene suficiente capacidad crediticia.

**Explicación**: BanQuito calcula el monto máximo según historial de movimientos.

**Solución**:
1. **Reducir monto**: Comprar menos productos
2. **Aumentar capacidad**: Agregar más depósitos en el banco
```sql
-- Agregar depósitos grandes
INSERT INTO MOVIMIENTO (NUM_CUENTA, TIPO, VALOR, FECHA)
SELECT NUM_CUENTA, 'DEP', 5000.00, DATE_SUB(CURDATE(), INTERVAL 15 DAY)
FROM CUENTA WHERE CEDULA = '1750123456';
```

---

## 📝 DATOS DE PRUEBA

### Clientes disponibles (ambas BD sincronizadas)

| Cédula | Nombre | Estado Civil | Edad | ¿Sujeto Crédito? |
|--------|--------|--------------|------|------------------|
| 1750123456 | Juan Carlos Pérez López | Casado | 40 | ✅ SÍ |
| 1750234567 | María Elena García Torres | Soltera | 35 | ✅ SÍ |
| 1750345678 | Pedro Antonio Morales Cruz | Casado | 37 | ✅ SÍ |
| 1750456789 | Ana Sofía Ramírez Flores | Soltera | 30 | ✅ SÍ |
| 1750567890 | Luis Fernando Castro Vega | Soltero | 23 | ✅ SÍ |

### Productos disponibles

| ID | Código | Nombre | Precio | Stock |
|----|--------|--------|--------|-------|
| 1 | REF001 | Refrigeradora LG 14 pies | $899 | 15 |
| 2 | LAV001 | Lavadora Samsung 20 lb | $749 | 20 |
| 3 | COC001 | Cocina Indurama 6 quemadores | $489 | 12 |
| 4 | MIC001 | Microondas Panasonic 1.2 cu ft | $159 | 30 |
| 5 | LIC001 | Licuadora Oster 10 velocidades | $89 | 40 |
| 6 | TV001 | Smart TV LG 55 pulgadas | $1,299 | 8 |
| 7 | TV002 | Smart TV Samsung 43 pulgadas | $849 | 10 |
| 8 | ASP001 | Aspiradora Electrolux 1800W | $249 | 18 |
| 9 | PLAN001 | Plancha a vapor Black+Decker | $45 | 50 |
| 10 | VEN001 | Ventilador de Torre Samurai | $79 | 25 |

---

## 🎓 CONCEPTOS CLAVE

### Sistema de Amortización Francés

- **Cuota fija** mensual durante todo el plazo
- **Interés decreciente** (se paga más al inicio)
- **Capital creciente** (se amortiza más al final)
- **Tasa**: 16% anual fija

### Ejemplo real:
- Crédito: $850
- Plazo: 6 meses
- Cuota: $148.35

| Cuota | Interés | Capital | Saldo |
|-------|---------|---------|-------|
| 1 | $11.33 | $137.02 | $712.98 |
| 2 | $9.51 | $138.84 | $574.14 |
| 3 | $7.66 | $140.69 | $433.45 |
| 4 | $5.78 | $142.57 | $290.88 |
| 5 | $3.88 | $144.47 | $146.41 |
| 6 | $1.95 | $146.41 | $0.00 |

---

## 🚀 EJECUCIÓN RÁPIDA

### Iniciar sistema completo

```bash
# 1. Iniciar MySQL
docker start mysql-db

# 2. Verificar conexión
docker exec -it mysql-db mysql -uroot -p1234 -e "SHOW DATABASES;"

# 3. Iniciar Payara con ambos proyectos desplegados

# 4. Probar conectividad
curl http://localhost:8080/Ex_Banquito_RESTJava/api/creditos/sujeto-credito/1750123456
curl http://localhost:8080/Ex_Comercializadora_RESTJava/api/health
```

---

**¡Listo para probar! 🎉**
