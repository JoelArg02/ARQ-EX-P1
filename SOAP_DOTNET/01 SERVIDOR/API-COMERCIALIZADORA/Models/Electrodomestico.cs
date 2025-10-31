using System.ComponentModel.DataAnnotations;
using System.Runtime.Serialization;

namespace API_Comercializadora.Models;

[DataContract]
public class Electrodomestico
{
    [DataMember]
    [Key]
    public int Id { get; set; }

    [DataMember]
    [Required]
    [MaxLength(100)]
    public string Nombre { get; set; } = string.Empty;

    [DataMember]
    [MaxLength(250)]
    public string? Descripcion { get; set; }

    [DataMember]
    [MaxLength(100)]
    public string? Marca { get; set; }

    [DataMember]
    [Range(0, double.MaxValue)]
    public decimal PrecioVenta { get; set; }

    [DataMember]
    public bool Activo { get; set; } = true;

    [DataMember]
    public DateTime FechaRegistro { get; set; } = DateTime.UtcNow;
}
