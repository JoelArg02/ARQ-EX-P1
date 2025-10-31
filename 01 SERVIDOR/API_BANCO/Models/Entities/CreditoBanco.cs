using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Runtime.Serialization;

namespace API_BANCO.Models.Entities;

[DataContract]
public class CreditoBanco
{
    [DataMember]
    [Key]
    public int Id { get; set; }

    [DataMember]
    [ForeignKey("ClienteBanco")]
    public int ClienteBancoId { get; set; }

    [DataMember]
    [Required]
    public decimal MontoAprobado { get; set; }

    [DataMember]
    [Required]
    public int NumeroCuotas { get; set; }

    [DataMember]
    [Required]
    public decimal TasaInteres { get; set; } = 0.16m;

    [DataMember]
    [Required]
    public DateTime FechaAprobacion { get; set; } = DateTime.UtcNow;

    [DataMember]
    public bool Activo { get; set; } = true;

    [DataMember]
    public ClienteBanco? ClienteBanco { get; set; }

    [DataMember]
    public ICollection<AmortizacionCredito>? Amortizaciones { get; set; }
}
