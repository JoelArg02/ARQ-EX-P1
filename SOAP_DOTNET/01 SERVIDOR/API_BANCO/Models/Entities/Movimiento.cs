using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Runtime.Serialization;
using API_BANCO.Models.Enums;


namespace API_BANCO.Models.Entities
{
    [DataContract]
    public class Movimiento
    {
        [DataMember]
        [Key]
        public int Id { get; set; }

        [DataMember]
        [ForeignKey("Cuenta")]
        public int CuentaId { get; set; }

        [DataMember]
        [Required]
        public TipoMovimiento Tipo { get; set; }

        [DataMember]
        [Required]
        public decimal Monto { get; set; }

        [DataMember]
        [Required]
        public DateTime Fecha { get; set; } = DateTime.UtcNow;

        [DataMember]
        public Cuenta? Cuenta { get; set; }
    }
}
