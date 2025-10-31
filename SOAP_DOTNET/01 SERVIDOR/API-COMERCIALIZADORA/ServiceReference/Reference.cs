namespace BancoBanquitoServiceReference
{
    using System.Runtime.Serialization;
    
    
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    [System.Runtime.Serialization.DataContractAttribute(Name="ClienteBanco", Namespace="http://schemas.datacontract.org/2004/07/API_BANCO.Models.Entities")]
    public partial class ClienteBanco : object
    {
        
        private string CedulaField;
        
        private BancoBanquitoServiceReference.EstadoCivil EstadoCivilField;
        
        private System.DateTime FechaNacimientoField;
        
        private int IdField;
        
        private string NombreCompletoField;
        
        private bool TieneCreditoActivoField;
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public string Cedula
        {
            get
            {
                return this.CedulaField;
            }
            set
            {
                this.CedulaField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public BancoBanquitoServiceReference.EstadoCivil EstadoCivil
        {
            get
            {
                return this.EstadoCivilField;
            }
            set
            {
                this.EstadoCivilField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public System.DateTime FechaNacimiento
        {
            get
            {
                return this.FechaNacimientoField;
            }
            set
            {
                this.FechaNacimientoField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public int Id
        {
            get
            {
                return this.IdField;
            }
            set
            {
                this.IdField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public string NombreCompleto
        {
            get
            {
                return this.NombreCompletoField;
            }
            set
            {
                this.NombreCompletoField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public bool TieneCreditoActivo
        {
            get
            {
                return this.TieneCreditoActivoField;
            }
            set
            {
                this.TieneCreditoActivoField = value;
            }
        }
    }
    
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    [System.Runtime.Serialization.DataContractAttribute(Name="EstadoCivil", Namespace="http://schemas.datacontract.org/2004/07/API_BANCO.Models.Enums")]
    public enum EstadoCivil : int
    {
        
        [System.Runtime.Serialization.EnumMemberAttribute()]
        Soltero = 1,
        
        [System.Runtime.Serialization.EnumMemberAttribute()]
        Casado = 2,
        
        [System.Runtime.Serialization.EnumMemberAttribute()]
        Divorciado = 3,
        
        [System.Runtime.Serialization.EnumMemberAttribute()]
        Viudo = 4,
    }
    
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    [System.Runtime.Serialization.DataContractAttribute(Name="ClienteBancoCreateDto", Namespace="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Clientes")]
    public partial class ClienteBancoCreateDto : object
    {
        
        private string CedulaField;
        
        private BancoBanquitoServiceReference.EstadoCivil EstadoCivilField;
        
        private System.DateTime FechaNacimientoField;
        
        private string NombreCompletoField;
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public string Cedula
        {
            get
            {
                return this.CedulaField;
            }
            set
            {
                this.CedulaField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public BancoBanquitoServiceReference.EstadoCivil EstadoCivil
        {
            get
            {
                return this.EstadoCivilField;
            }
            set
            {
                this.EstadoCivilField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public System.DateTime FechaNacimiento
        {
            get
            {
                return this.FechaNacimientoField;
            }
            set
            {
                this.FechaNacimientoField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public string NombreCompleto
        {
            get
            {
                return this.NombreCompletoField;
            }
            set
            {
                this.NombreCompletoField = value;
            }
        }
    }
    
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    [System.Runtime.Serialization.DataContractAttribute(Name="VerificacionClienteResponseDto", Namespace="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Clientes")]
    public partial class VerificacionClienteResponseDto : object
    {
        
        private bool CumpleEdadEstadoCivilField;
        
        private bool EsClienteField;
        
        private bool EsElegibleField;
        
        private string MensajeField;
        
        private bool NoTieneCreditoActivoField;
        
        private bool TieneDepositoUltimoMesField;
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public bool CumpleEdadEstadoCivil
        {
            get
            {
                return this.CumpleEdadEstadoCivilField;
            }
            set
            {
                this.CumpleEdadEstadoCivilField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public bool EsCliente
        {
            get
            {
                return this.EsClienteField;
            }
            set
            {
                this.EsClienteField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public bool EsElegible
        {
            get
            {
                return this.EsElegibleField;
            }
            set
            {
                this.EsElegibleField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public string Mensaje
        {
            get
            {
                return this.MensajeField;
            }
            set
            {
                this.MensajeField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public bool NoTieneCreditoActivo
        {
            get
            {
                return this.NoTieneCreditoActivoField;
            }
            set
            {
                this.NoTieneCreditoActivoField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public bool TieneDepositoUltimoMes
        {
            get
            {
                return this.TieneDepositoUltimoMesField;
            }
            set
            {
                this.TieneDepositoUltimoMesField = value;
            }
        }
    }
    
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    [System.Runtime.Serialization.DataContractAttribute(Name="CalculoCreditoResponseDto", Namespace="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Creditos")]
    public partial class CalculoCreditoResponseDto : object
    {
        
        private string MensajeField;
        
        private decimal MontoMaximoCreditoField;
        
        private decimal PromedioDepositosField;
        
        private decimal PromedioRetirosField;
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public string Mensaje
        {
            get
            {
                return this.MensajeField;
            }
            set
            {
                this.MensajeField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal MontoMaximoCredito
        {
            get
            {
                return this.MontoMaximoCreditoField;
            }
            set
            {
                this.MontoMaximoCreditoField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal PromedioDepositos
        {
            get
            {
                return this.PromedioDepositosField;
            }
            set
            {
                this.PromedioDepositosField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal PromedioRetiros
        {
            get
            {
                return this.PromedioRetirosField;
            }
            set
            {
                this.PromedioRetirosField = value;
            }
        }
    }
    
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    [System.Runtime.Serialization.DataContractAttribute(Name="EvaluacionCreditoRequestDto", Namespace="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Creditos")]
    public partial class EvaluacionCreditoRequestDto : object
    {
        
        private string CedulaClienteField;
        
        private decimal MontoRequeridoField;
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public string CedulaCliente
        {
            get
            {
                return this.CedulaClienteField;
            }
            set
            {
                this.CedulaClienteField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal MontoRequerido
        {
            get
            {
                return this.MontoRequeridoField;
            }
            set
            {
                this.MontoRequeridoField = value;
            }
        }
    }
    
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    [System.Runtime.Serialization.DataContractAttribute(Name="EvaluacionCreditoResultadoDto", Namespace="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Creditos")]
    public partial class EvaluacionCreditoResultadoDto : object
    {
        
        private bool AprobadoField;
        
        private string DetalleField;
        
        private decimal LimiteMaximoCreditoField;
        
        private decimal PromedioDepositos3MesesField;
        
        private decimal PromedioRetiros3MesesField;
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public bool Aprobado
        {
            get
            {
                return this.AprobadoField;
            }
            set
            {
                this.AprobadoField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public string Detalle
        {
            get
            {
                return this.DetalleField;
            }
            set
            {
                this.DetalleField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal LimiteMaximoCredito
        {
            get
            {
                return this.LimiteMaximoCreditoField;
            }
            set
            {
                this.LimiteMaximoCreditoField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal PromedioDepositos3Meses
        {
            get
            {
                return this.PromedioDepositos3MesesField;
            }
            set
            {
                this.PromedioDepositos3MesesField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal PromedioRetiros3Meses
        {
            get
            {
                return this.PromedioRetiros3MesesField;
            }
            set
            {
                this.PromedioRetiros3MesesField = value;
            }
        }
    }
    
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    [System.Runtime.Serialization.DataContractAttribute(Name="AprobacionCreditoRequestDto", Namespace="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Creditos")]
    public partial class AprobacionCreditoRequestDto : object
    {
        
        private string CedulaClienteField;
        
        private decimal MontoSolicitadoField;
        
        private int NumeroCuotasField;
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public string CedulaCliente
        {
            get
            {
                return this.CedulaClienteField;
            }
            set
            {
                this.CedulaClienteField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal MontoSolicitado
        {
            get
            {
                return this.MontoSolicitadoField;
            }
            set
            {
                this.MontoSolicitadoField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public int NumeroCuotas
        {
            get
            {
                return this.NumeroCuotasField;
            }
            set
            {
                this.NumeroCuotasField = value;
            }
        }
    }
    
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    [System.Runtime.Serialization.DataContractAttribute(Name="AprobacionCreditoResponseDto", Namespace="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Creditos")]
    public partial class AprobacionCreditoResponseDto : object
    {
        
        private bool AprobadoField;
        
        private string MensajeField;
        
        private int NumeroCuotasField;
        
        private BancoBanquitoServiceReference.AmortizacionCreditoDto[] TablaAmortizacionField;
        
        private decimal ValorCuotaField;
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public bool Aprobado
        {
            get
            {
                return this.AprobadoField;
            }
            set
            {
                this.AprobadoField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public string Mensaje
        {
            get
            {
                return this.MensajeField;
            }
            set
            {
                this.MensajeField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public int NumeroCuotas
        {
            get
            {
                return this.NumeroCuotasField;
            }
            set
            {
                this.NumeroCuotasField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public BancoBanquitoServiceReference.AmortizacionCreditoDto[] TablaAmortizacion
        {
            get
            {
                return this.TablaAmortizacionField;
            }
            set
            {
                this.TablaAmortizacionField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal ValorCuota
        {
            get
            {
                return this.ValorCuotaField;
            }
            set
            {
                this.ValorCuotaField = value;
            }
        }
    }
    
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    [System.Runtime.Serialization.DataContractAttribute(Name="AmortizacionCreditoDto", Namespace="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Creditos")]
    public partial class AmortizacionCreditoDto : object
    {
        
        private decimal CapitalPagadoField;
        
        private decimal InteresPagadoField;
        
        private int NumeroCuotaField;
        
        private decimal SaldoPendienteField;
        
        private decimal ValorCuotaField;
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal CapitalPagado
        {
            get
            {
                return this.CapitalPagadoField;
            }
            set
            {
                this.CapitalPagadoField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal InteresPagado
        {
            get
            {
                return this.InteresPagadoField;
            }
            set
            {
                this.InteresPagadoField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public int NumeroCuota
        {
            get
            {
                return this.NumeroCuotaField;
            }
            set
            {
                this.NumeroCuotaField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal SaldoPendiente
        {
            get
            {
                return this.SaldoPendienteField;
            }
            set
            {
                this.SaldoPendienteField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal ValorCuota
        {
            get
            {
                return this.ValorCuotaField;
            }
            set
            {
                this.ValorCuotaField = value;
            }
        }
    }
    
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    [System.Runtime.Serialization.DataContractAttribute(Name="CreditoResumenDto", Namespace="http://schemas.datacontract.org/2004/07/API_BANCO.Application.DTOs.Creditos")]
    public partial class CreditoResumenDto : object
    {
        
        private string CedulaClienteField;
        
        private int CreditoIdField;
        
        private System.DateTime FechaAprobacionField;
        
        private System.DateTime FechaFinalizacionField;
        
        private decimal MontoAprobadoField;
        
        private string NombreClienteField;
        
        private decimal SaldoPendienteField;
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public string CedulaCliente
        {
            get
            {
                return this.CedulaClienteField;
            }
            set
            {
                this.CedulaClienteField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public int CreditoId
        {
            get
            {
                return this.CreditoIdField;
            }
            set
            {
                this.CreditoIdField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public System.DateTime FechaAprobacion
        {
            get
            {
                return this.FechaAprobacionField;
            }
            set
            {
                this.FechaAprobacionField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public System.DateTime FechaFinalizacion
        {
            get
            {
                return this.FechaFinalizacionField;
            }
            set
            {
                this.FechaFinalizacionField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal MontoAprobado
        {
            get
            {
                return this.MontoAprobadoField;
            }
            set
            {
                this.MontoAprobadoField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public string NombreCliente
        {
            get
            {
                return this.NombreClienteField;
            }
            set
            {
                this.NombreClienteField = value;
            }
        }
        
        [System.Runtime.Serialization.DataMemberAttribute()]
        public decimal SaldoPendiente
        {
            get
            {
                return this.SaldoPendienteField;
            }
            set
            {
                this.SaldoPendienteField = value;
            }
        }
    }
    
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    [System.ServiceModel.ServiceContractAttribute(ConfigurationName="BancoBanquitoServiceReference.IClienteBancoController")]
    public interface IClienteBancoController
    {
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/GetAllClientesBanco", ReplyAction="http://tempuri.org/IClienteBancoController/GetAllClientesBancoResponse")]
        System.Threading.Tasks.Task<BancoBanquitoServiceReference.ClienteBanco[]> GetAllClientesBancoAsync();
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/GetClienteBancoById", ReplyAction="http://tempuri.org/IClienteBancoController/GetClienteBancoByIdResponse")]
        System.Threading.Tasks.Task<BancoBanquitoServiceReference.ClienteBanco> GetClienteBancoByIdAsync(int id);
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/GetClienteBancoByCedula", ReplyAction="http://tempuri.org/IClienteBancoController/GetClienteBancoByCedulaResponse")]
        System.Threading.Tasks.Task<BancoBanquitoServiceReference.ClienteBanco> GetClienteBancoByCedulaAsync(string cedula);
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/CreateClienteBanco", ReplyAction="http://tempuri.org/IClienteBancoController/CreateClienteBancoResponse")]
        System.Threading.Tasks.Task<BancoBanquitoServiceReference.ClienteBanco> CreateClienteBancoAsync(BancoBanquitoServiceReference.ClienteBancoCreateDto dto);
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/UpdateClienteBanco", ReplyAction="http://tempuri.org/IClienteBancoController/UpdateClienteBancoResponse")]
        System.Threading.Tasks.Task<BancoBanquitoServiceReference.ClienteBanco> UpdateClienteBancoAsync(int id, string cedula, string nombreCompleto, int estadoCivil, System.DateTime fechaNacimiento, bool tieneCreditoActivo);
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/DeleteClienteBanco", ReplyAction="http://tempuri.org/IClienteBancoController/DeleteClienteBancoResponse")]
        System.Threading.Tasks.Task<bool> DeleteClienteBancoAsync(int id);
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/VerificarClientePorCedula", ReplyAction="http://tempuri.org/IClienteBancoController/VerificarClientePorCedulaResponse")]
        System.Threading.Tasks.Task<bool> VerificarClientePorCedulaAsync(string cedula);
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/VerificarElegibilidadCliente", ReplyAction="http://tempuri.org/IClienteBancoController/VerificarElegibilidadClienteResponse")]
        System.Threading.Tasks.Task<BancoBanquitoServiceReference.VerificacionClienteResponseDto> VerificarElegibilidadClienteAsync(string cedula);
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/CalcularMontoMaximoCredito", ReplyAction="http://tempuri.org/IClienteBancoController/CalcularMontoMaximoCreditoResponse")]
        System.Threading.Tasks.Task<BancoBanquitoServiceReference.CalculoCreditoResponseDto> CalcularMontoMaximoCreditoAsync(string cedula);
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/EvaluarCredito", ReplyAction="http://tempuri.org/IClienteBancoController/EvaluarCreditoResponse")]
        System.Threading.Tasks.Task<BancoBanquitoServiceReference.EvaluacionCreditoResultadoDto> EvaluarCreditoAsync(BancoBanquitoServiceReference.EvaluacionCreditoRequestDto dto);
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/AprobarCredito", ReplyAction="http://tempuri.org/IClienteBancoController/AprobarCreditoResponse")]
        System.Threading.Tasks.Task<BancoBanquitoServiceReference.AprobacionCreditoResponseDto> AprobarCreditoAsync(BancoBanquitoServiceReference.AprobacionCreditoRequestDto dto);
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/ObtenerCreditoPorCedula", ReplyAction="http://tempuri.org/IClienteBancoController/ObtenerCreditoPorCedulaResponse")]
        System.Threading.Tasks.Task<BancoBanquitoServiceReference.CreditoResumenDto> ObtenerCreditoPorCedulaAsync(string cedula);
        
        [System.ServiceModel.OperationContractAttribute(Action="http://tempuri.org/IClienteBancoController/ObtenerAmortizacionPorCreditoId", ReplyAction="http://tempuri.org/IClienteBancoController/ObtenerAmortizacionPorCreditoIdRespons" +
            "e")]
        System.Threading.Tasks.Task<BancoBanquitoServiceReference.AmortizacionCreditoDto[]> ObtenerAmortizacionPorCreditoIdAsync(int creditoId);
    }
    
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    public interface IClienteBancoControllerChannel : BancoBanquitoServiceReference.IClienteBancoController, System.ServiceModel.IClientChannel
    {
    }
    
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Tools.ServiceModel.Svcutil", "8.0.0")]
    public partial class ClienteBancoControllerClient : System.ServiceModel.ClientBase<BancoBanquitoServiceReference.IClienteBancoController>, BancoBanquitoServiceReference.IClienteBancoController
    {
        
        /// <summary>
        /// Implemente este método parcial para configurar el punto de conexión de servicio.
        /// </summary>
        /// <param name="serviceEndpoint">El punto de conexión para configurar</param>
        /// <param name="clientCredentials">Credenciales de cliente</param>
        static partial void ConfigureEndpoint(System.ServiceModel.Description.ServiceEndpoint serviceEndpoint, System.ServiceModel.Description.ClientCredentials clientCredentials);
        
        public ClienteBancoControllerClient() : 
                base(ClienteBancoControllerClient.GetDefaultBinding(), ClienteBancoControllerClient.GetDefaultEndpointAddress())
        {
            this.Endpoint.Name = EndpointConfiguration.BasicHttpBinding_IClienteBancoController.ToString();
            ConfigureEndpoint(this.Endpoint, this.ClientCredentials);
        }
        
        public ClienteBancoControllerClient(EndpointConfiguration endpointConfiguration) : 
                base(ClienteBancoControllerClient.GetBindingForEndpoint(endpointConfiguration), ClienteBancoControllerClient.GetEndpointAddress(endpointConfiguration))
        {
            this.Endpoint.Name = endpointConfiguration.ToString();
            ConfigureEndpoint(this.Endpoint, this.ClientCredentials);
        }
        
        public ClienteBancoControllerClient(EndpointConfiguration endpointConfiguration, string remoteAddress) : 
                base(ClienteBancoControllerClient.GetBindingForEndpoint(endpointConfiguration), new System.ServiceModel.EndpointAddress(remoteAddress))
        {
            this.Endpoint.Name = endpointConfiguration.ToString();
            ConfigureEndpoint(this.Endpoint, this.ClientCredentials);
        }
        
        public ClienteBancoControllerClient(EndpointConfiguration endpointConfiguration, System.ServiceModel.EndpointAddress remoteAddress) : 
                base(ClienteBancoControllerClient.GetBindingForEndpoint(endpointConfiguration), remoteAddress)
        {
            this.Endpoint.Name = endpointConfiguration.ToString();
            ConfigureEndpoint(this.Endpoint, this.ClientCredentials);
        }
        
        public ClienteBancoControllerClient(System.ServiceModel.Channels.Binding binding, System.ServiceModel.EndpointAddress remoteAddress) : 
                base(binding, remoteAddress)
        {
        }
        
        public System.Threading.Tasks.Task<BancoBanquitoServiceReference.ClienteBanco[]> GetAllClientesBancoAsync()
        {
            return base.Channel.GetAllClientesBancoAsync();
        }
        
        public System.Threading.Tasks.Task<BancoBanquitoServiceReference.ClienteBanco> GetClienteBancoByIdAsync(int id)
        {
            return base.Channel.GetClienteBancoByIdAsync(id);
        }
        
        public System.Threading.Tasks.Task<BancoBanquitoServiceReference.ClienteBanco> GetClienteBancoByCedulaAsync(string cedula)
        {
            return base.Channel.GetClienteBancoByCedulaAsync(cedula);
        }
        
        public System.Threading.Tasks.Task<BancoBanquitoServiceReference.ClienteBanco> CreateClienteBancoAsync(BancoBanquitoServiceReference.ClienteBancoCreateDto dto)
        {
            return base.Channel.CreateClienteBancoAsync(dto);
        }
        
        public System.Threading.Tasks.Task<BancoBanquitoServiceReference.ClienteBanco> UpdateClienteBancoAsync(int id, string cedula, string nombreCompleto, int estadoCivil, System.DateTime fechaNacimiento, bool tieneCreditoActivo)
        {
            return base.Channel.UpdateClienteBancoAsync(id, cedula, nombreCompleto, estadoCivil, fechaNacimiento, tieneCreditoActivo);
        }
        
        public System.Threading.Tasks.Task<bool> DeleteClienteBancoAsync(int id)
        {
            return base.Channel.DeleteClienteBancoAsync(id);
        }
        
        public System.Threading.Tasks.Task<bool> VerificarClientePorCedulaAsync(string cedula)
        {
            return base.Channel.VerificarClientePorCedulaAsync(cedula);
        }
        
        public System.Threading.Tasks.Task<BancoBanquitoServiceReference.VerificacionClienteResponseDto> VerificarElegibilidadClienteAsync(string cedula)
        {
            return base.Channel.VerificarElegibilidadClienteAsync(cedula);
        }
        
        public System.Threading.Tasks.Task<BancoBanquitoServiceReference.CalculoCreditoResponseDto> CalcularMontoMaximoCreditoAsync(string cedula)
        {
            return base.Channel.CalcularMontoMaximoCreditoAsync(cedula);
        }
        
        public System.Threading.Tasks.Task<BancoBanquitoServiceReference.EvaluacionCreditoResultadoDto> EvaluarCreditoAsync(BancoBanquitoServiceReference.EvaluacionCreditoRequestDto dto)
        {
            return base.Channel.EvaluarCreditoAsync(dto);
        }
        
        public System.Threading.Tasks.Task<BancoBanquitoServiceReference.AprobacionCreditoResponseDto> AprobarCreditoAsync(BancoBanquitoServiceReference.AprobacionCreditoRequestDto dto)
        {
            return base.Channel.AprobarCreditoAsync(dto);
        }
        
        public System.Threading.Tasks.Task<BancoBanquitoServiceReference.CreditoResumenDto> ObtenerCreditoPorCedulaAsync(string cedula)
        {
            return base.Channel.ObtenerCreditoPorCedulaAsync(cedula);
        }
        
        public System.Threading.Tasks.Task<BancoBanquitoServiceReference.AmortizacionCreditoDto[]> ObtenerAmortizacionPorCreditoIdAsync(int creditoId)
        {
            return base.Channel.ObtenerAmortizacionPorCreditoIdAsync(creditoId);
        }
        
        public virtual System.Threading.Tasks.Task OpenAsync()
        {
            return System.Threading.Tasks.Task.Factory.FromAsync(((System.ServiceModel.ICommunicationObject)(this)).BeginOpen(null, null), new System.Action<System.IAsyncResult>(((System.ServiceModel.ICommunicationObject)(this)).EndOpen));
        }
        
        #if !NET6_0_OR_GREATER
        public virtual System.Threading.Tasks.Task CloseAsync()
        {
            return System.Threading.Tasks.Task.Factory.FromAsync(((System.ServiceModel.ICommunicationObject)(this)).BeginClose(null, null), new System.Action<System.IAsyncResult>(((System.ServiceModel.ICommunicationObject)(this)).EndClose));
        }
        #endif
        
        private static System.ServiceModel.Channels.Binding GetBindingForEndpoint(EndpointConfiguration endpointConfiguration)
        {
            if ((endpointConfiguration == EndpointConfiguration.BasicHttpBinding_IClienteBancoController))
            {
                System.ServiceModel.BasicHttpBinding result = new System.ServiceModel.BasicHttpBinding();
                result.MaxBufferSize = int.MaxValue;
                result.ReaderQuotas = System.Xml.XmlDictionaryReaderQuotas.Max;
                result.MaxReceivedMessageSize = int.MaxValue;
                result.AllowCookies = true;
                return result;
            }
            throw new System.InvalidOperationException(string.Format("No se pudo encontrar un punto de conexión con el nombre \"{0}\".", endpointConfiguration));
        }
        
        private static System.ServiceModel.EndpointAddress GetEndpointAddress(EndpointConfiguration endpointConfiguration)
        {
            if ((endpointConfiguration == EndpointConfiguration.BasicHttpBinding_IClienteBancoController))
            {
                return new System.ServiceModel.EndpointAddress("http://[::]:5001/ClienteBancoService.svc");
            }
            throw new System.InvalidOperationException(string.Format("No se pudo encontrar un punto de conexión con el nombre \"{0}\".", endpointConfiguration));
        }
        
        private static System.ServiceModel.Channels.Binding GetDefaultBinding()
        {
            return ClienteBancoControllerClient.GetBindingForEndpoint(EndpointConfiguration.BasicHttpBinding_IClienteBancoController);
        }
        
        private static System.ServiceModel.EndpointAddress GetDefaultEndpointAddress()
        {
            return ClienteBancoControllerClient.GetEndpointAddress(EndpointConfiguration.BasicHttpBinding_IClienteBancoController);
        }
        
        public enum EndpointConfiguration
        {
            
            BasicHttpBinding_IClienteBancoController,
        }
    }
}
