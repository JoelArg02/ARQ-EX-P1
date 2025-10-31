using System.ComponentModel.DataAnnotations;
using System.Runtime.Serialization;
using API_BANCO.Models.Enums;

namespace API_BANCO.Models.Entities;

[DataContract]
public class ClienteBanco
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
    [Required]
    public EstadoCivil EstadoCivil { get; set; }

    [DataMember]
    [Required]
    public DateTime FechaNacimiento { get; set; }

    [DataMember]
    public bool TieneCreditoActivo { get; set; } = false;
}
