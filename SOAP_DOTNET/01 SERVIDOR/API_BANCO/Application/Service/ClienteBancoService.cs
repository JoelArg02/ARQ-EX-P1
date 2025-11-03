using API_BANCO.Application.DTOs.Clientes;
using API_BANCO.Application.DTOs.Creditos;
using API_BANCO.Application.Interface;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Models.Enums;
using API_BANCO.Repositories;
using API_BANCO.Repositories.Interfaces;
using Microsoft.EntityFrameworkCore;
using System;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Linq;

namespace API_BANCO.Application.Service;

public class ClienteBancoService : IClienteBancoService
{
    private readonly AppDbContext _context;
    private readonly IClienteBancoRepository _repository;

    public ClienteBancoService(AppDbContext context)
    {
        _context = context;
        _repository = new ClienteBancoRepository(_context);
    }

    public async Task<List<ClienteBanco>> GetAllClientesBanco()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<ClienteBanco?> GetClienteBancoById(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<ClienteBanco?> GetClienteBancoByCedula(string cedula)
    {
        return await _repository.GetByCedulaAsync(cedula);
    }


    public async Task<ClienteBanco> CreateClienteBanco(ClienteBancoCreateDto dto)
    {
        if (string.IsNullOrWhiteSpace(dto.Cedula))
            throw new ArgumentException("La cédula no puede estar vacía.");

        if (string.IsNullOrWhiteSpace(dto.NombreCompleto))
            throw new ArgumentException("El nombre no puede estar vacío.");

        if (dto.FechaNacimiento == default)
            throw new ArgumentException("Debe especificar la fecha de nacimiento.");

        var clienteBanco = new ClienteBanco
        {
            Cedula = dto.Cedula.Trim(),
            NombreCompleto = dto.NombreCompleto.Trim(),
            EstadoCivil = dto.EstadoCivil, // se guarda tal como llega del cliente
            FechaNacimiento = dto.FechaNacimiento,
            TieneCreditoActivo = false
        };

        var created = await _repository.CreateAsync(clienteBanco);
        return created;
    }


    public async Task<ClienteBanco?> UpdateClienteBanco(int id, string cedula, string nombreCompleto, int estadoCivil, DateTime fechaNacimiento, bool tieneCreditoActivo)
    {
        var clienteBanco = new ClienteBanco
        {
            Id = id,
            Cedula = cedula,
            NombreCompleto = nombreCompleto,
            EstadoCivil = (EstadoCivil)estadoCivil,
            FechaNacimiento = fechaNacimiento,
            TieneCreditoActivo = tieneCreditoActivo
        };
        return await _repository.UpdateAsync(clienteBanco);
    }

    public async Task<bool> DeleteClienteBanco(int id)
    {
        return await _repository.DeleteAsync(id);
    }

    public async Task<bool> VerificarClientePorCedula(string cedula)
    {
        return await _repository.ExistePorCedulaAsync(cedula);
    }
    public async Task<VerificacionClienteResponseDto> VerificarElegibilidadCliente(string cedula)
    {
        return await _repository.VerificarElegibilidadAsync(cedula);
    }

    public async Task<CalculoCreditoResponseDto> CalcularMontoMaximoCredito(string cedula)
    {
        return await _repository.CalcularMontoMaximoCreditoAsync(cedula);
    }

    public async Task<EvaluacionCreditoResultadoDto> EvaluarCredito(string cedula, decimal montoRequerido)
    {
        if (montoRequerido <= 0)
        {
            return new EvaluacionCreditoResultadoDto
            {
                Aprobado = false,
                Detalle = "El monto solicitado debe ser mayor a 0."
            };
        }

        return await _repository.EvaluarCreditoAsync(cedula, montoRequerido);
    }
    public Task<AprobacionCreditoResponseDto> AprobarCredito(AprobacionCreditoRequestDto dto) => _repository.AprobarCreditoAsync(dto.CedulaCliente, dto.MontoSolicitado, dto.NumeroCuotas);
    public async Task<CreditoResumenDto?> ObtenerCreditoPorCedula(string cedula)
    {
        return await _repository.ObtenerCreditoPorCedulaAsync(cedula);
    }

    public async Task<List<AmortizacionCreditoDto>> ObtenerAmortizacionPorCreditoId(int creditoId)
    {
        return await _repository.ObtenerAmortizacionPorCreditoIdAsync(creditoId);
    }

    public async Task<List<AmortizacionCreditoDto>> GetAmortizacionesByCredito(int creditoId)
    {
        return await _repository.ObtenerAmortizacionPorCreditoIdAsync(creditoId);
    }

    public async Task<List<CreditoBanco>> GetCreditosByClienteId(int clienteId)
    {
        return await _context.CreditosBanco
            .Where(c => c.ClienteBancoId == clienteId)
            .OrderByDescending(c => c.FechaAprobacion)
            .ToListAsync();
    }

    public async Task<List<CreditoBanco>> GetAllCreditosBanco()
    {
        return await _context.CreditosBanco
            .OrderByDescending(c => c.FechaAprobacion)
            .ToListAsync();
    }

}
