package ec.edu.espe.arguello.repository

import android.util.Log
import ec.edu.espe.arguello.models.*
import ec.edu.espe.arguello.network.SoapService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

class BancoRepository {
    private val soapService = SoapService.create()
    private val TAG = "BancoRepository"

    suspend fun getAllUsers(): List<User> =
            withContext(Dispatchers.IO) {
                try {
                    val soapRequest =
                            """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetAllUsers/>
                    </soap:Body>
                </soap:Envelope>
            """.trimIndent()

                    val response = soapService.getAllUsers(soapRequest)
                    parseUsersFromXml(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }

    suspend fun getAllClientes(): List<ClienteBanco> =
            withContext(Dispatchers.IO) {
                val soapRequest =
                        """
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                <soap:Header/>
                <soap:Body>
                    <tem:GetAllClientesBanco/>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

                Log.d(TAG, "Enviando request de clientes...")
                val response = soapService.getAllClientes(soapRequest)
                Log.d(TAG, "Respuesta recibida: ${response.take(500)}...")
                val clientes = parseClientesFromXml(response)
                Log.d(TAG, "Clientes parseados: ${clientes.size}")
                clientes
            }

    suspend fun getAllCuentas(): List<Cuenta> =
            withContext(Dispatchers.IO) {
                val soapRequest =
                        """
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                <soap:Header/>
                <soap:Body>
                    <tem:GetAllCuentas/>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

                val response = soapService.getAllCuentas(soapRequest)
                parseCuentasFromXml(response)
            }

    suspend fun getCuentasByClienteId(clienteId: Int): List<Cuenta> =
        withContext(Dispatchers.IO) {
            try {
                val soapRequest = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:tem="http://tempuri.org/">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <tem:GetCuentasByClienteBancoId>
                         <tem:clienteBancoId>$clienteId</tem:clienteBancoId>
                      </tem:GetCuentasByClienteBancoId>
                   </soapenv:Body>
                </soapenv:Envelope>
            """.trimIndent()

                android.util.Log.e("SOAP", "Request XML (GetCuentasByClienteBancoId):\n$soapRequest")

                val response = soapService.getCuentasByClienteId(soapRequest)

                android.util.Log.d("SOAP", "Response XML (GetCuentasByClienteBancoId):\n$response")

                parseCuentasFromXml(response)
            } catch (e: Exception) {
                android.util.Log.e("SOAP", "Error en GetCuentasByClienteBancoId: ${e.message}", e)
                emptyList()
            }
        }

    suspend fun calcularMontoMaximoCredito(cedula: String): MontoMaximoCredito? =
        withContext(Dispatchers.IO) {
            try {
                val soapRequest = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:tem="http://tempuri.org/">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <tem:CalcularMontoMaximoCredito>
                         <tem:cedula>$cedula</tem:cedula>
                      </tem:CalcularMontoMaximoCredito>
                   </soapenv:Body>
                </soapenv:Envelope>
            """.trimIndent()

                val response = soapService.calcularMontoMaximoCredito(soapRequest)
                parseMontoMaximoFromXml(response)
            } catch (e: Exception) {
                null
            }
        }
    fun parseMontoMaximoFromXml(xml: String): MontoMaximoCredito? {
        val monto = Regex("<a:MontoMaximoCredito>(.*?)</a:MontoMaximoCredito>").find(xml)?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
        val dep = Regex("<a:PromedioDepositos>(.*?)</a:PromedioDepositos>").find(xml)?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
        val ret = Regex("<a:PromedioRetiros>(.*?)</a:PromedioRetiros>").find(xml)?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0
        val msg = Regex("<a:Mensaje>(.*?)</a:Mensaje>").find(xml)?.groupValues?.get(1)?.replace("&#xF1;", "ñ") ?: ""
        return MontoMaximoCredito(monto, dep, ret, msg)
    }


    suspend fun verificarElegibilidadCliente(cedula: String): ElegibilidadCliente? =
        withContext(Dispatchers.IO) {
            try {
                val soapRequest = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:tem="http://tempuri.org/">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <tem:VerificarElegibilidadCliente>
                         <tem:cedula>$cedula</tem:cedula>
                      </tem:VerificarElegibilidadCliente>
                   </soapenv:Body>
                </soapenv:Envelope>
            """.trimIndent()

                android.util.Log.e("SOAP", "Request XML (VerificarElegibilidadCliente):\n$soapRequest")

                val response = soapService.verificarElegibilidadCliente(soapRequest)

                android.util.Log.d("SOAP", "Response XML (VerificarElegibilidadCliente):\n$response")

                parseElegibilidadFromXml(response)
            } catch (e: Exception) {
                android.util.Log.e("SOAP", "Error en VerificarElegibilidadCliente: ${e.message}", e)
                null
            }
        }

    fun parseElegibilidadFromXml(xml: String): ElegibilidadCliente? {
        return try {
            val cumpleEdad = xml.contains("<a:CumpleEdadEstadoCivil>true</a:CumpleEdadEstadoCivil>")
            val esCliente = xml.contains("<a:EsCliente>true</a:EsCliente>")
            val tieneDeposito = xml.contains("<a:TieneDepositoUltimoMes>true</a:TieneDepositoUltimoMes>")
            val noTieneCredito = xml.contains("<a:NoTieneCreditoActivo>true</a:NoTieneCreditoActivo>")
            val esElegible = xml.contains("<a:EsElegible>true</a:EsElegible>")
            val mensaje = Regex("<a:Mensaje>(.*?)</a:Mensaje>").find(xml)?.groupValues?.get(1)
                ?.replace("&#xF1;", "ñ") ?: "Sin mensaje"

            ElegibilidadCliente(
                esCliente = esCliente,
                cumpleEdadEstadoCivil = cumpleEdad,
                tieneDepositoUltimoMes = tieneDeposito,
                noTieneCreditoActivo = noTieneCredito,
                esElegible = esElegible,
                mensaje = mensaje
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllCreditos(): List<CreditoBanco> =
            withContext(Dispatchers.IO) {
                val soapRequest =
                        """
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                <soap:Header/>
                <soap:Body>
                    <tem:GetAllCreditosBanco/>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

                val response = soapService.getAllCreditos(soapRequest)
                parseCreditosFromXml(response)
            }

    suspend fun getCreditosByClienteId(clienteId: Int): List<CreditoBanco> =
            withContext(Dispatchers.IO) {
                try {
                    val soapRequest =
                            """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                    <soap:Header/>
                    <soap:Body>
                        <tem:GetCreditosByClienteId>
                            <tem:clienteId>$clienteId</tem:clienteId>
                        </tem:GetCreditosByClienteId>
                    </soap:Body>
                </soap:Envelope>
            """.trimIndent()

                    val response = soapService.getCreditosByClienteId(soapRequest)
                    parseCreditosFromXml(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }

    suspend fun createCliente(cliente: ClienteBanco): ClienteBanco =
        withContext(Dispatchers.IO) {
            val soapRequest = """
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
                           xmlns:tem="http://tempuri.org/"
                           xmlns:api="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Clientes">
                <soap:Header/>
                <soap:Body>
                    <tem:CreateClienteBanco>
                        <tem:dto>
                            <api:Cedula>${cliente.cedula}</api:Cedula>
                            <api:EstadoCivil>${cliente.estadoCivil}</api:EstadoCivil>
                            <api:FechaNacimiento>${cliente.fechaNacimiento}</api:FechaNacimiento>
                            <api:NombreCompleto>${cliente.nombreCompleto}</api:NombreCompleto>
                        </tem:dto>
                    </tem:CreateClienteBanco>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

            android.util.Log.e("SOAP", "Request XML:\n$soapRequest")

            val response = soapService.createCliente(soapRequest)
            parseClienteFromXml(response)
        }




    suspend fun updateCliente(cliente: ClienteBanco): ClienteBanco =
            withContext(Dispatchers.IO) {
                val soapRequest =
                        """
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                <soap:Header/>
                <soap:Body>
                    <tem:UpdateClienteBanco>
                        <tem:id>${cliente.id}</tem:id>
                        <tem:clienteBanco>
                            <tem:Id>${cliente.id}</tem:Id>
                            <tem:Cedula>${cliente.cedula}</tem:Cedula>
                            <tem:NombreCompleto>${cliente.nombreCompleto}</tem:NombreCompleto>
                            <tem:EstadoCivil>${cliente.estadoCivil}</tem:EstadoCivil>
                            <tem:FechaNacimiento>${cliente.fechaNacimiento}</tem:FechaNacimiento>
                            <tem:TieneCreditoActivo>${cliente.tieneCreditoActivo}</tem:TieneCreditoActivo>
                        </tem:clienteBanco>
                    </tem:UpdateClienteBanco>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

                val response = soapService.updateCliente(soapRequest)
                parseClienteFromXml(response)
            }

    suspend fun deleteCliente(clienteId: Int): Boolean =
            withContext(Dispatchers.IO) {
                val soapRequest =
                        """
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                <soap:Header/>
                <soap:Body>
                    <tem:DeleteClienteBanco>
                        <tem:id>$clienteId</tem:id>
                    </tem:DeleteClienteBanco>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

                soapService.deleteCliente(soapRequest)
                true
            }

    suspend fun createCuenta(cuenta: Cuenta): Cuenta =
        withContext(Dispatchers.IO) {
            val tipoCuentaNumero = when (cuenta.tipoCuenta.trim()) {
                "Ahorros" -> 1
                "Corriente" -> 2
                else -> 1
            }

            val soapRequest = """
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
                           xmlns:tem="http://tempuri.org/">
                <soap:Header/>
                <soap:Body>
                    <tem:CreateCuenta>
                        <tem:clienteBancoId>${cuenta.clienteBancoId}</tem:clienteBancoId>
                        <tem:numeroCuenta>${cuenta.numeroCuenta}</tem:numeroCuenta>
                        <tem:saldo>${cuenta.saldo}</tem:saldo>
                        <tem:tipoCuenta>$tipoCuentaNumero</tem:tipoCuenta>
                    </tem:CreateCuenta>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

            android.util.Log.e("SOAP", "Request XML:\n$soapRequest")

            val response = soapService.createCuenta(soapRequest)
            parseCuentaFromXml(response)
        }



    suspend fun updateCuenta(cuenta: Cuenta): Cuenta =
            withContext(Dispatchers.IO) {
                val soapRequest =
                        """
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                <soap:Header/>
                <soap:Body>
                    <tem:UpdateCuenta>
                        <tem:id>${cuenta.id}</tem:id>
                        <tem:cuenta>
                            <tem:Id>${cuenta.id}</tem:Id>
                            <tem:ClienteBancoId>${cuenta.clienteBancoId}</tem:ClienteBancoId>
                            <tem:NumeroCuenta>${cuenta.numeroCuenta}</tem:NumeroCuenta>
                            <tem:Saldo>${cuenta.saldo}</tem:Saldo>
                            <tem:TipoCuenta>${cuenta.tipoCuenta}</tem:TipoCuenta>
                        </tem:cuenta>
                    </tem:UpdateCuenta>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

                val response = soapService.updateCuenta(soapRequest)
                parseCuentaFromXml(response)
            }

    suspend fun deleteCuenta(cuentaId: Int): Boolean =
            withContext(Dispatchers.IO) {
                val soapRequest =
                        """
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                <soap:Header/>
                <soap:Body>
                    <tem:DeleteCuenta>
                        <tem:id>$cuentaId</tem:id>
                    </tem:DeleteCuenta>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

                soapService.deleteCuenta(soapRequest)
                true
            }

    suspend fun createCredito(cedulaCliente: String, montoSolicitado: Double, numeroCuotas: Int): CreditoBanco =
        withContext(Dispatchers.IO) {
            val soapRequest = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                              xmlns:tem="http://tempuri.org/"
                              xmlns:api="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Creditos">
               <soapenv:Header/>
               <soapenv:Body>
                  <tem:AprobarCredito>
                     <tem:dto>
                        <api:CedulaCliente>$cedulaCliente</api:CedulaCliente>
                        <api:MontoSolicitado>$montoSolicitado</api:MontoSolicitado>
                        <api:NumeroCuotas>$numeroCuotas</api:NumeroCuotas>
                     </tem:dto>
                  </tem:AprobarCredito>
               </soapenv:Body>
            </soapenv:Envelope>
        """.trimIndent()

            val response = soapService.aprobarCredito(soapRequest)
            parseCreditoFromXml(response)
        }

    suspend fun deleteCredito(creditoId: Int): Boolean =
            withContext(Dispatchers.IO) {
                val soapRequest =
                        """
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                <soap:Header/>
                <soap:Body>
                    <tem:DeleteCreditoBanco>
                        <tem:id>$creditoId</tem:id>
                    </tem:DeleteCreditoBanco>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

                soapService.deleteCredito(soapRequest)
                true
            }

    suspend fun createMovimiento(movimiento: Movimiento): Movimiento =
            withContext(Dispatchers.IO) {
                // Backend enum: Deposito = 1, Retiro = 2
                val tipo = if (movimiento.tipoMovimiento == "Deposito") 1 else 2
                val soapRequest =
                        """
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                <soap:Header/>
                <soap:Body>
                    <tem:CreateMovimiento>
                        <tem:cuentaId>${movimiento.cuentaId}</tem:cuentaId>
                        <tem:tipo>$tipo</tem:tipo>
                        <tem:monto>${movimiento.monto}</tem:monto>
                    </tem:CreateMovimiento>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

                val response = soapService.createMovimiento(soapRequest)
                parseMovimientoFromXml(response)
            }

    suspend fun getAmortizacionesByCredito(creditoId: Int): List<AmortizacionCredito> =
            withContext(Dispatchers.IO) {
                val soapRequest =
                        """
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:tem="http://tempuri.org/">
                <soap:Header/>
                <soap:Body>
                    <tem:GetAmortizacionesByCredito>
                        <tem:creditoId>$creditoId</tem:creditoId>
                    </tem:GetAmortizacionesByCredito>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

                val response = soapService.getAmortizaciones(soapRequest)
                parseAmortizacionesFromXml(response)
            }

    private fun parseUsersFromXml(xml: String): List<User> {
        val users = mutableListOf<User>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            val parser = factory.newPullParser()
            parser.setInput(xml.reader())

            var eventType = parser.eventType
            var currentUser = User()
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                        if (currentTag == "User") {
                            currentUser = User()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "Id" -> currentUser = currentUser.copy(id = text.toIntOrNull() ?: 0)
                                "Nombre" -> currentUser = currentUser.copy(nombre = text)
                                "Contrasena" -> currentUser = currentUser.copy(contrasena = text)
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "User" && currentUser.id != 0) {
                            users.add(currentUser)
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return users
    }

    private fun parseClientesFromXml(xml: String): List<ClienteBanco> {
        val clientes = mutableListOf<ClienteBanco>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(xml.reader())

            var eventType = parser.eventType
            var currentCliente = ClienteBanco()
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                        if (currentTag == "ClienteBanco") {
                            currentCliente = ClienteBanco()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "Id" ->
                                        currentCliente =
                                                currentCliente.copy(id = text.toIntOrNull() ?: 0)
                                "Cedula" -> currentCliente = currentCliente.copy(cedula = text)
                                "NombreCompleto" ->
                                        currentCliente = currentCliente.copy(nombreCompleto = text)
                                "EstadoCivil" ->
                                        currentCliente = currentCliente.copy(estadoCivil = text)
                                "FechaNacimiento" ->
                                        currentCliente = currentCliente.copy(fechaNacimiento = text)
                                "TieneCreditoActivo" ->
                                        currentCliente =
                                                currentCliente.copy(
                                                        tieneCreditoActivo =
                                                                text.equals(
                                                                        "true",
                                                                        ignoreCase = true
                                                                )
                                                )
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "ClienteBanco" && currentCliente.id != 0) {
                            clientes.add(currentCliente)
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error al parsear clientes: ${e.message}")
        }
        return clientes
    }

    private fun parseCuentasFromXml(xml: String): List<Cuenta> {
        val cuentas = mutableListOf<Cuenta>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(xml.reader())

            var eventType = parser.eventType
            var currentCuenta = Cuenta()
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                        if (currentTag == "Cuenta") {
                            currentCuenta = Cuenta()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "Id" ->
                                        currentCuenta =
                                                currentCuenta.copy(id = text.toIntOrNull() ?: 0)
                                "ClienteBancoId" ->
                                        currentCuenta =
                                                currentCuenta.copy(
                                                        clienteBancoId = text.toIntOrNull() ?: 0
                                                )
                                "NumeroCuenta" ->
                                        currentCuenta = currentCuenta.copy(numeroCuenta = text)
                                "Saldo" ->
                                        currentCuenta =
                                                currentCuenta.copy(
                                                        saldo = text.toDoubleOrNull() ?: 0.0
                                                )
                                "TipoCuenta" ->
                                        currentCuenta = currentCuenta.copy(tipoCuenta = text)
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "Cuenta" && currentCuenta.id != 0) {
                            cuentas.add(currentCuenta)
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error al parsear cuentas: ${e.message}")
        }
        return cuentas
    }

    private fun parseCreditosFromXml(xml: String): List<CreditoBanco> {
        val creditos = mutableListOf<CreditoBanco>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(xml.reader())

            var eventType = parser.eventType
            var currentCredito = CreditoBanco()
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                        if (currentTag == "CreditoBanco") {
                            currentCredito = CreditoBanco()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "Id" ->
                                    currentCredito = currentCredito.copy(id = text.toIntOrNull() ?: 0)
                                "ClienteBancoId" ->
                                    currentCredito = currentCredito.copy(clienteBancoId = text.toIntOrNull() ?: 0)
                                "MontoAprobado" ->
                                    currentCredito = currentCredito.copy(montoAprobado = text.toDoubleOrNull() ?: 0.0)
                                "NumeroCuotas" ->
                                    currentCredito = currentCredito.copy(numeroCuotas = text.toIntOrNull() ?: 0)
                                "TasaInteres" ->
                                    currentCredito = currentCredito.copy(tasaInteres = text.toDoubleOrNull() ?: 0.16)
                                "FechaAprobacion" ->
                                    currentCredito = currentCredito.copy(fechaAprobacion = text)
                                "Activo" ->
                                    currentCredito = currentCredito.copy(activo = text.toBoolean())
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "CreditoBanco") {
                            creditos.add(currentCredito)
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error al parsear créditos: ${e.message}")
        }
        return creditos
    }


    private fun parseClienteFromXml(xml: String): ClienteBanco {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(xml.reader())

        var eventType = parser.eventType
        var cliente = ClienteBanco()
        var currentTag = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> currentTag = parser.name
                XmlPullParser.TEXT -> {
                    val text = parser.text?.trim() ?: ""
                    if (text.isNotEmpty()) {
                        when (currentTag) {
                            "Id" -> cliente = cliente.copy(id = text.toIntOrNull() ?: 0)
                            "Cedula" -> cliente = cliente.copy(cedula = text)
                            "NombreCompleto" -> cliente = cliente.copy(nombreCompleto = text)
                            "EstadoCivil" -> cliente = cliente.copy(estadoCivil = text)
                            "FechaNacimiento" -> cliente = cliente.copy(fechaNacimiento = text)
                            "TieneCreditoActivo" ->
                                    cliente =
                                            cliente.copy(
                                                    tieneCreditoActivo =
                                                            text.equals("true", ignoreCase = true)
                                            )
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return cliente
    }

    private fun parseCuentaFromXml(xml: String): Cuenta {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(xml.reader())

        var eventType = parser.eventType
        var cuenta = Cuenta()
        var currentTag = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> currentTag = parser.name
                XmlPullParser.TEXT -> {
                    val text = parser.text?.trim() ?: ""
                    if (text.isNotEmpty()) {
                        when (currentTag) {
                            "Id" -> cuenta = cuenta.copy(id = text.toIntOrNull() ?: 0)
                            "ClienteBancoId" ->
                                    cuenta = cuenta.copy(clienteBancoId = text.toIntOrNull() ?: 0)
                            "NumeroCuenta" -> cuenta = cuenta.copy(numeroCuenta = text)
                            "Saldo" -> cuenta = cuenta.copy(saldo = text.toDoubleOrNull() ?: 0.0)
                            "TipoCuenta" -> cuenta = cuenta.copy(tipoCuenta = text)
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return cuenta
    }

    private fun parseCreditoFromXml(xml: String): CreditoBanco {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(xml.reader())

        var eventType = parser.eventType
        var credito = CreditoBanco()
        var currentTag = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> currentTag = parser.name
                XmlPullParser.TEXT -> {
                    val text = parser.text?.trim() ?: ""
                    if (text.isNotEmpty()) {
                        when (currentTag) {
                            "Id" ->
                                credito = credito.copy(id = text.toIntOrNull() ?: 0)
                            "ClienteBancoId" ->
                                credito = credito.copy(clienteBancoId = text.toIntOrNull() ?: 0)
                            "MontoAprobado" ->
                                credito = credito.copy(montoAprobado = text.toDoubleOrNull() ?: 0.0)
                            "NumeroCuotas" ->
                                credito = credito.copy(numeroCuotas = text.toIntOrNull() ?: 0)
                            "TasaInteres" ->
                                credito = credito.copy(tasaInteres = text.toDoubleOrNull() ?: 0.16)
                            "FechaAprobacion" ->
                                credito = credito.copy(fechaAprobacion = text)
                            "Activo" ->
                                credito = credito.copy(activo = text.toBoolean())
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        return credito
    }

    private fun parseMovimientoFromXml(xml: String): Movimiento {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(xml.reader())

        var eventType = parser.eventType
        var movimiento = Movimiento()
        var currentTag = ""

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> currentTag = parser.name
                XmlPullParser.TEXT -> {
                    val text = parser.text?.trim() ?: ""
                    if (text.isNotEmpty()) {
                        when (currentTag) {
                            "Id" -> movimiento = movimiento.copy(id = text.toIntOrNull() ?: 0)
                            "CuentaId" ->
                                    movimiento = movimiento.copy(cuentaId = text.toIntOrNull() ?: 0)
                            "TipoMovimiento" -> movimiento = movimiento.copy(tipoMovimiento = text)
                            "Monto" ->
                                    movimiento =
                                            movimiento.copy(monto = text.toDoubleOrNull() ?: 0.0)
                            "Fecha" -> movimiento = movimiento.copy(fecha = text)
                            "Descripcion" -> movimiento = movimiento.copy(descripcion = text)
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return movimiento
    }

    private fun parseAmortizacionesFromXml(xml: String): List<AmortizacionCredito> {
        val amortizaciones = mutableListOf<AmortizacionCredito>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(xml.reader())

            var eventType = parser.eventType
            var currentAmortizacion = AmortizacionCredito()
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name
                        if (currentTag == "AmortizacionCreditoDto") {
                            currentAmortizacion = AmortizacionCredito()
                        }
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "NumeroCuota" ->
                                        currentAmortizacion =
                                                currentAmortizacion.copy(
                                                        numeroCuota = text.toIntOrNull() ?: 0
                                                )
                                "ValorCuota" ->
                                        currentAmortizacion =
                                                currentAmortizacion.copy(
                                                        montoCuota = text.toDoubleOrNull() ?: 0.0
                                                )
                                "CapitalPagado" ->
                                        currentAmortizacion =
                                                currentAmortizacion.copy(
                                                        capital = text.toDoubleOrNull() ?: 0.0
                                                )
                                "InteresPagado" ->
                                        currentAmortizacion =
                                                currentAmortizacion.copy(
                                                        interes = text.toDoubleOrNull() ?: 0.0
                                                )
                                "SaldoPendiente" ->
                                        currentAmortizacion =
                                                currentAmortizacion.copy(
                                                        saldoPendiente = text.toDoubleOrNull()
                                                                        ?: 0.0
                                                )
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "AmortizacionCreditoDto" && currentAmortizacion.numeroCuota != 0) {
                            amortizaciones.add(currentAmortizacion)
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error al parsear amortizaciones: ${e.message}")
        }
        return amortizaciones
    }
}
