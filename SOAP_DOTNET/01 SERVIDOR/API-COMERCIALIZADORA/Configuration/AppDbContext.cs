using API_Comercializadora.Models;
using Microsoft.EntityFrameworkCore;

namespace API_Comercializadora.Configuration;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options)
        : base(options) { }

    public DbSet<Cliente> Clientes { get; set; }
    public DbSet<User> Users { get; set; }
    public DbSet<Electrodomestico> Electrodomesticos { get; set; }
    public DbSet<FormaPago> FormasPago { get; set; }
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
        });

        modelBuilder.Entity<Electrodomestico>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Nombre).IsRequired().HasMaxLength(100);
            entity.Property(e => e.Descripcion).HasMaxLength(250);
            entity.Property(e => e.Marca).HasMaxLength(100);
            entity.Property(e => e.PrecioVenta).HasPrecision(10, 2);
        });

        modelBuilder.Entity<FormaPago>(entity =>
{
    entity.HasKey(e => e.Id);
    entity.Property(e => e.Nombre).IsRequired().HasMaxLength(50);
    entity.Property(e => e.Descripcion).HasMaxLength(150);

    entity.HasData(
        new FormaPago
        {
            Id = 1,
            Nombre = "Efectivo",
            Descripcion = "Pago inmediato con descuento del 33% sobre el precio de venta."
        },
        new FormaPago
        {
            Id = 2,
            Nombre = "Crédito Directo",
            Descripcion = "Compra financiada validada mediante el servicio web del Banco BanQuito."
        }
    );
});


        modelBuilder.Entity<Factura>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.NumeroFactura).IsRequired().HasMaxLength(20);
            entity.Property(e => e.Subtotal).HasPrecision(10, 2);
            entity.Property(e => e.Descuento).HasPrecision(10, 2);
            entity.Property(e => e.TotalFinal).HasPrecision(10, 2);

            entity
                .HasOne(e => e.Cliente)
                .WithMany()
                .HasForeignKey(e => e.ClienteId)
                .OnDelete(DeleteBehavior.Restrict);

            entity
                .HasOne(e => e.FormaPago)
                .WithMany()
                .HasForeignKey(e => e.FormaPagoId)
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
