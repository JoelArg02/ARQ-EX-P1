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

        modelBuilder.Entity<User>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Nombre).IsRequired().HasMaxLength(100);
            entity.Property(e => e.Contrasena).IsRequired().HasMaxLength(100);
            entity.HasData(new User { Id = 1, Nombre = "MONSTER", Contrasena = "MONSTER1" });
        });

        modelBuilder.Entity<ClienteBanco>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Cedula).IsRequired().HasMaxLength(10);
            entity.Property(e => e.NombreCompleto).IsRequired().HasMaxLength(150);
            entity.Property(e => e.EstadoCivil).HasConversion<string>().HasMaxLength(15);
            entity.Property(e => e.FechaNacimiento).IsRequired();

            entity.HasData(
                new ClienteBanco { Id = 1, Cedula = "0101010101", NombreCompleto = "Juan Pérez", EstadoCivil = EstadoCivil.Soltero, FechaNacimiento = new DateTime(1990, 5, 12), TieneCreditoActivo = false },
                new ClienteBanco { Id = 2, Cedula = "0202020202", NombreCompleto = "María García", EstadoCivil = EstadoCivil.Casado, FechaNacimiento = new DateTime(1985, 11, 3), TieneCreditoActivo = false },
                new ClienteBanco { Id = 3, Cedula = "0303030303", NombreCompleto = "Carlos López", EstadoCivil = EstadoCivil.Soltero, FechaNacimiento = new DateTime(1995, 2, 20), TieneCreditoActivo = false },
                new ClienteBanco { Id = 4, Cedula = "0404040404", NombreCompleto = "Pedro Menor", EstadoCivil = EstadoCivil.Casado, FechaNacimiento = new DateTime(2003, 8, 15), TieneCreditoActivo = false },
                new ClienteBanco { Id = 5, Cedula = "0505050505", NombreCompleto = "Ana Torres", EstadoCivil = EstadoCivil.Soltero, FechaNacimiento = new DateTime(1988, 1, 5), TieneCreditoActivo = true }
            );
        });

        modelBuilder.Entity<Cuenta>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.NumeroCuenta).IsRequired().HasMaxLength(20);
            entity.Property(e => e.Saldo).HasPrecision(12, 2);
            entity.Property(e => e.TipoCuenta).IsRequired().HasMaxLength(20);

            entity.HasOne(e => e.ClienteBanco).WithMany().HasForeignKey(e => e.ClienteBancoId).OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Cuenta>().HasData(
                new Cuenta { Id = 1, ClienteBancoId = 1, NumeroCuenta = "1234567890", Saldo = 1800.00m, TipoCuenta = TipoCuenta.Ahorros },
                new Cuenta { Id = 2, ClienteBancoId = 2, NumeroCuenta = "9876543210", Saldo = 2400.00m, TipoCuenta = TipoCuenta.Ahorros },
                new Cuenta { Id = 3, ClienteBancoId = 3, NumeroCuenta = "5556667771", Saldo = 900.00m, TipoCuenta = TipoCuenta.Corriente },
                new Cuenta { Id = 4, ClienteBancoId = 4, NumeroCuenta = "5556667772", Saldo = 3200.00m, TipoCuenta = TipoCuenta.Ahorros },
                new Cuenta { Id = 5, ClienteBancoId = 5, NumeroCuenta = "5556667773", Saldo = 1500.00m, TipoCuenta = TipoCuenta.Ahorros }
            );
        });

        modelBuilder.Entity<Movimiento>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.Monto).HasPrecision(10, 2);
            entity.Property(e => e.Tipo).HasConversion<string>().HasMaxLength(10);

            entity.HasOne(e => e.Cuenta).WithMany().HasForeignKey(e => e.CuentaId).OnDelete(DeleteBehavior.Cascade);

            entity.HasData(
                new Movimiento { Id = 1, CuentaId = 1, Tipo = TipoMovimiento.Deposito, Monto = 500.00m, Fecha = DateTime.UtcNow.AddDays(-5) },
                new Movimiento { Id = 2, CuentaId = 1, Tipo = TipoMovimiento.Retiro, Monto = 120.00m, Fecha = DateTime.UtcNow.AddDays(-4) },
                new Movimiento { Id = 3, CuentaId = 1, Tipo = TipoMovimiento.Deposito, Monto = 350.00m, Fecha = DateTime.UtcNow.AddDays(-12) },
                new Movimiento { Id = 4, CuentaId = 1, Tipo = TipoMovimiento.Retiro, Monto = 90.00m, Fecha = DateTime.UtcNow.AddDays(-15) },
                new Movimiento { Id = 5, CuentaId = 1, Tipo = TipoMovimiento.Deposito, Monto = 420.00m, Fecha = DateTime.UtcNow.AddDays(-20) },
                new Movimiento { Id = 6, CuentaId = 1, Tipo = TipoMovimiento.Retiro, Monto = 60.00m, Fecha = DateTime.UtcNow.AddDays(-25) },
                new Movimiento { Id = 7, CuentaId = 1, Tipo = TipoMovimiento.Deposito, Monto = 250.00m, Fecha = DateTime.UtcNow.AddDays(-30) },
                new Movimiento { Id = 8, CuentaId = 1, Tipo = TipoMovimiento.Retiro, Monto = 130.00m, Fecha = DateTime.UtcNow.AddDays(-35) },
                new Movimiento { Id = 9, CuentaId = 1, Tipo = TipoMovimiento.Deposito, Monto = 600.00m, Fecha = DateTime.UtcNow.AddDays(-40) },
                new Movimiento { Id = 10, CuentaId = 1, Tipo = TipoMovimiento.Retiro, Monto = 110.00m, Fecha = DateTime.UtcNow.AddDays(-45) },

                new Movimiento { Id = 11, CuentaId = 2, Tipo = TipoMovimiento.Deposito, Monto = 1000.00m, Fecha = DateTime.UtcNow.AddDays(-3) },
                new Movimiento { Id = 12, CuentaId = 2, Tipo = TipoMovimiento.Retiro, Monto = 200.00m, Fecha = DateTime.UtcNow.AddDays(-6) },
                new Movimiento { Id = 13, CuentaId = 2, Tipo = TipoMovimiento.Deposito, Monto = 450.00m, Fecha = DateTime.UtcNow.AddDays(-9) },
                new Movimiento { Id = 14, CuentaId = 2, Tipo = TipoMovimiento.Retiro, Monto = 150.00m, Fecha = DateTime.UtcNow.AddDays(-14) },
                new Movimiento { Id = 15, CuentaId = 2, Tipo = TipoMovimiento.Deposito, Monto = 520.00m, Fecha = DateTime.UtcNow.AddDays(-18) },
                new Movimiento { Id = 16, CuentaId = 2, Tipo = TipoMovimiento.Retiro, Monto = 90.00m, Fecha = DateTime.UtcNow.AddDays(-22) },
                new Movimiento { Id = 17, CuentaId = 2, Tipo = TipoMovimiento.Deposito, Monto = 780.00m, Fecha = DateTime.UtcNow.AddDays(-26) },
                new Movimiento { Id = 18, CuentaId = 2, Tipo = TipoMovimiento.Retiro, Monto = 65.00m, Fecha = DateTime.UtcNow.AddDays(-29) },
                new Movimiento { Id = 19, CuentaId = 2, Tipo = TipoMovimiento.Deposito, Monto = 300.00m, Fecha = DateTime.UtcNow.AddDays(-33) },
                new Movimiento { Id = 20, CuentaId = 2, Tipo = TipoMovimiento.Retiro, Monto = 120.00m, Fecha = DateTime.UtcNow.AddDays(-38) },

                new Movimiento { Id = 21, CuentaId = 3, Tipo = TipoMovimiento.Deposito, Monto = 400.00m, Fecha = DateTime.UtcNow.AddMonths(-3) },
                new Movimiento { Id = 22, CuentaId = 3, Tipo = TipoMovimiento.Retiro, Monto = 50.00m, Fecha = DateTime.UtcNow.AddMonths(-2).AddDays(-20) },
                new Movimiento { Id = 23, CuentaId = 3, Tipo = TipoMovimiento.Deposito, Monto = 280.00m, Fecha = DateTime.UtcNow.AddDays(-40) },
                new Movimiento { Id = 24, CuentaId = 3, Tipo = TipoMovimiento.Retiro, Monto = 40.00m, Fecha = DateTime.UtcNow.AddDays(-32) },
                new Movimiento { Id = 25, CuentaId = 3, Tipo = TipoMovimiento.Deposito, Monto = 310.00m, Fecha = DateTime.UtcNow.AddDays(-25) },
                new Movimiento { Id = 26, CuentaId = 3, Tipo = TipoMovimiento.Retiro, Monto = 75.00m, Fecha = DateTime.UtcNow.AddDays(-21) },
                new Movimiento { Id = 27, CuentaId = 3, Tipo = TipoMovimiento.Deposito, Monto = 500.00m, Fecha = DateTime.UtcNow.AddDays(-16) },
                new Movimiento { Id = 28, CuentaId = 3, Tipo = TipoMovimiento.Retiro, Monto = 60.00m, Fecha = DateTime.UtcNow.AddDays(-11) },
                new Movimiento { Id = 29, CuentaId = 3, Tipo = TipoMovimiento.Deposito, Monto = 200.00m, Fecha = DateTime.UtcNow.AddDays(-6) },
                new Movimiento { Id = 30, CuentaId = 3, Tipo = TipoMovimiento.Retiro, Monto = 55.00m, Fecha = DateTime.UtcNow.AddDays(-2) },

                new Movimiento { Id = 31, CuentaId = 4, Tipo = TipoMovimiento.Deposito, Monto = 200.00m, Fecha = DateTime.UtcNow.AddDays(-10) },
                new Movimiento { Id = 32, CuentaId = 4, Tipo = TipoMovimiento.Retiro, Monto = 100.00m, Fecha = DateTime.UtcNow.AddDays(-9) },
                new Movimiento { Id = 33, CuentaId = 4, Tipo = TipoMovimiento.Deposito, Monto = 750.00m, Fecha = DateTime.UtcNow.AddDays(-20) },
                new Movimiento { Id = 34, CuentaId = 4, Tipo = TipoMovimiento.Retiro, Monto = 160.00m, Fecha = DateTime.UtcNow.AddDays(-23) },
                new Movimiento { Id = 35, CuentaId = 4, Tipo = TipoMovimiento.Deposito, Monto = 600.00m, Fecha = DateTime.UtcNow.AddDays(-27) },
                new Movimiento { Id = 36, CuentaId = 4, Tipo = TipoMovimiento.Retiro, Monto = 95.00m, Fecha = DateTime.UtcNow.AddDays(-30) },
                new Movimiento { Id = 37, CuentaId = 4, Tipo = TipoMovimiento.Deposito, Monto = 500.00m, Fecha = DateTime.UtcNow.AddDays(-33) },
                new Movimiento { Id = 38, CuentaId = 4, Tipo = TipoMovimiento.Retiro, Monto = 140.00m, Fecha = DateTime.UtcNow.AddDays(-36) },
                new Movimiento { Id = 39, CuentaId = 4, Tipo = TipoMovimiento.Deposito, Monto = 820.00m, Fecha = DateTime.UtcNow.AddDays(-42) },
                new Movimiento { Id = 40, CuentaId = 4, Tipo = TipoMovimiento.Retiro, Monto = 110.00m, Fecha = DateTime.UtcNow.AddDays(-48) },

                new Movimiento { Id = 41, CuentaId = 5, Tipo = TipoMovimiento.Deposito, Monto = 700.00m, Fecha = DateTime.UtcNow.AddDays(-2) },
                new Movimiento { Id = 42, CuentaId = 5, Tipo = TipoMovimiento.Retiro, Monto = 130.00m, Fecha = DateTime.UtcNow.AddDays(-4) },
                new Movimiento { Id = 43, CuentaId = 5, Tipo = TipoMovimiento.Deposito, Monto = 450.00m, Fecha = DateTime.UtcNow.AddDays(-8) },
                new Movimiento { Id = 44, CuentaId = 5, Tipo = TipoMovimiento.Retiro, Monto = 90.00m, Fecha = DateTime.UtcNow.AddDays(-11) },
                new Movimiento { Id = 45, CuentaId = 5, Tipo = TipoMovimiento.Deposito, Monto = 300.00m, Fecha = DateTime.UtcNow.AddDays(-15) },
                new Movimiento { Id = 46, CuentaId = 5, Tipo = TipoMovimiento.Retiro, Monto = 70.00m, Fecha = DateTime.UtcNow.AddDays(-20) },
                new Movimiento { Id = 47, CuentaId = 5, Tipo = TipoMovimiento.Deposito, Monto = 600.00m, Fecha = DateTime.UtcNow.AddDays(-25) },
                new Movimiento { Id = 48, CuentaId = 5, Tipo = TipoMovimiento.Retiro, Monto = 80.00m, Fecha = DateTime.UtcNow.AddDays(-30) },
                new Movimiento { Id = 49, CuentaId = 5, Tipo = TipoMovimiento.Deposito, Monto = 520.00m, Fecha = DateTime.UtcNow.AddDays(-34) },
                new Movimiento { Id = 50, CuentaId = 5, Tipo = TipoMovimiento.Retiro, Monto = 95.00m, Fecha = DateTime.UtcNow.AddDays(-38) }
            );
        });

        modelBuilder.Entity<CreditoBanco>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.MontoAprobado).HasPrecision(12, 2);
            entity.Property(e => e.TasaInteres).HasPrecision(5, 4);
            entity.Property(e => e.NumeroCuotas).IsRequired();

            entity.HasOne(e => e.ClienteBanco).WithMany().HasForeignKey(e => e.ClienteBancoId).OnDelete(DeleteBehavior.Restrict);

            entity.HasData(
                new CreditoBanco
                {
                    Id = 1,
                    ClienteBancoId = 5,
                    MontoAprobado = 3000.00m,
                    TasaInteres = 0.12m,
                    NumeroCuotas = 12,
                    FechaAprobacion = DateTime.UtcNow.AddMonths(-1),
                    Activo = true
                }
            );
        });

        modelBuilder.Entity<AmortizacionCredito>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.ValorCuota).HasPrecision(10, 2);
            entity.Property(e => e.InteresPagado).HasPrecision(10, 2);
            entity.Property(e => e.CapitalPagado).HasPrecision(10, 2);
            entity.Property(e => e.SaldoPendiente).HasPrecision(10, 2);

            entity.HasOne(e => e.CreditoBanco).WithMany(c => c.Amortizaciones).HasForeignKey(e => e.CreditoBancoId).OnDelete(DeleteBehavior.Cascade);
        });
    }
}
