using System.ComponentModel.DataAnnotations;
using System.Runtime.Serialization;

namespace API_Comercializadora.Models;

[DataContract]
public class FormaPago
{
    [DataMember]
    [Key]
    public int Id { get; set; }

    [DataMember]
    [Required]
    [MaxLength(50)]
    public string Nombre { get; set; } = string.Empty;

    [DataMember]
    [MaxLength(150)]
    public string? Descripcion { get; set; }
}
