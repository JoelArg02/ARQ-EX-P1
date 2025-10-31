using API_BANCO.Models.Entities;

namespace API_BANCO.Application.Interface;

public interface IClienteBancoService
{
    Task<List<ClienteBanco>> GetAllClientesBanco();
    Task<ClienteBanco?> GetClienteBancoById(int id);
    Task<ClienteBanco?> GetClienteBancoByCedula(string cedula);
    Task<ClienteBanco> CreateClienteBanco(string cedula, string nombreCompleto, int estadoCivil, DateTime fechaNacimiento);
    Task<ClienteBanco?> UpdateClienteBanco(int id, string cedula, string nombreCompleto, int estadoCivil, DateTime fechaNacimiento, bool tieneCreditoActivo);
    Task<bool> DeleteClienteBanco(int id);
}
