package ec.edu.espe.arguello.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface SoapService {
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/IUserController/GetAllUsers\"")
    @POST("UserService.svc")
    suspend fun getAllUsers(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/IClienteBancoController/GetAllClientesBanco\"")
    @POST("ClienteBancoService.svc")
    suspend fun getAllClientes(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/ICuentaController/GetAllCuentas\"")
    @POST("CuentaService.svc")
    suspend fun getAllCuentas(@Body body: String): String

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/ICuentaController/GetCuentasByClienteBancoId\""
    )
    @POST("CuentaService.svc")
    suspend fun getCuentasByClienteId(@Body body: String): String

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IClienteBancoController/VerificarElegibilidadCliente\""
    )
    @POST("ClienteBancoService.svc")
    suspend fun verificarElegibilidadCliente(@Body body: String): String

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IClienteBancoController/CalcularMontoMaximoCredito\""
    )
    @POST("ClienteBancoService.svc")
    suspend fun calcularMontoMaximoCredito(@Body body: String): String


    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IClienteBancoController/AprobarCredito\""
    )
    @POST("ClienteBancoService.svc")
    suspend fun aprobarCredito(@Body body: String): String


    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/IClienteBancoController/GetAllCreditosBanco\"")
    @POST("ClienteBancoService.svc")
    suspend fun getAllCreditos(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/IClienteBancoController/GetCreditosByClienteId\"")
    @POST("ClienteBancoService.svc")
    suspend fun getCreditosByClienteId(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/IClienteBancoController/CreateClienteBanco\"")
    @POST("ClienteBancoService.svc")
    suspend fun createCliente(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/IClienteBancoController/UpdateClienteBanco\"")
    @POST("ClienteBancoService.svc")
    suspend fun updateCliente(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/IClienteBancoController/DeleteClienteBanco\"")
    @POST("ClienteBancoService.svc")
    suspend fun deleteCliente(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/ICuentaController/CreateCuenta\"")
    @POST("CuentaService.svc")
    suspend fun createCuenta(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/ICuentaController/UpdateCuenta\"")
    @POST("CuentaService.svc")
    suspend fun updateCuenta(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/ICuentaController/DeleteCuenta\"")
    @POST("CuentaService.svc")
    suspend fun deleteCuenta(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/ICreditoBancoController/CreateCreditoBanco\"")
    @POST("CreditoBancoService.svc")
    suspend fun createCredito(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/ICreditoBancoController/UpdateCreditoBanco\"")
    @POST("CreditoBancoService.svc")
    suspend fun updateCredito(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/ICreditoBancoController/DeleteCreditoBanco\"")
    @POST("CreditoBancoService.svc")
    suspend fun deleteCredito(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/IMovimientoController/CreateMovimiento\"")
    @POST("MovimientoService.svc")
    suspend fun createMovimiento(@Body body: String): String
    
    @Headers("Content-Type: text/xml; charset=utf-8", "SOAPAction: \"http://tempuri.org/IClienteBancoController/GetAmortizacionesByCredito\"")
    @POST("ClienteBancoService.svc")
    suspend fun getAmortizaciones(@Body body: String): String
    
    companion object {
        private const val BASE_URL = "http://10.0.2.2:5001/"
        
        fun create(): SoapService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(SoapService::class.java)
        }
    }
}
