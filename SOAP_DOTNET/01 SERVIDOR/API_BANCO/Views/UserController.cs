using CoreWCF;
using Microsoft.EntityFrameworkCore;
using API_BANCO.Application.Interface;
using API_BANCO.Application.Service;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;

namespace API_BANCO.Views;

[ServiceContract]
public interface IUserController
{
    [OperationContract]
    Task<List<User>> GetAllUsers();

    [OperationContract]
    Task<User?> GetUserById(int id);

    [OperationContract]
    Task<User?> Login(string nombre, string contrasena);

    [OperationContract]
    Task<User> CreateUser(string nombre);

    [OperationContract]
    Task<User?> UpdateUser(int id, string nombre);

    [OperationContract]
    Task<bool> DeleteUser(int id);
}

public class UserController : IUserController
{
    private readonly IUserService _userService;

    public UserController(AppDbContext context)
    {
        _userService = new UserService(context);
    }

    public async Task<List<User>> GetAllUsers()
    {
        return await _userService.GetAllUsers();
    }

    public async Task<User?> GetUserById(int id)
    {
        return await _userService.GetUserById(id);
    }

    public async Task<User?> Login(string nombre, string contrasena)
    {
        return await _userService.Login(nombre, contrasena);
    }

    public async Task<User> CreateUser(string nombre)
    {
        return await _userService.CreateUser(nombre);
    }

    public async Task<User?> UpdateUser(int id, string nombre)
    {
        return await _userService.UpdateUser(id, nombre);
    }

    public async Task<bool> DeleteUser(int id)
    {
        return await _userService.DeleteUser(id);
    }
}
