using System.ComponentModel.DataAnnotations;
using System.Runtime.Serialization;

namespace API_Comercializadora.Models;

[DataContract]
public class User
{
    [DataMember]
    [Key]
    public int Id { get; set; }

    [DataMember]
    [Required]
    [MaxLength(100)]
    public string Nombre { get; set; } = string.Empty;

    [DataMember]
    [Required]
    [MaxLength(100)]
    public string Contrasena { get; set; } = string.Empty;
}
