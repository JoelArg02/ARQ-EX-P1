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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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

enum class HomeTab(val route: String, val label: String) {
    Productos("productos", "Productos"),
    Carrito("carrito", "Carrito"),
    Compras("compras", "Mis compras"),
    Admin("admin", "Admin")
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(controller: AppController, state: UiState, snackbarHostState: SnackbarHostState) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        topBar = { TopAppBar(title = { Text("Comercializadora Monster") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                HomeTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { androidx.compose.material3.Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = HomeTab.Productos.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(HomeTab.Productos.route) {
                ProductosScreen(state, onAdd = { p, qty -> controller.agregarAlCarrito(p, qty) }, onRefresh = { controller.cargarProductos() })
            }
            composable(HomeTab.Carrito.route) {
                CarritoScreen(state, total = controller.totalCarrito, onEliminar = controller::eliminarDelCarrito, onVaciar = controller::vaciarCarrito, onCheckout = controller::checkout)
            }
            composable(HomeTab.Compras.route) {
                MisComprasScreen(state, onBuscar = controller::cargarMisCompras, onDetalle = controller::cargarFacturaDetalle)
            }
            composable(HomeTab.Admin.route) {
                AdminProductosScreen(state = state, controller = controller)
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
        state.facturaActual?.let {
            Spacer(Modifier.height(12.dp))
            FacturaDetalleCard(it)
        }
    }

    if (mostrarCheckout) {
        CheckoutDialog(onDismiss = { mostrarCheckout = false }, onConfirm = { cedula, metodo, cuotas ->
            onCheckout(cedula, metodo, cuotas)
            mostrarCheckout = false
        })
    }
}

@Composable
fun CheckoutDialog(onDismiss: () -> Unit, onConfirm: (String, String, Int?) -> Unit) {
    var cedula by remember { mutableStateOf("") }
    var metodo by remember { mutableStateOf("EFECTIVO") }
    var cuotasText by remember { mutableStateOf("3") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = { onConfirm(cedula.trim(), metodo, cuotasText.toIntOrNull()) }) { Text("Pagar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Checkout") },
        text = {
            Column {
                OutlinedTextField(value = cedula, onValueChange = { cedula = it }, label = { Text("Cedula del cliente") }, singleLine = true)
                Spacer(Modifier.height(8.dp))
                Text("Forma de pago")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = metodo == "EFECTIVO", onClick = { metodo = "EFECTIVO" })
                    Text("Efectivo")
                    Spacer(Modifier.width(12.dp))
                    RadioButton(selected = metodo == "CREDITO_DIRECTO", onClick = { metodo = "CREDITO_DIRECTO" })
                    Text("Credito directo")
                }
                if (metodo == "CREDITO_DIRECTO") {
                    OutlinedTextField(
                        value = cuotasText,
                        onValueChange = { cuotasText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Numero de cuotas (3-24)") },
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
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("Mis compras", style = androidx.compose.material3.MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = cedula, onValueChange = { cedula = it }, label = { Text("Cedula") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Button(onClick = { onBuscar(cedula.trim()) }) { Text("Buscar") }
        Spacer(Modifier.height(12.dp))
        LazyColumn {
            items(state.misCompras) { factura ->
                FacturaResumenCard(factura = factura, onClick = { factura.idFactura?.let(onDetalle) })
                Spacer(Modifier.height(8.dp))
            }
        }
        state.facturaActual?.let {
            Spacer(Modifier.height(12.dp))
            FacturaDetalleCard(it)
        }
    }
}

@Composable
fun FacturaResumenCard(factura: FacturaResponseDTO, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant), onClick = onClick) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Factura #${factura.idFactura ?: "-"}", fontWeight = FontWeight.Bold)
            Text("Cliente: ${factura.nombreCliente ?: "-"} (${factura.cedulaCliente ?: ""})")
            Text("Total: $${factura.total ?: BigDecimal.ZERO} | Pago: ${factura.formaPago ?: "-"}")
        }
    }
}

@Composable
fun FacturaDetalleCard(factura: FacturaResponseDTO) {
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
                Text("- ${it.nombreProducto} x${it.cantidad} @ $${it.precioUnitario} = $${it.subtotal}")
            }

            factura.infoCredito?.let { credito ->
                Spacer(Modifier.height(8.dp))
                Text("Credito directo", fontWeight = FontWeight.SemiBold)
                Text("Credito #${credito.idCredito} | Monto: $${credito.montoCredito} | Cuotas: ${credito.numeroCuotas} | Valor cuota: $${credito.valorCuota} | Tasa: ${credito.tasaInteres}")
                val tabla = credito.tablaAmortizacion
                if (tabla != null && tabla.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text("Tabla de amortizacion:", fontWeight = FontWeight.SemiBold)
                    tabla.forEach { cuota ->
                        Text("Cuota ${cuota.numeroCuota}: Valor $${cuota.valorCuota} | Interes $${cuota.interesPagado} | Capital $${cuota.capitalPagado} | Saldo $${cuota.saldoRestante}")
                    }
                }
            }
        }
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
