using System.ComponentModel.DataAnnotations;
using System.Runtime.Serialization;

namespace API_Comercializadora.Models;

[DataContract]
public class Cliente
{
    [DataMember]
    [Key]
    public int Id { get; set; }

    [DataMember]
    [Required]
    [MaxLength(10)]
    public string Cedula { get; set; } = string.Empty;

    [DataMember]
    [Required]
    [MaxLength(150)]
    public string NombreCompleto { get; set; } = string.Empty;

    [DataMember]
    [EmailAddress]
    [MaxLength(100)]
    public string? Correo { get; set; }

    [DataMember]
    [MaxLength(15)]
    public string? Telefono { get; set; }

    [DataMember]
    [MaxLength(200)]
    public string? Direccion { get; set; }

    [DataMember]
    public DateTime FechaRegistro { get; set; } = DateTime.UtcNow;
}
