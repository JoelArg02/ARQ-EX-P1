using System.Runtime.Serialization;

namespace API_BANCO.Application.DTOs.Movimientos
{
    [DataContract]
    public class MovimientoFiltroDto
    {
        [DataMember]
        public string Cedula { get; set; } = string.Empty;

        [DataMember]
        public DateTime? FechaInicio { get; set; }

        [DataMember]
        public DateTime? FechaFin { get; set; }
    }
}
