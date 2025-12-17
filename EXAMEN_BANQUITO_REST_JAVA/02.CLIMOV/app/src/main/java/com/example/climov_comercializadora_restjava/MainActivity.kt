package com.example.climov_comercializadora_restjava

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.climov_comercializadora_restjava.controllers.AppController
import com.example.climov_comercializadora_restjava.controllers.UiState
import com.example.climov_comercializadora_restjava.models.FacturaResponseDTO
import com.example.climov_comercializadora_restjava.models.ProductoDTO
import com.example.climov_comercializadora_restjava.components.ProductImage
import com.example.climov_comercializadora_restjava.screens.AdminProductosScreen
import com.example.climov_comercializadora_restjava.utils.PdfGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : ComponentActivity() {
    private val controller by viewModels<AppController> { AppController.factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CliconTheme {
                App(controller)
            }
        }
    }
}

// ========= UI =========

enum class Screen(val route: String, val label: String) {
    Productos("productos", "Productos"),
    Carrito("carrito", "Carrito"),
    TodasVentas("todas_ventas", "Todas las Ventas"),
    MisCompras("mis_compras", "Mis Compras"),
    Admin("admin", "Admin Productos")
}

@Composable
fun App(controller: AppController = viewModel(factory = AppController.factory)) {
    val state by controller.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    state.mensaje?.let { msg ->
        LaunchedEffect(msg) {
            snackbarHostState.showSnackbar(msg)
            controller.limpiarMensaje()
        }
    }

    Surface(color = androidx.compose.material3.MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        if (!state.isLoggedIn) {
            LoginScreen(onLogin = { u, p -> controller.login(u, p) }, snackbarHostState = snackbarHostState)
        } else {
            HomeScreen(controller = controller, state = state, snackbarHostState = snackbarHostState)
        }
    }
}

@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, snackbarHostState: SnackbarHostState) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.sullyvan),
                contentDescription = "Logo",
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "Comercializadora Monster",
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
            )
            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contrasena") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))
            Button(onClick = { onLogin(usuario.trim(), password.trim()) }, modifier = Modifier.fillMaxWidth()) {
                Text("Ingresar")
            }
            
            // Instrucciones de login
            Spacer(Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Credenciales de acceso:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Admin: MONSTER / MONSTER9", fontSize = 13.sp, color = Color.Gray)
                    Text("Cliente: cédula / abcd1234", fontSize = 13.sp, color = Color.Gray)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(controller: AppController, state: UiState, snackbarHostState: SnackbarHostState) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                
                // Info del usuario
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        if (state.isAdmin) "Admin" else "Cliente",
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge
                    )
                    Text(
                        state.usuario,
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    if (!state.isAdmin && state.cedula.isNotEmpty()) {
                        Text(
                            "CI: ${state.cedula}",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
                HorizontalDivider()
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(Screen.Productos.label) },
                    selected = currentRoute == Screen.Productos.route,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Productos.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    label = { Text(Screen.Carrito.label) },
                    selected = currentRoute == Screen.Carrito.route,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Carrito.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                
                // Ventas/Compras - Admin ve "Todas las Ventas", Cliente ve "Mis Compras"
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text(if (state.isAdmin) "Historial de Ventas" else "Mis Compras") },
                    selected = currentRoute == Screen.TodasVentas.route,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.TodasVentas.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                
                // Solo admin ve la opción de Admin Productos
                if (state.isAdmin) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text(Screen.Admin.label) },
                        selected = currentRoute == Screen.Admin.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Admin.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Comercializadora Monster") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Productos.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(Screen.Productos.route) {
                    ProductosScreen(state, onAdd = { p, qty -> controller.agregarAlCarrito(p, qty) }, onRefresh = { controller.cargarProductos() })
                }
                composable(Screen.Carrito.route) {
                    CarritoScreen(state, total = controller.totalCarrito, onEliminar = controller::eliminarDelCarrito, onVaciar = controller::vaciarCarrito, onCheckout = controller::checkout)
                }
                composable(Screen.TodasVentas.route) {
                    TodasVentasScreen(state, onCargar = { controller.cargarVentas() }, onDetalle = controller::cargarFacturaDetalle)
                }
                composable(Screen.MisCompras.route) {
                    MisComprasScreen(state, onBuscar = controller::cargarMisCompras, onDetalle = controller::cargarFacturaDetalle)
                }
                composable(Screen.Admin.route) {
                    AdminProductosScreen(state = state, controller = controller)
                }
            }
        }
    }
}

