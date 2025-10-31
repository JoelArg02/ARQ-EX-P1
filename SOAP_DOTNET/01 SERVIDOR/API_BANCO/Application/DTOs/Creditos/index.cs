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
}
