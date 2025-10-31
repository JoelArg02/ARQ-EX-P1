using System.Runtime.Serialization;

namespace API_Comercializadora.Models.Enums;

[DataContract]
public enum FormaPagoEnum
{
    [EnumMember(Value = "0")]
    Efectivo = 0,

    [EnumMember(Value = "1")]
    CreditoDirecto = 1
}
