using API_Comercializadora.Models;

namespace API_Comercializadora.Application.Interface;

public interface IUserService
{
    Task<List<User>> GetAllUsers();
    Task<User?> GetUserById(int id);
    Task<User> CreateUser(string nombre);
    Task<User?> UpdateUser(int id, string nombre);
    Task<bool> DeleteUser(int id);
}
