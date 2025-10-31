using System.Runtime.Serialization;

namespace API_BANCO.Application.DTOs.Creditos
{

    [DataContract]
    public class CalculoCreditoResponseDto
    {
        [DataMember]
        public decimal PromedioDepositos { get; set; }

        [DataMember]
        public decimal PromedioRetiros { get; set; }

        [DataMember]
        public decimal MontoMaximoCredito { get; set; }

        [DataMember]
        public string Mensaje { get; set; } = string.Empty;
    }

    [DataContract]
    public class EvaluacionCreditoRequestDto
    {
        [DataMember]
        public string CedulaCliente { get; set; } = string.Empty;

        [DataMember]
        public decimal MontoRequerido { get; set; }
    }

    [DataContract]
    public class EvaluacionCreditoResultadoDto
    {
        [DataMember]
        public decimal PromedioDepositos3Meses { get; set; }

        [DataMember]
        public decimal PromedioRetiros3Meses { get; set; }

        [DataMember]
        public decimal LimiteMaximoCredito { get; set; }

        [DataMember]
        public bool Aprobado { get; set; }

        [DataMember]
        public string Detalle { get; set; } = string.Empty;
    }

    [DataContract]
    public class AprobacionCreditoRequestDto
    {
        [DataMember]
        public string CedulaCliente { get; set; } = string.Empty;

        [DataMember]
        public decimal MontoSolicitado { get; set; }

        [DataMember]
        public int NumeroCuotas { get; set; }
    }

    [DataContract]
    public class AprobacionCreditoResponseDto
    {
        [DataMember]
        public bool Aprobado { get; set; }

        [DataMember]
        public string Mensaje { get; set; } = string.Empty;

        [DataMember]
        public decimal ValorCuota { get; set; }

        [DataMember]
        public int NumeroCuotas { get; set; }

        [DataMember]
        public List<AmortizacionCreditoDto> TablaAmortizacion { get; set; } = new();
    }

    [DataContract]
    public class AmortizacionCreditoDto
    {
        [DataMember]
        public int NumeroCuota { get; set; }

        [DataMember]
        public decimal ValorCuota { get; set; }

        [DataMember]
        public decimal InteresPagado { get; set; }

        [DataMember]
        public decimal CapitalPagado { get; set; }

        [DataMember]
        public decimal SaldoPendiente { get; set; }
    }

    [DataContract]
    public class CreditoResumenDto
    {
        [DataMember] public int CreditoId { get; set; }
        [DataMember] public string CedulaCliente { get; set; } = string.Empty;
        [DataMember] public string NombreCliente { get; set; } = string.Empty;
        [DataMember] public decimal MontoAprobado { get; set; }
        [DataMember] public decimal SaldoPendiente { get; set; }
        [DataMember] public DateTime FechaAprobacion { get; set; }
        [DataMember] public DateTime FechaFinalizacion { get; set; }
    }
}
