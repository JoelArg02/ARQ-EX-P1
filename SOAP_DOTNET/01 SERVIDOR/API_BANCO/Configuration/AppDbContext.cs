using API_BANCO.Models.Entities;
using API_BANCO.Models.Enums;
using Microsoft.EntityFrameworkCore;

namespace API_BANCO.Configuration;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options)
        : base(options) { }

    public DbSet<User> Users { get; set; }
    public DbSet<ClienteBanco> ClientesBanco { get; set; }
    public DbSet<Cuenta> Cuentas { get; set; }
    public DbSet<Movimiento> Movimientos { get; set; }
    public DbSet<CreditoBanco> CreditosBanco { get; set; }
    public DbSet<AmortizacionCredito> AmortizacionesCredito { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        // USER
        modelBuilder.Entity<User>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Nombre)
                  .IsRequired()
                  .HasMaxLength(100);
            entity.Property(e => e.Contrasena)
                  .IsRequired()
                  .HasMaxLength(100);

            entity.HasData(
                new User
                {
                    Id = 1,
                    Nombre = "MONSTER",
                    Contrasena = "MONSTER1"
                }
            );
        });

        // CLIENTE BANCO
        modelBuilder.Entity<ClienteBanco>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Cedula)
                  .IsRequired()
                  .HasMaxLength(10);
            entity.Property(e => e.NombreCompleto)
                  .IsRequired()
                  .HasMaxLength(150);
            entity.Property(e => e.EstadoCivil)
                  .HasConversion<string>()
                  .HasMaxLength(15);
            entity.Property(e => e.FechaNacimiento)
                  .IsRequired();

            entity.HasData(
                new ClienteBanco
                {
                    Id = 1,
                    Cedula = "0101010101",
                    NombreCompleto = "Juan Pérez",
                    EstadoCivil = EstadoCivil.Soltero,
                    FechaNacimiento = new DateTime(1990, 5, 12),
                    TieneCreditoActivo = false
                },
                new ClienteBanco
                {
                    Id = 2,
                    Cedula = "0202020202",
                    NombreCompleto = "María García",
                    EstadoCivil = EstadoCivil.Casado,
                    FechaNacimiento = new DateTime(1985, 11, 3),
                    TieneCreditoActivo = false
                }
            );
        });

        // CUENTA
        modelBuilder.Entity<Cuenta>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.NumeroCuenta)
                  .IsRequired()
                  .HasMaxLength(20);
            entity.Property(e => e.Saldo)
                  .HasPrecision(12, 2);
            entity.Property(e => e.TipoCuenta)
                  .IsRequired()
                  .HasMaxLength(20);

            entity.HasOne(e => e.ClienteBanco)
                  .WithMany()
                  .HasForeignKey(e => e.ClienteBancoId)
                  .OnDelete(DeleteBehavior.Cascade);

            entity.HasData(
                new Cuenta
                {
                    Id = 1,
                    ClienteBancoId = 1,
                    NumeroCuenta = "1234567890",
                    Saldo = 1800.00m,
                    TipoCuenta = TipoCuenta.Ahorros
                },
                new Cuenta
                {
                    Id = 2,
                    ClienteBancoId = 2,
                    NumeroCuenta = "9876543210",
                    Saldo = 2400.00m,
                    TipoCuenta = TipoCuenta.Ahorros
                }
            );
        });

        // MOVIMIENTO
        modelBuilder.Entity<Movimiento>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Monto)
                  .HasPrecision(10, 2);
            entity.Property(e => e.Tipo)
                  .HasConversion<string>()
                  .HasMaxLength(10);

            entity.HasOne(e => e.Cuenta)
                  .WithMany()
                  .HasForeignKey(e => e.CuentaId)
                  .OnDelete(DeleteBehavior.Cascade);

            entity.HasData(
                new Movimiento
                {
                    Id = 1,
                    CuentaId = 1,
                    Tipo = TipoMovimiento.Deposito,
                    Monto = 700.00m,
                    Fecha = DateTime.UtcNow.AddDays(-25)
                },
                new Movimiento
                {
                    Id = 2,
                    CuentaId = 1,
                    Tipo = TipoMovimiento.Deposito,
                    Monto = 300.00m,
                    Fecha = DateTime.UtcNow.AddDays(-10)
                },
                new Movimiento
                {
                    Id = 3,
                    CuentaId = 2,
                    Tipo = TipoMovimiento.Deposito,
                    Monto = 1200.00m,
                    Fecha = DateTime.UtcNow.AddDays(-15)
                },
                new Movimiento
                {
                    Id = 4,
                    CuentaId = 2,
                    Tipo = TipoMovimiento.Retiro,
                    Monto = 400.00m,
                    Fecha = DateTime.UtcNow.AddDays(-5)
                }
            );
        });

        // CREDITO BANCO
        modelBuilder.Entity<CreditoBanco>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.MontoAprobado).HasPrecision(12, 2);
            entity.Property(e => e.TasaInteres).HasPrecision(5, 4);
            entity.Property(e => e.NumeroCuotas).IsRequired();

            entity.HasOne(e => e.ClienteBanco)
                  .WithMany()
                  .HasForeignKey(e => e.ClienteBancoId)
                  .OnDelete(DeleteBehavior.Restrict);
        });

        // AMORTIZACIÓN CREDITO
        modelBuilder.Entity<AmortizacionCredito>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.ValorCuota).HasPrecision(10, 2);
            entity.Property(e => e.InteresPagado).HasPrecision(10, 2);
            entity.Property(e => e.CapitalPagado).HasPrecision(10, 2);
            entity.Property(e => e.SaldoPendiente).HasPrecision(10, 2);

            entity.HasOne(e => e.CreditoBanco)
                  .WithMany(c => c.Amortizaciones)
                  .HasForeignKey(e => e.CreditoBancoId)
                  .OnDelete(DeleteBehavior.Cascade);
        });
    }
}
