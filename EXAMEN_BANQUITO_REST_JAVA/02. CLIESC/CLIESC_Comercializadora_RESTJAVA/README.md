# 🛒 CLIENTE DE ESCRITORIO - COMERCIALIZADORA MONSTER

Cliente de escritorio Java Swing que consume las APIs REST de Comercializadora y BanQuito.

## 🎨 Características

- **Diseño Monsters Inc**: Interfaz con gradientes #7c3aed → #667eea (púrpura)
- **Patrón MVC**: Separación clara de Modelos, Vistas y Controladores
- **Funcionalidades Completas**:
  - ✅ Login con validación de credenciales
  - ✅ Catálogo de productos con stock en tiempo real
  - ✅ Carrito de compras (Factura)
  - ✅ Validación de stock (mismo fix que cliente web)
  - ✅ Checkout con dos métodos de pago:
    - 💵 Efectivo (33% de descuento)
    - 💳 Crédito Directo (3-24 meses, integración con BanQuito)
  - ✅ Historial de ventas
  - ✅ Detalle de facturas
  - ✅ Tabla de amortización para créditos
  - ✅ Estado de créditos desde BanQuito (APROBADO/CANCELADO)

## 📦 Estructura del Proyecto (MVC)

```
src/main/java/ec/edu/pinza/cliesc/
├── MainApp.java                    # Clase principal ejecutable
├── models/                          # Modelos (DTOs)
│   ├── ClienteDTO.java
│   ├── ProductoDTO.java
│   ├── ItemCarrito.java
│   ├── VentaDTO.java
│   ├── DetalleVentaDTO.java
│   ├── CreditoDTO.java
│   └── AmortizacionDTO.java
├── views/                           # Vistas (Swing Frames)
│   ├── LoginFrame.java
│   ├── ProductosFrame.java
│   ├── CarritoFrame.java
│   ├── CheckoutFrame.java
│   └── VentasFrame.java
├── controllers/                     # Controladores (Lógica)
│   ├── LoginController.java
│   ├── ProductosController.java
│   ├── CarritoController.java
│   ├── CheckoutController.java
│   └── VentasController.java
├── services/                        # Clientes REST
│   ├── ComercializadoraRestClient.java
│   └── BanquitoRestClient.java
├── managers/                        # Gestores de sesión
│   └── SessionManager.java
└── utils/                           # Utilidades
    ├── UIConstants.java            # Constantes de diseño
    └── FormatUtils.java            # Formateo de datos
```

## 🚀 Compilación

```bash
cd "02. CLIESC\CLIESC_Comercializadora_RESTJAVA"
mvn clean package
```

Esto generará:
- `target/CLIESC_Comercializadora_RESTJAVA-1.0-SNAPSHOT.jar` - JAR ejecutable
- `target/lib/` - Dependencias (Gson, FlatLaf)

## ▶️ Ejecución

### Opción 1: Con Maven
```bash
mvn exec:java
```

### Opción 2: Con Java (requiere dependencias en lib/)
```bash
java -jar target/CLIESC_Comercializadora_RESTJAVA-1.0-SNAPSHOT.jar
```

### Opción 3: Doble clic en el JAR
Simplemente hacer doble clic en el archivo JAR generado.

## 🔗 Requisitos Previos

1. **Servidores REST en ejecución**:
   - `Ex_Comercializadora_RESTJava` en `http://localhost:8080/Ex_Comercializadora_RESTJava/api`
   - `Ex_Banquito_RESTJava` en `http://localhost:8080/Ex_Banquito_RESTJava/api`

2. **Bases de datos**:
   - MySQL `comercializadora_db` con productos y clientes
   - MySQL `banquito_db` para créditos

3. **Java 17+** instalado

## 👤 Credenciales de Prueba

```
Correo: juan.perez@example.com
Contraseña: password123
```

## 🎨 Diseño de la Interfaz

- **Colores Principales**: Gradiente #7c3aed (púrpura inicio) → #667eea (púrpura fin)
- **Look & Feel**: FlatLaf Dark (moderno y elegante)
- **Tipografía**: Segoe UI
- **Componentes**:
  - Navbar con gradiente
  - Cards para productos
  - Tablas con estilos personalizados
  - Modals para detalles
  - Botones con hover effects
  - Badges para notificaciones

