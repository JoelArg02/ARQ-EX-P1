using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Runtime.Serialization;

namespace API_Comercializadora.Models;

[DataContract]
public class Factura
{
    [DataMember]
    [Key]
    public int Id { get; set; }

    [DataMember]
    [Required]
    [MaxLength(20)]
    public string NumeroFactura { get; set; } = string.Empty;

    [DataMember]
    [Required]
    public DateTime FechaEmision { get; set; } = DateTime.UtcNow;

    [DataMember]
    [ForeignKey("Cliente")]
    public int ClienteId { get; set; }

    [DataMember]
    [ForeignKey("FormaPago")]
    public int FormaPagoId { get; set; }

    [DataMember]
    [Range(0, double.MaxValue)]
    public decimal Subtotal { get; set; }

    [DataMember]
    [Range(0, double.MaxValue)]
    public decimal Descuento { get; set; }

    [DataMember]
    [Range(0, double.MaxValue)]
    public decimal TotalFinal { get; set; }

    [DataMember]
    public Cliente? Cliente { get; set; }

    [DataMember]
    public FormaPago? FormaPago { get; set; }

    [DataMember]
    public ICollection<DetalleFactura>? Detalles { get; set; }
}
