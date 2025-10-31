using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Runtime.Serialization;

namespace API_Comercializadora.Models;

[DataContract]
public class DetalleFactura
{
    [DataMember]
    [Key]
    public int Id { get; set; }

    [DataMember]
    [ForeignKey("Factura")]
    public int FacturaId { get; set; }

    [DataMember]
    [ForeignKey("Electrodomestico")]
    public int ElectrodomesticoId { get; set; }

    [DataMember]
    [Range(1, int.MaxValue)]
    public int Cantidad { get; set; }

    [DataMember]
    [Range(0, double.MaxValue)]
    public decimal PrecioUnitario { get; set; }

    [DataMember]
    [Range(0, double.MaxValue)]
    public decimal Subtotal { get; set; }

    [DataMember]
    public Factura? Factura { get; set; }

    [DataMember]
    public Electrodomestico? Electrodomestico { get; set; }
}
