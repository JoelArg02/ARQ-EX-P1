using API_Comercializadora.Application.Interface;
using API_Comercializadora.Configuration;
using API_Comercializadora.Models;
using API_Comercializadora.Repositories;
using API_Comercializadora.Repositories.Interfaces;

namespace API_Comercializadora.Application.Service;

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
