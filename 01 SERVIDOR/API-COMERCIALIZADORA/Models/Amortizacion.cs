using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Runtime.Serialization;

namespace API_Comercializadora.Models;

[DataContract]
public class Amortizacion
{
    [DataMember]
    [Key]
    public int Id { get; set; }

    [DataMember]
    [ForeignKey("Credito")]
    public int CreditoId { get; set; }

    [DataMember]
    public int NumeroCuota { get; set; }

    [DataMember]
    public decimal ValorCuota { get; set; }

    [DataMember]
    public decimal InteresPagado { get; set; }

    [DataMember]
    public decimal CapitalPagado { get; set; }

    [DataMember]
    public decimal Saldo { get; set; }

    [DataMember]
    public Credito? Credito { get; set; }
}
