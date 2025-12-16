# 🔍 Endpoints GET para Validar el Sistema

## ✅ Backend Banquito (Puerto 8080)

### 1. Validar si un cliente es sujeto de crédito
```bash
curl http://localhost:8080/Ex_Banquito_RESTJava-1.0-SNAPSHOT/api/creditos/sujeto-credito/1720665049
```

### 2. Obtener monto máximo de crédito
```bash
curl http://localhost:8080/Ex_Banquito_RESTJava-1.0-SNAPSHOT/api/creditos/monto-maximo/1720665049
```

---

## ✅ Backend Comercializadora (Puerto 8081)

### 1. Listar todos los productos
```bash
curl http://localhost:8081/Ex_Comercializadora_RESTJava-1.0-SNAPSHOT/api/productos
```

### 2. Obtener un producto específico
```bash
curl http://localhost:8081/Ex_Comercializadora_RESTJava-1.0-SNAPSHOT/api/productos/1
```

### 3. Test de cliente (consume API Banquito internamente)
```bash
curl http://localhost:8081/Ex_Comercializadora_RESTJava-1.0-SNAPSHOT/api/facturas/test-cliente/1720665049
```

---

## 📊 Respuestas Esperadas

### Banquito - Sujeto de Crédito:
```json
{
  "cedula": "1720665049",
  "esSujetoCredito": false,
  "motivo": "El cliente no está registrado en el banco, no es cliente"
}
```

### Comercializadora - Productos:
```json
[
  {
    "idProducto": 1,
    "codigo": "REF001",
    "nombre": "Refrigeradora LG 14 pies",
    "precio": 899.00,
    "stock": 15
  },
  ...
]
```

### Comercializadora - Test Cliente (comunicación entre backends):
```json
{
  "mensaje": "Probando conexión con Banquito",
  "cedula": "1720665049",
  "respuestaBanquito": {...}
}
```

---

## 🚀 Todo está en Docker

- **Payara Server**: Imagen `payara/server-full:6.2023.12-jdk17`
- **MySQL Banquito**: Puerto 3306 (contenedor: mysql_banquito)
- **MySQL Comercializadora**: Puerto 3307 (contenedor: mysql_comercializadora)
- **Backend Banquito**: Puerto 8080 (contenedor: backend_banquito)
- **Backend Comercializadora**: Puerto 8081 (contenedor: backend_comercializadora)

Todos los servicios corren 100% en Docker, nada se consume del host excepto los puertos expuestos.
