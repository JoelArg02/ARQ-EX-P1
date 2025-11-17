# CLI_CON - Cliente de Consola para Sistema Bancario

## Descripción
Aplicación de consola en Java que consume los servicios SOAP del Sistema Bancario (API_BANCO).

## Características
- ✅ Inicio de sesión con validación de credenciales
- ✅ Gestión de clientes (listar)
- ✅ Gestión de cuentas (listar)
- ✅ Gestión de créditos (listar)
- ✅ Operaciones bancarias (depósitos y retiros)
- ✅ Interfaz de usuario intuitiva en consola
- ✅ Consumo de servicios SOAP con HttpClient5

## Requisitos Previos
- Java 17 o superior
- Maven 3.6+
- Servidor API_BANCO corriendo en http://localhost:5001

## Configuración

### 1. Configurar la URL del servidor
Editar el archivo `src/main/java/ec/edu/arguello/config/ServiceConfig.java`:

```java
public static final String BASE_URL = "http://localhost:5001";
```

Para producción, cambiar a:
```java
public static final String BASE_URL = "https://soap-espe.azurewebsites.net";
```

### 2. Iniciar el servidor API_BANCO
```bash
cd ../01\ SERVIDOR/API_BANCO
dotnet run
```

## Compilación

```bash
mvn clean compile
```

## Ejecución

```bash
mvn exec:java
```

O usando el comando directo:
```bash
java -cp target/classes ec.edu.arguello.cli_con.CLI_CON
```

## Uso

### 1. Inicio de Sesión
Al ejecutar la aplicación, se solicitarán las credenciales:
```
Usuario: MONSTER
Contraseña: MONSTER1
```

### 2. Menú Principal
```
╔════════════════════════════════════════╗
║          MENÚ PRINCIPAL                ║
╠════════════════════════════════════════╣
║ 1. Gestionar Clientes                 ║
║ 2. Gestionar Cuentas                  ║
║ 3. Gestionar Créditos                 ║
║ 4. Operaciones Bancarias              ║
║ 0. Salir                              ║
╚════════════════════════════════════════╝
```

### 3. Operaciones Disponibles

#### Gestionar Clientes
- Listar todos los clientes del banco
- Ver información detallada de cada cliente

#### Gestionar Cuentas
- Listar todas las cuentas bancarias
- Ver saldo, tipo de cuenta y número

#### Gestionar Créditos
- Listar todos los créditos bancarios
- Ver monto, tasa de interés, plazo y estado

#### Operaciones Bancarias
1. Seleccionar una cuenta de la lista
2. Elegir tipo de operación (Depósito o Retiro)
3. Ingresar el monto
4. Confirmar la operación
5. El saldo se actualiza automáticamente

## Estructura del Proyecto

```
src/main/java/ec/edu/arguello/
├── cli_con/
│   └── CLI_CON.java              # Clase principal con menú interactivo
├── config/
│   └── ServiceConfig.java        # Configuración de URLs de servicios
├── model/
│   ├── User.java                 # Modelo de usuario
│   ├── ClienteBanco.java         # Modelo de cliente
│   ├── Cuenta.java               # Modelo de cuenta
│   ├── CreditoBanco.java         # Modelo de crédito
│   └── Movimiento.java           # Modelo de movimiento
├── service/
│   ├── UserService.java          # Servicio de autenticación
│   ├── ClienteService.java       # Servicio de clientes
│   ├── CuentaService.java        # Servicio de cuentas
│   └── CreditoService.java       # Servicio de créditos
└── util/
    └── SoapClient.java           # Cliente SOAP base
```

## Servicios Consumidos

### UserService
- **ValidateCredentials**: Valida usuario y contraseña

### ClienteBancoService
- **GetAllClientesBanco**: Obtiene todos los clientes

### CuentaService
- **GetAllCuentas**: Obtiene todas las cuentas

### CreditoBancoService
- **GetAllCreditosBanco**: Obtiene todos los créditos

### MovimientoService
- **CreateMovimiento**: Crea depósitos o retiros
  - Tipo 1: Depósito
  - Tipo 2: Retiro

## Ejemplo de Uso

```bash
# 1. Compilar
mvn clean compile

# 2. Ejecutar
mvn exec:java

# 3. Iniciar sesión
Usuario: MONSTER
Contraseña: MONSTER1

# 4. Seleccionar operación
Seleccione una opción: 4

# 5. Realizar depósito
Ingrese el ID de la cuenta: 4
1. Depósito
2. Retiro
Seleccione el tipo de operación: 1
Ingrese el monto: $100
¿Confirmar operación? (S/N): S

✓ Operación realizada exitosamente
```

## Credenciales de Prueba

| Usuario | Contraseña | Rol |
|---------|-----------|-----|
| MONSTER | MONSTER1  | Admin |

## Notas Importantes

1. **Servidor requerido**: El servidor API_BANCO debe estar corriendo antes de ejecutar esta aplicación
2. **Puerto**: Por defecto usa el puerto 5001 (localhost)
3. **Timeout**: Las operaciones tienen un timeout por defecto del HttpClient
4. **Encoding**: Todas las peticiones usan UTF-8
5. **Saldo**: Los retiros validan que haya saldo suficiente

## Solución de Problemas

### Error de conexión
```
Error al obtener clientes: Connection refused
```
**Solución**: Verificar que el servidor API_BANCO esté corriendo en el puerto 5001

### Credenciales incorrectas
```
✗ Credenciales incorrectas
```
**Solución**: Verificar usuario y contraseña (MONSTER/MONSTER1)

### Operación fallida
```
✗ Error al realizar la operación
```
**Solución**: 
- Verificar que la cuenta tenga saldo suficiente para retiros
- Verificar que el monto sea mayor a cero
- Revisar los logs del servidor

## Autor
Joel Arguello - ESPE 2025
