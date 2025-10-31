using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Runtime.Serialization;

namespace API_Comercializadora.Models;

[DataContract]
public class Credito
{
    [DataMember]
    [Key]
    public int Id { get; set; }

    [DataMember]
    [ForeignKey("Factura")]
    public int FacturaId { get; set; }

    [DataMember]
    [Required]
    [MaxLength(10)]
    public string CedulaCliente { get; set; } = string.Empty;

    [DataMember]
    [Range(0, double.MaxValue)]
    public decimal MontoAprobado { get; set; }

    [DataMember]
    [Range(3, 24)]
    public int NumeroCuotas { get; set; }

    [DataMember]
    [Range(0, 100)]
    public decimal TasaInteres { get; set; }

    [DataMember]
    [MaxLength(20)]
    public string Estado { get; set; } = "Pendiente";

    [DataMember]
    public DateTime FechaAprobacion { get; set; } = DateTime.UtcNow;

    [DataMember]
    public Factura? Factura { get; set; }

    [DataMember]
    public ICollection<Amortizacion>? Amortizaciones { get; set; }
}
