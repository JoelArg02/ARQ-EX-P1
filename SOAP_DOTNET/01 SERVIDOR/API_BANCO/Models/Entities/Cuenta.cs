using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Runtime.Serialization;
using API_BANCO.Models.Enums;

namespace API_BANCO.Models.Entities;

[DataContract]
public class Cuenta
{
    [DataMember]
    [Key]
    public int Id { get; set; }

    [DataMember]
    [ForeignKey("ClienteBanco")]
    public int ClienteBancoId { get; set; }

    [DataMember]
    [Required]
    [MaxLength(20)]
    public string NumeroCuenta { get; set; } = string.Empty;

    [DataMember]
    [Required]
    public decimal Saldo { get; set; }

    [DataMember]
    [Required]
    public TipoCuenta TipoCuenta { get; set; } = TipoCuenta.Ahorros;

    [DataMember]
    public ClienteBanco? ClienteBanco { get; set; }
}