@Composable
fun ProductosScreen(state: UiState, onAdd: (ProductoDTO, Int) -> Unit, onRefresh: () -> Unit) {
    LaunchedEffect(Unit) { onRefresh() }
    LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        items(state.productos) { producto ->
            ProductoCard(producto = producto, onAdd = { qty -> onAdd(producto, qty) })
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun ProductoCard(producto: ProductoDTO, onAdd: (Int) -> Unit) {
    var cantidadText by remember { mutableStateOf("1") }
    Card(colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Imagen del producto
            if (!producto.imagen.isNullOrBlank()) {
                ProductImage(
                    base64Image = producto.imagen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
            
            Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Codigo: ${producto.codigo}", color = Color.Gray, fontSize = 12.sp)
            Text("Precio: $${producto.precio} | Stock: ${producto.stock}")
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = cantidadText,
                    onValueChange = { cantidadText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Cantidad") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { onAdd(cantidadText.toIntOrNull() ?: 0) }) { Text("Agregar") }
            }
        }
    }
}

@Composable
fun CarritoScreen(
    state: UiState,
    total: BigDecimal,
    onEliminar: (Int) -> Unit,
    onVaciar: () -> Unit,
    onCheckout: (String, String, Int?) -> Unit
) {
    var mostrarCheckout by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("Carrito", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        if (state.carrito.isEmpty()) {
            Text("No tienes productos en el carrito.")
        } else {
            LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                items(state.carrito) { item ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(Modifier.padding(12.dp)) {
                            Text(item.nombre, fontWeight = FontWeight.Bold)
                            Text("Precio: $${item.precio} | Cant: ${item.cantidad} | Subtotal: $${item.subtotal}")
                            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                                androidx.compose.material3.IconButton(onClick = { onEliminar(item.idProducto) }) {
                                    androidx.compose.material3.Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
            Divider()
            Text("Total: $${total.setScale(2, RoundingMode.HALF_UP)}", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { mostrarCheckout = true }) { Text("Checkout") }
                TextButton(onClick = onVaciar) { Text("Vaciar") }
            }
        }
    }

    if (mostrarCheckout) {
        CheckoutDialog(
            isAdmin = state.isAdmin,
            cedulaUsuario = state.cedula,
            onDismiss = { mostrarCheckout = false },
            onConfirm = { cedula, metodo, cuotas ->
                onCheckout(cedula, metodo, cuotas)
                mostrarCheckout = false
            }
        )
    }
}

@Composable
fun CheckoutDialog(
    isAdmin: Boolean,
    cedulaUsuario: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int?) -> Unit
) {
    // Si no es admin, la cédula viene fija del usuario logueado
    var cedula by remember { mutableStateOf(cedulaUsuario) }
    var metodo by remember { mutableStateOf("EFECTIVO") }
    var cuotasText by remember { mutableStateOf("3") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = { onConfirm(cedula.trim(), metodo, cuotasText.toIntOrNull()) }) { Text("Pagar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Checkout") },
        text = {
            Column {
                OutlinedTextField(
                    value = cedula,
                    onValueChange = { if (isAdmin) cedula = it },
                    label = { Text(if (isAdmin) "Cédula del cliente" else "Tu cédula") },
                    singleLine = true,
                    enabled = isAdmin, // Solo admin puede editar la cédula
                    readOnly = !isAdmin
                )
                if (!isAdmin) {
                    Text(
                        "Solo puedes comprar para tu propia cédula",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text("Forma de pago")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = metodo == "EFECTIVO", onClick = { metodo = "EFECTIVO" })
                    Text("Efectivo")
                    Spacer(Modifier.width(12.dp))
                    RadioButton(selected = metodo == "CREDITO_DIRECTO", onClick = { metodo = "CREDITO_DIRECTO" })
                    Text("Crédito directo")
                }
                if (metodo == "CREDITO_DIRECTO") {
                    OutlinedTextField(
                        value = cuotasText,
                        onValueChange = { cuotasText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Número de cuotas (3-24)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
        }
    )
}

@Composable
fun MisComprasScreen(state: UiState, onBuscar: (String) -> Unit, onDetalle: (Int) -> Unit) {
    var cedula by remember { mutableStateOf("") }
    var mostrarDetalle by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("Mis compras", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = cedula, onValueChange = { cedula = it }, label = { Text("Cedula") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onBuscar(cedula.trim()) }) { Text("Buscar") }
        Spacer(Modifier.height(12.dp))
        
        if (state.misCompras.isEmpty()) {
            Text("No hay compras para mostrar.", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn {
                items(state.misCompras) { factura ->
                    FacturaResumenCard(factura = factura, onClick = { 
                        factura.idFactura?.let { id ->
                            onDetalle(id)
                            mostrarDetalle = true
                        }
                    })
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
    
    // Modal de detalle
    if (mostrarDetalle && state.facturaActual != null) {
        FacturaDetalleDialog(
            factura = state.facturaActual,
            onDismiss = { mostrarDetalle = false }
        )
    }
}

@Composable
fun TodasVentasScreen(state: UiState, onCargar: () -> Unit, onDetalle: (Int) -> Unit) {
    var mostrarDetalle by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) { onCargar() }
    
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        // Título según el rol
        Text(
            if (state.isAdmin) "Historial de Ventas (Todas)" else "Mis Compras",
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))
        
        if (state.ventas.isEmpty()) {
            Text(
                if (state.isAdmin) "No hay ventas registradas." else "No tienes compras registradas.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn {
                items(state.ventas) { factura ->
                    FacturaResumenCard(factura = factura, onClick = { 
                        factura.idFactura?.let { id ->
                            onDetalle(id)
                            mostrarDetalle = true
                        }
                    })
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
    
    // Modal de detalle
    if (mostrarDetalle && state.facturaActual != null) {
        FacturaDetalleDialog(
            factura = state.facturaActual,
            onDismiss = { mostrarDetalle = false }
        )
    }
}

@Composable
fun FacturaResumenCard(factura: FacturaResponseDTO, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Factura #${factura.idFactura ?: "-"}", 
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF667eea)
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (factura.formaPago == "EFECTIVO") Color(0xFF10b981) else Color(0xFF667eea)
                    )
                ) {
                    Text(
                        factura.formaPago ?: "-",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Cliente",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        factura.nombreCliente ?: "-",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color(0xFF1e293b)
                    )
                    Text(
                        "CI: ${factura.cedulaCliente ?: "-"}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748b)
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Total",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        "$${factura.total ?: BigDecimal.ZERO}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF667eea)
                    )
                }
            }
            
            if (factura.fecha != null) {
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFFe2e8f0))
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "📅 ${factura.fecha}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748b)
                    )
                }
            }
        }
    }
}

@Composable
fun FacturaDetalleCard(factura: FacturaResponseDTO) {
    var mostrarAmortizacion by remember { mutableStateOf(false) }
    
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Factura #${factura.idFactura ?: "-"}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Cliente: ${factura.nombreCliente ?: "-"} (${factura.cedulaCliente ?: ""})")
            Text("Fecha: ${factura.fecha ?: "-"}")
            Text("Forma de pago: ${factura.formaPago ?: "-"}")
            factura.idCreditoBanco?.let { Text("Id credito banco: $it") }

            val subtotal = calcularSubtotal(factura)
            val descuento = calcularDescuentoEfectivo(factura, subtotal)
            Text("Subtotal productos: $${subtotal}")
            if (descuento > BigDecimal.ZERO) {
                Text("Descuento 33% (efectivo): -$descuento", color = Color(0xFF16a34a))
            }
            Text("Total (API): $${factura.total ?: BigDecimal.ZERO}", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(8.dp))
            Text("Productos:", fontWeight = FontWeight.SemiBold)
            factura.detalles?.forEach {
                Text("- ${it.nombreProducto} x${it.cantidad} @ $${it.precioUnitario} = $${it.subtotal}", fontSize = 14.sp)
            }

            factura.infoCredito?.let { credito ->
                Spacer(Modifier.height(8.dp))
                Text("Credito directo", fontWeight = FontWeight.SemiBold)
                Text("Credito #${credito.idCredito}", fontSize = 14.sp)
                Text("Monto: $${credito.montoCredito} | Cuotas: ${credito.numeroCuotas}", fontSize = 14.sp)
                Text("Valor cuota: $${credito.valorCuota} | Tasa: ${credito.tasaInteres}%", fontSize = 14.sp)
                
                val tabla = credito.tablaAmortizacion
                if (tabla != null && tabla.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { mostrarAmortizacion = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver Tabla de Amortizacion")
                    }
                }
            }
        }
    }
    
    // Diálogo de tabla de amortización
    if (mostrarAmortizacion && factura.infoCredito?.tablaAmortizacion != null) {
        AlertDialog(
            onDismissRequest = { mostrarAmortizacion = false },
            confirmButton = {
                TextButton(onClick = { mostrarAmortizacion = false }) {
                    Text("Cerrar")
                }
            },
            title = { Text("Tabla de Amortizacion") },
            text = {
                LazyColumn {
                    item {
                        // Encabezado
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF667eea)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Cuota", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(0.8f))
                                Text("Valor", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                Text("Interes", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                Text("Capital", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                Text("Saldo", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                    
                    items(factura.infoCredito.tablaAmortizacion) { cuota ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${cuota.numeroCuota}", fontSize = 11.sp, modifier = Modifier.weight(0.8f))
                                Text("$${cuota.valorCuota}", fontSize = 11.sp, modifier = Modifier.weight(1f))
                                Text("$${cuota.interesPagado}", fontSize = 11.sp, modifier = Modifier.weight(1f))
                                Text("$${cuota.capitalPagado}", fontSize = 11.sp, modifier = Modifier.weight(1f))
                                Text("$${cuota.saldoRestante}", fontSize = 11.sp, modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        )
    }
}

@Composable
fun FacturaDetalleDialog(factura: FacturaResponseDTO, onDismiss: () -> Unit) {
    var mostrarAmortizacion by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mensajePdf by remember { mutableStateOf<String?>(null) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        title = { 
            Text(
                "Factura #${factura.idFactura ?: "-"}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF667eea)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Información del cliente
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("CLIENTE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748b))
                        Spacer(Modifier.height(4.dp))
                        Text(
                            factura.nombreCliente ?: "-",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1e293b)
                        )
                        Text("CI: ${factura.cedulaCliente ?: "-"}", fontSize = 13.sp, color = Color(0xFF64748b))
                        Text("Fecha: ${factura.fecha ?: "-"}", fontSize = 13.sp, color = Color(0xFF64748b))
                    }
                }
                
                Spacer(Modifier.height(12.dp))
                
                // Forma de pago
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Forma de pago:", fontSize = 13.sp, color = Color(0xFF64748b))
                    Text(
                        factura.formaPago ?: "-",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (factura.formaPago == "EFECTIVO") Color(0xFF10b981) else Color(0xFF667eea)
                    )
                }
                
                factura.idCreditoBanco?.let { 
                    Text("ID Crédito Banco: $it", fontSize = 12.sp, color = Color(0xFF64748b), modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFe2e8f0))
                Spacer(Modifier.height(12.dp))

                // Productos
                Text("PRODUCTOS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748b))
                Spacer(Modifier.height(8.dp))
                
                factura.detalles?.forEach { detalle ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                detalle.nombreProducto ?: "-",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1e293b)
                            )
                            Text(
                                "${detalle.cantidad} x $${detalle.precioUnitario}",
                                fontSize = 12.sp,
                                color = Color(0xFF64748b)
                            )
                        }
                        Text(
                            "$${detalle.subtotal}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1e293b)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFe2e8f0))
                Spacer(Modifier.height(12.dp))

                // Resumen financiero
                val subtotal = calcularSubtotal(factura)
                val descuento = calcularDescuentoEfectivo(factura, subtotal)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal:", fontSize = 14.sp, color = Color(0xFF64748b))
                    Text("$${subtotal}", fontSize = 14.sp, color = Color(0xFF1e293b))
                }
                
                if (descuento > BigDecimal.ZERO) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Descuento (33%):", fontSize = 14.sp, color = Color(0xFF10b981))
                        Text("-$descuento", fontSize = 14.sp, color = Color(0xFF10b981), fontWeight = FontWeight.Medium)
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TOTAL", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1e293b))
                        Text(
                            "$${factura.total ?: BigDecimal.ZERO}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667eea)
                        )
                    }
                }

                // Información de crédito
                factura.infoCredito?.let { credito ->
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFe2e8f0))
                    Spacer(Modifier.height(12.dp))
                    
                    Text("CRÉDITO DIRECTO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748b))
                    Spacer(Modifier.height(8.dp))
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Crédito #", fontSize = 12.sp, color = Color(0xFF64748b))
                                Text("${credito.idCredito}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1e293b))
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Monto", fontSize = 12.sp, color = Color(0xFF64748b))
                                Text("$${credito.montoCredito}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1e293b))
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Cuotas", fontSize = 12.sp, color = Color(0xFF64748b))
                                Text("${credito.numeroCuotas}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1e293b))
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Valor cuota", fontSize = 12.sp, color = Color(0xFF64748b))
                                Text("$${credito.valorCuota}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF667eea))
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Tasa de interés", fontSize = 12.sp, color = Color(0xFF64748b))
                                Text("${credito.tasaInteres}%", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1e293b))
                            }
                        }
                    }
                    
                    val tabla = credito.tablaAmortizacion
                    if (tabla != null && tabla.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { mostrarAmortizacion = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF667eea)
                            )
                        ) {
                            Text("📊 Ver Tabla de Amortización", fontSize = 13.sp)
                        }
                        
                        Spacer(Modifier.height(6.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    try {
                                        val result = withContext(Dispatchers.IO) {
                                            PdfGenerator.generarPdfAmortizacion(context, factura)
                                        }
                                        result.onSuccess { file ->
                                            mensajePdf = "PDF guardado: ${file.name}"
                                            PdfGenerator.abrirPdf(context, file)
                                        }.onFailure { error ->
                                            mensajePdf = "Error: ${error.message}"
                                        }
                                    } catch (e: Exception) {
                                        mensajePdf = "Error al generar PDF: ${e.message}"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF10b981)
                            )
                        ) {
                            Text("📄 Descargar PDF", fontSize = 13.sp)
                        }
                        
                        mensajePdf?.let { msg ->
                            Spacer(Modifier.height(8.dp))
                            Text(
                                msg,
                                fontSize = 11.sp,
                                color = if (msg.startsWith("Error")) Color(0xFFef4444) else Color(0xFF10b981),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    )
    
    // Diálogo de tabla de amortización
    if (mostrarAmortizacion && factura.infoCredito?.tablaAmortizacion != null) {
        AlertDialog(
            onDismissRequest = { mostrarAmortizacion = false },
            confirmButton = {
                TextButton(onClick = { mostrarAmortizacion = false }) {
                    Text("Cerrar")
                }
            },
            title = { Text("Tabla de Amortización", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    item {
                        // Encabezado
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF667eea)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Cta", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp, modifier = Modifier.weight(0.6f))
                                Text("Valor", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp, modifier = Modifier.weight(1f))
                                Text("Int", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp, modifier = Modifier.weight(0.9f))
                                Text("Cap", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp, modifier = Modifier.weight(0.9f))
                                Text("Saldo", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                    
                    items(factura.infoCredito.tablaAmortizacion) { cuota ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${cuota.numeroCuota}", fontSize = 10.sp, modifier = Modifier.weight(0.6f))
                                Text("$${cuota.valorCuota}", fontSize = 10.sp, modifier = Modifier.weight(1f))
                                Text("$${cuota.interesPagado}", fontSize = 10.sp, modifier = Modifier.weight(0.9f))
                                Text("$${cuota.capitalPagado}", fontSize = 10.sp, modifier = Modifier.weight(0.9f))
                                Text("$${cuota.saldoRestante}", fontSize = 10.sp, modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        )
    }
}

private fun calcularSubtotal(factura: FacturaResponseDTO): BigDecimal {
    return factura.detalles?.fold(BigDecimal.ZERO) { acc, det -> acc + det.subtotal } ?: BigDecimal.ZERO
}

private fun calcularDescuentoEfectivo(factura: FacturaResponseDTO, subtotal: BigDecimal): BigDecimal {
    return if (factura.formaPago.equals("EFECTIVO", true)) {
        subtotal.multiply(BigDecimal("0.33")).setScale(2, RoundingMode.HALF_UP)
    } else {
        BigDecimal.ZERO
    }
}

// ========= Theming =========

private val PrimaryColor = Color(0xFF2563EB)
private val SecondaryColor = Color(0xFF64748B)

@Composable
fun CliconTheme(content: @Composable () -> Unit) {
    val colors = androidx.compose.material3.lightColorScheme(
        primary = PrimaryColor,
        secondary = SecondaryColor,
        onPrimary = Color.White,
        onSecondary = Color.White
    )
    androidx.compose.material3.MaterialTheme(
        colorScheme = colors,
        typography = androidx.compose.material3.MaterialTheme.typography,
        content = content
    )
}
