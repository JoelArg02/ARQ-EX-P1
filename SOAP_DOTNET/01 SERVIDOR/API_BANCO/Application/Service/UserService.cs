using API_BANCO.Application.Interface;
using API_BANCO.Configuration;
using API_BANCO.Models.Entities;
using API_BANCO.Repositories;
using API_BANCO.Repositories.Interfaces;

namespace API_BANCO.Application.Service;

public class UserService : IUserService
{
    private readonly AppDbContext _context;
    private readonly IUserRepository _repository;

    public UserService(AppDbContext context)
    {
        _context = context;
        _repository = new UserRepository(_context);
    }

    public async Task<List<User>> GetAllUsers()
    {
        return await _repository.GetAllAsync();
    }

    public async Task<User?> GetUserById(int id)
    {
        return await _repository.GetByIdAsync(id);
    }

    public async Task<User?> Login(string nombre, string contrasena)
    {
        return await _repository.LoginAsync(nombre, contrasena);
    }

    public async Task<User> CreateUser(string nombre)
    {
        var user = new User { Nombre = nombre };
        return await _repository.CreateAsync(user);
    }

    public async Task<User?> UpdateUser(int id, string nombre)
    {
        var user = new User { Id = id, Nombre = nombre };
        return await _repository.UpdateAsync(user);
    }

    public async Task<bool> DeleteUser(int id)
    {
        return await _repository.DeleteAsync(id);
    }
}
