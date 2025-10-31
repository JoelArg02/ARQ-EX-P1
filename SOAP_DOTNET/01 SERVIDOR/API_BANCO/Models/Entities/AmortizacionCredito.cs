using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Runtime.Serialization;

namespace API_BANCO.Models.Entities;

[DataContract]
public class AmortizacionCredito
{
    [DataMember]
    [Key]
    public int Id { get; set; }

    [DataMember]
    [ForeignKey("CreditoBanco")]
    public int CreditoBancoId { get; set; }

    [DataMember]
    public int NumeroCuota { get; set; }

    [DataMember]
    public decimal ValorCuota { get; set; }

    [DataMember]
    public decimal InteresPagado { get; set; }

    [DataMember]
    public decimal CapitalPagado { get; set; }

    [DataMember]
    public decimal SaldoPendiente { get; set; }

    [DataMember]
    public DateTime FechaPago { get; set; } = DateTime.UtcNow;

    [DataMember]
    public CreditoBanco? CreditoBanco { get; set; }
}
