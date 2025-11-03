package ec.edu.espe.comercializadora.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SoapService {

    // --------------------- USUARIO ---------------------
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IUserController/GetAllUsers\""
    )
    @POST("UserService.svc")
    suspend fun getAllUsers(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IUserController/GetUserById\""
    )
    @POST("UserService.svc")
    suspend fun getUserById(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IUserController/CreateUser\""
    )
    @POST("UserService.svc")
    suspend fun createUser(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IUserController/UpdateUser\""
    )
    @POST("UserService.svc")
    suspend fun updateUser(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IUserController/DeleteUser\""
    )
    @POST("UserService.svc")
    suspend fun deleteUser(@Body body: String): Response<ResponseBody>


    // --------------------- CLIENTE ---------------------
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IClienteController/GetAllClientes\""
    )
    @POST("ClienteService.svc")
    suspend fun getAllClientes(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IClienteController/GetClienteById\""
    )
    @POST("ClienteService.svc")
    suspend fun getClienteById(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IClienteController/CreateCliente\""
    )
    @POST("ClienteService.svc")
    suspend fun createCliente(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IClienteController/UpdateCliente\""
    )
    @POST("ClienteService.svc")
    suspend fun updateCliente(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IClienteController/DeleteCliente\""
    )
    @POST("ClienteService.svc")
    suspend fun deleteCliente(@Body body: String): Response<ResponseBody>


    // --------------------- ELECTRODOMÉSTICO ---------------------
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IElectrodomesticoController/GetAllElectrodomesticos\""
    )
    @POST("ElectrodomesticoService.svc")
    suspend fun getAllElectrodomesticos(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IElectrodomesticoController/GetElectrodomesticoById\""
    )
    @POST("ElectrodomesticoService.svc")
    suspend fun getElectrodomesticoById(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IElectrodomesticoController/CreateElectrodomestico\""
    )
    @POST("ElectrodomesticoService.svc")
    suspend fun createElectrodomestico(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IElectrodomesticoController/UpdateElectrodomestico\""
    )
    @POST("ElectrodomesticoService.svc")
    suspend fun updateElectrodomestico(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IElectrodomesticoController/DeleteElectrodomestico\""
    )
    @POST("ElectrodomesticoService.svc")
    suspend fun deleteElectrodomestico(@Body body: String): Response<ResponseBody>


    // --------------------- FACTURA ---------------------
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IFacturaController/GetAllFacturas\""
    )
    @POST("FacturaService.svc")
    suspend fun getAllFacturas(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IFacturaController/GetFacturaById\""
    )
    @POST("FacturaService.svc")
    suspend fun getFacturaById(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IFacturaController/CrearFacturaConValidacion\""
    )
    @POST("FacturaService.svc")
    suspend fun crearFacturaConValidacion(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IFacturaController/VerificarElegibilidadCredito\""
    )
    @POST("FacturaService.svc")
    suspend fun verificarElegibilidadCredito(@Body body: String): Response<ResponseBody>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/IFacturaController/DeleteFactura\""
    )
    @POST("FacturaService.svc")
    suspend fun deleteFactura(@Body body: String): Response<ResponseBody>
}
