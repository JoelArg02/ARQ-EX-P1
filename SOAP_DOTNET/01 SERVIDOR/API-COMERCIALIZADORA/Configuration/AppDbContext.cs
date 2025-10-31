using API_Comercializadora.Models;
using API_Comercializadora.Models.Enums;
using Microsoft.EntityFrameworkCore;

namespace API_Comercializadora.Configuration;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options)
        : base(options) { }

    public DbSet<Cliente> Clientes { get; set; }
    public DbSet<User> Users { get; set; }
    public DbSet<Electrodomestico> Electrodomesticos { get; set; }
    public DbSet<Factura> Facturas { get; set; }
    public DbSet<DetalleFactura> DetallesFactura { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<Cliente>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Cedula).IsRequired().HasMaxLength(10);
            entity.Property(e => e.NombreCompleto).IsRequired().HasMaxLength(150);
            entity.Property(e => e.Correo).HasMaxLength(100);
            entity.Property(e => e.Telefono).HasMaxLength(15);
            entity.Property(e => e.Direccion).HasMaxLength(200);

            // Datos iniciales: clientes del banco + clientes solo de comercializadora
            entity.HasData(
                // Clientes que están registrados en el banco (0101010101 a 0505050505)
                new Cliente { Id = 1, Cedula = "0101010101", NombreCompleto = "Juan Pérez", Correo = "juan.perez@email.com", Telefono = "0987654321", Direccion = "Quito, Ecuador", FechaRegistro = DateTime.UtcNow.AddMonths(-6) },
                new Cliente { Id = 2, Cedula = "0202020202", NombreCompleto = "María García", Correo = "maria.garcia@email.com", Telefono = "0987654322", Direccion = "Guayaquil, Ecuador", FechaRegistro = DateTime.UtcNow.AddMonths(-5) },
                new Cliente { Id = 3, Cedula = "0303030303", NombreCompleto = "Carlos López", Correo = "carlos.lopez@email.com", Telefono = "0987654323", Direccion = "Cuenca, Ecuador", FechaRegistro = DateTime.UtcNow.AddMonths(-4) },
                new Cliente { Id = 4, Cedula = "0404040404", NombreCompleto = "Pedro Menor", Correo = "pedro.menor@email.com", Telefono = "0987654324", Direccion = "Ambato, Ecuador", FechaRegistro = DateTime.UtcNow.AddMonths(-3) },
                new Cliente { Id = 5, Cedula = "0505050505", NombreCompleto = "Ana Torres", Correo = "ana.torres@email.com", Telefono = "0987654325", Direccion = "Machala, Ecuador", FechaRegistro = DateTime.UtcNow.AddMonths(-2) },
                
                // Clientes que solo están en la comercializadora (no tienen cuenta en el banco)
                new Cliente { Id = 6, Cedula = "0606060606", NombreCompleto = "Luis Fernández", Correo = "luis.fernandez@email.com", Telefono = "0987654326", Direccion = "Loja, Ecuador", FechaRegistro = DateTime.UtcNow.AddMonths(-1) },
                new Cliente { Id = 7, Cedula = "0707070707", NombreCompleto = "Sandra Morales", Correo = "sandra.morales@email.com", Telefono = "0987654327", Direccion = "Manta, Ecuador", FechaRegistro = DateTime.UtcNow.AddDays(-15) },
                new Cliente { Id = 8, Cedula = "0808080808", NombreCompleto = "Roberto Díaz", Correo = "roberto.diaz@email.com", Telefono = "0987654328", Direccion = "Riobamba, Ecuador", FechaRegistro = DateTime.UtcNow.AddDays(-10) }
            );
        });

        modelBuilder.Entity<Electrodomestico>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Nombre).IsRequired().HasMaxLength(100);
            entity.Property(e => e.Descripcion).HasMaxLength(250);
            entity.Property(e => e.Marca).HasMaxLength(100);
            entity.Property(e => e.PrecioVenta).HasPrecision(10, 2);

            // Datos iniciales: electrodomésticos disponibles
            entity.HasData(
                new Electrodomestico { Id = 1, Nombre = "Refrigeradora", Descripcion = "Refrigeradora de 18 pies cúbicos", Marca = "Samsung", PrecioVenta = 1200.00m, Activo = true, FechaRegistro = DateTime.UtcNow.AddMonths(-1) },
                new Electrodomestico { Id = 2, Nombre = "Lavadora", Descripcion = "Lavadora automática 15 kg", Marca = "LG", PrecioVenta = 800.00m, Activo = true, FechaRegistro = DateTime.UtcNow.AddMonths(-1) },
                new Electrodomestico { Id = 3, Nombre = "Microondas", Descripcion = "Microondas digital 1.5 pies cúbicos", Marca = "Panasonic", PrecioVenta = 250.00m, Activo = true, FechaRegistro = DateTime.UtcNow.AddMonths(-1) },
                new Electrodomestico { Id = 4, Nombre = "Televisor 50\"", Descripcion = "Televisor Smart TV 4K 50 pulgadas", Marca = "Sony", PrecioVenta = 950.00m, Activo = true, FechaRegistro = DateTime.UtcNow.AddMonths(-1) },
                new Electrodomestico { Id = 5, Nombre = "Cocina", Descripcion = "Cocina a gas 4 quemadores", Marca = "Indurama", PrecioVenta = 450.00m, Activo = true, FechaRegistro = DateTime.UtcNow.AddMonths(-1) },
                new Electrodomestico { Id = 6, Nombre = "Aire Acondicionado", Descripcion = "Aire acondicionado split 12000 BTU", Marca = "Carrier", PrecioVenta = 650.00m, Activo = true, FechaRegistro = DateTime.UtcNow.AddMonths(-1) },
                new Electrodomestico { Id = 7, Nombre = "Licuadora", Descripcion = "Licuadora de 10 velocidades", Marca = "Oster", PrecioVenta = 85.00m, Activo = true, FechaRegistro = DateTime.UtcNow.AddMonths(-1) },
                new Electrodomestico { Id = 8, Nombre = "Plancha", Descripcion = "Plancha de vapor", Marca = "Black & Decker", PrecioVenta = 45.00m, Activo = true, FechaRegistro = DateTime.UtcNow.AddMonths(-1) }
            );
        });

        modelBuilder.Entity<Factura>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.NumeroFactura).IsRequired().HasMaxLength(50);
            entity.Property(e => e.FormaPago).HasConversion<int>(); // Guardar el enum como int
            entity.Property(e => e.Subtotal).HasPrecision(10, 2);
            entity.Property(e => e.Descuento).HasPrecision(10, 2);
            entity.Property(e => e.TotalFinal).HasPrecision(10, 2);

            entity
                .HasOne(e => e.Cliente)
                .WithMany()
                .HasForeignKey(e => e.ClienteId)
                .OnDelete(DeleteBehavior.Restrict);
        });

        modelBuilder.Entity<DetalleFactura>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.PrecioUnitario).HasPrecision(10, 2);
            entity.Property(e => e.Subtotal).HasPrecision(10, 2);

            entity
                .HasOne(e => e.Factura)
                .WithMany(f => f.Detalles)
                .HasForeignKey(e => e.FacturaId)
                .OnDelete(DeleteBehavior.Cascade);

            entity
                .HasOne(e => e.Electrodomestico)
                .WithMany()
                .HasForeignKey(e => e.ElectrodomesticoId)
                .OnDelete(DeleteBehavior.Restrict);
        });

        modelBuilder.Entity<User>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Nombre).IsRequired().HasMaxLength(100);

            entity.Property(e => e.Contrasena).IsRequired().HasMaxLength(100);

            entity.HasData(
                new User
                {
                    Id = 123,
                    Nombre = "MONSTER",
                    Contrasena = "MONSTER9",
                }
            );
        });
    }
}
