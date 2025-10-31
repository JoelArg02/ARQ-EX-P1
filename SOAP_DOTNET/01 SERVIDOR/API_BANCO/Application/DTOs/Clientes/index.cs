using API_BANCO.Models.Enums;
using System.ComponentModel.DataAnnotations;
using System.Runtime.Serialization;

namespace API_BANCO.Application.DTOs.Clientes
{

    [DataContract]
    public class ClienteBancoCreateDto
    {
        [DataMember]
        [Required]
        [MaxLength(10)]
        public string Cedula { get; set; } = string.Empty;

        [DataMember]
        [Required]
        [MaxLength(150)]
        public string NombreCompleto { get; set; } = string.Empty;

        [DataMember]
        [Required]
        public EstadoCivil EstadoCivil { get; set; }

        [DataMember]
        [Required]
        public DateTime FechaNacimiento { get; set; }
    }


    [DataContract]
    public class VerificacionClienteResponseDto
    {
        [DataMember]
        public bool EsCliente { get; set; }

        [DataMember]
        public bool TieneDepositoUltimoMes { get; set; }

        [DataMember]
        public bool CumpleEdadEstadoCivil { get; set; }

        [DataMember]
        public bool NoTieneCreditoActivo { get; set; }

        [DataMember]
        public bool EsElegible { get; set; }

        [DataMember]
        public string Mensaje { get; set; } = string.Empty;
    }
}
