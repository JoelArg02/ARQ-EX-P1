using System.Runtime.Serialization;
using API_Comercializadora.Models.Enums;

namespace API_Comercializadora.Application.DTOs;

[DataContract]
public class CrearFacturaRequestDto
{
    [DataMember]
    public int ClienteId { get; set; } // ID del cliente en la comercializadora

    [DataMember]
    public FormaPagoEnum FormaPago { get; set; } // 0: Efectivo, 1: Crédito Directo

    [DataMember]
    public int? NumeroCuotas { get; set; } // Solo para crédito directo

    [DataMember]
    public List<DetalleFacturaDto> Detalles { get; set; } = new List<DetalleFacturaDto>();
}

[DataContract]
public class DetalleFacturaDto
{
    [DataMember]
    public int ElectrodomesticoId { get; set; }

    [DataMember]
    public int Cantidad { get; set; }
}

[DataContract]
public class FacturaResponseDto
{
    [DataMember]
    public bool Exitoso { get; set; }

    [DataMember]
    public string Mensaje { get; set; } = string.Empty;

    [DataMember]
    public int? FacturaId { get; set; }

    [DataMember]
    public string? NumeroFactura { get; set; }

    [DataMember]
    public decimal Subtotal { get; set; }

    [DataMember]
    public decimal Descuento { get; set; }

    [DataMember]
    public decimal TotalFinal { get; set; }

    [DataMember]
    public string? FormaPago { get; set; }

    [DataMember]
    public CreditoInfoDto? InformacionCredito { get; set; }
}

[DataContract]
public class CreditoInfoDto
{
    [DataMember]
    public bool CreditoAprobado { get; set; }

    [DataMember]
    public decimal MontoCredito { get; set; }

    [DataMember]
    public int NumeroCuotas { get; set; }

    [DataMember]
    public decimal ValorCuota { get; set; }

    [DataMember]
    public string MensajeBanco { get; set; } = string.Empty;
}

[DataContract]
public class VerificarElegibilidadRequestDto
{
    [DataMember]
    public string Cedula { get; set; } = string.Empty;
}

[DataContract]
public class VerificarElegibilidadResponseDto
{
    [DataMember]
    public bool EsElegible { get; set; }

    [DataMember]
    public string Mensaje { get; set; } = string.Empty;

    [DataMember]
    public decimal MontoMaximoCredito { get; set; }
}

[DataContract]
public class FacturaListDto
{
    [DataMember]
    public int Id { get; set; }

    [DataMember]
    public int ClienteId { get; set; }

    [DataMember]
    public string NombreCliente { get; set; } = string.Empty;

    [DataMember]
    public string CedulaCliente { get; set; } = string.Empty;

    [DataMember]
    public DateTime FechaEmision { get; set; }

    [DataMember]
    public decimal Subtotal { get; set; }

    [DataMember]
    public decimal Iva { get; set; }

    [DataMember]
    public decimal Total { get; set; }

    [DataMember]
    public string FormaPago { get; set; } = string.Empty; // "Efectivo" o "Crédito"

    [DataMember]
    public int CantidadProductos { get; set; }
}
