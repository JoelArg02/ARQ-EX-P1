using API_BANCO.Models.Entities;

namespace API_BANCO.Application.Interface;

public interface IUserService
{
    Task<List<User>> GetAllUsers();
    Task<User?> GetUserById(int id);
    Task<User?> Login(string nombre, string contrasena);
    Task<User> CreateUser(string nombre);
    Task<User?> UpdateUser(int id, string nombre);
    Task<bool> DeleteUser(int id);
}