## 🛠️ Tecnologías Utilizadas

- **Java 17**: Lenguaje de programación
- **Swing**: Framework para GUI
- **FlatLaf**: Modern Look & Feel
- **Gson**: Serialización/Deserialización JSON
- **HttpClient**: Consumo de APIs REST (Java 11+)
- **Maven**: Gestión de dependencias y build

## 📋 Funcionalidades Implementadas

### 1. Login
- Validación de credenciales contra API REST
- Manejo de sesión con SessionManager
- Navegación automática a catálogo de productos

### 2. Catálogo de Productos
- Grid de productos con información completa
- Stock en tiempo real
- Validación de stock al agregar (bug fix del cliente web)
- Badge de notificación en carrito
- Navegación entre vistas

### 3. Carrito/Factura
- Tabla de items agregados
- Cálculo automático de subtotales
- Opciones para eliminar items
- Botón para limpiar factura completa
- Total con gradiente destacado

### 4. Checkout
- Tabla resumen de productos
- Selector de método de pago:
  - **Efectivo**: Muestra descuento del 33% en tiempo real
  - **Crédito**: Selector de cuotas (3-24 meses)
- Validación de stock antes de confirmar
- Integración con BanQuito para solicitud de crédito
- Confirmación y creación de venta

### 5. Mis Ventas
- Tabla de historial de ventas del cliente
- Doble clic para ver detalle de factura
- Modal de detalle con:
  - Información de la venta
  - Tabla de productos comprados
  - Resumen financiero (subtotal, descuento/intereses, total)
  - Botón para ver tabla de amortización (solo créditos)
- Estado del crédito desde BanQuito (APROBADO/CANCELADO)

### 6. Tabla de Amortización
- Modal con tabla completa de cuotas
- Columnas: Cuota, Fecha Pago, Monto Cuota, Capital, Interés, Saldo
- Total de intereses destacado
- Datos en tiempo real desde BanQuito

## 🐛 Bug Fixes Implementados

1. **Validación de Stock en Carrito**: Igual que en cliente web, se valida la cantidad total (existente + nueva) antes de agregar al carrito.

2. **Formateo de Moneda**: Uso consistente de FormatUtils para mostrar valores monetarios.

3. **Manejo de Errores**: Try-catch en todos los controladores con mensajes claros.

## 📝 Notas Técnicas

- **SessionManager**: Singleton que maneja el estado de la aplicación (cliente autenticado y carrito)
- **Deserialización de Fechas**: Gson configurado con adaptadores para LocalDate y LocalDateTime
- **Gradient Panels**: Componentes personalizados que pintan gradientes con Graphics2D
- **Thread Safety**: SwingUtilities.invokeLater() para operaciones de UI

## 🎯 Modelo MVC

### Model (models/)
DTOs que representan los datos del negocio, mapeados directamente desde las APIs REST.

### View (views/)
Frames de Swing que definen la interfaz gráfica. No contienen lógica de negocio.

### Controller (controllers/)
Clases que manejan la lógica de la aplicación, conectan las vistas con los servicios REST.

## 🔄 Flujo de la Aplicación

1. **Inicio** → LoginFrame
2. **Login exitoso** → ProductosFrame
3. **Agregar productos** → Badge actualizado
4. **Ver Factura** → CarritoFrame (modal)
5. **Proceder al Pago** → CheckoutFrame
6. **Seleccionar método de pago** → EFECTIVO o CRÉDITO
7. **Confirmar compra** → Venta creada, carrito limpio, volver a ProductosFrame
8. **Ver Mis Ventas** → VentasFrame
9. **Ver Detalle** → Modal con información completa
10. **Ver Amortización** → Modal con tabla de cuotas (solo créditos)

## ✅ Testing

Para probar la aplicación:

1. Asegúrate de que los servidores REST estén corriendo
2. Ejecuta la aplicación
3. Inicia sesión con las credenciales de prueba
4. Navega por el catálogo y agrega productos
5. Revisa el carrito
6. Prueba ambos métodos de pago
7. Revisa el historial de ventas
8. Verifica la tabla de amortización para compras a crédito

---

**Desarrollado con ❤️ siguiendo el patrón MVC y las mejores prácticas de Java Swing**
