using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace API_BANCO.Migrations
{
    /// <inheritdoc />
    public partial class newmigration : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "ClientesBanco",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Cedula = table.Column<string>(type: "nvarchar(10)", maxLength: 10, nullable: false),
                    NombreCompleto = table.Column<string>(type: "nvarchar(150)", maxLength: 150, nullable: false),
                    EstadoCivil = table.Column<string>(type: "nvarchar(15)", maxLength: 15, nullable: false),
                    FechaNacimiento = table.Column<DateTime>(type: "datetime2", nullable: false),
                    TieneCreditoActivo = table.Column<bool>(type: "bit", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ClientesBanco", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Users",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Nombre = table.Column<string>(type: "nvarchar(100)", maxLength: 100, nullable: false),
                    Contrasena = table.Column<string>(type: "nvarchar(100)", maxLength: 100, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Users", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "CreditosBanco",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    ClienteBancoId = table.Column<int>(type: "int", nullable: false),
                    MontoAprobado = table.Column<decimal>(type: "decimal(12,2)", precision: 12, scale: 2, nullable: false),
                    NumeroCuotas = table.Column<int>(type: "int", nullable: false),
                    TasaInteres = table.Column<decimal>(type: "decimal(5,4)", precision: 5, scale: 4, nullable: false),
                    FechaAprobacion = table.Column<DateTime>(type: "datetime2", nullable: false),
                    Activo = table.Column<bool>(type: "bit", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_CreditosBanco", x => x.Id);
                    table.ForeignKey(
                        name: "FK_CreditosBanco_ClientesBanco_ClienteBancoId",
                        column: x => x.ClienteBancoId,
                        principalTable: "ClientesBanco",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.CreateTable(
                name: "Cuentas",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    ClienteBancoId = table.Column<int>(type: "int", nullable: false),
                    NumeroCuenta = table.Column<string>(type: "nvarchar(20)", maxLength: 20, nullable: false),
                    Saldo = table.Column<decimal>(type: "decimal(12,2)", precision: 12, scale: 2, nullable: false),
                    TipoCuenta = table.Column<int>(type: "int", maxLength: 20, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Cuentas", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Cuentas_ClientesBanco_ClienteBancoId",
                        column: x => x.ClienteBancoId,
                        principalTable: "ClientesBanco",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "AmortizacionesCredito",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    CreditoBancoId = table.Column<int>(type: "int", nullable: false),
                    NumeroCuota = table.Column<int>(type: "int", nullable: false),
                    ValorCuota = table.Column<decimal>(type: "decimal(10,2)", precision: 10, scale: 2, nullable: false),
                    InteresPagado = table.Column<decimal>(type: "decimal(10,2)", precision: 10, scale: 2, nullable: false),
                    CapitalPagado = table.Column<decimal>(type: "decimal(10,2)", precision: 10, scale: 2, nullable: false),
                    SaldoPendiente = table.Column<decimal>(type: "decimal(10,2)", precision: 10, scale: 2, nullable: false),
                    FechaPago = table.Column<DateTime>(type: "datetime2", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_AmortizacionesCredito", x => x.Id);
                    table.ForeignKey(
                        name: "FK_AmortizacionesCredito_CreditosBanco_CreditoBancoId",
                        column: x => x.CreditoBancoId,
                        principalTable: "CreditosBanco",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "Movimientos",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    CuentaId = table.Column<int>(type: "int", nullable: false),
                    Tipo = table.Column<string>(type: "nvarchar(10)", maxLength: 10, nullable: false),
                    Monto = table.Column<decimal>(type: "decimal(10,2)", precision: 10, scale: 2, nullable: false),
                    Fecha = table.Column<DateTime>(type: "datetime2", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Movimientos", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Movimientos_Cuentas_CuentaId",
                        column: x => x.CuentaId,
                        principalTable: "Cuentas",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.InsertData(
                table: "ClientesBanco",
                columns: new[] { "Id", "Cedula", "EstadoCivil", "FechaNacimiento", "NombreCompleto", "TieneCreditoActivo" },
                values: new object[,]
                {
                    { 1, "0101010101", "Soltero", new DateTime(1990, 5, 12, 0, 0, 0, 0, DateTimeKind.Unspecified), "Juan Pérez", false },
                    { 2, "0202020202", "Casado", new DateTime(1985, 11, 3, 0, 0, 0, 0, DateTimeKind.Unspecified), "María García", false },
                    { 3, "0303030303", "Soltero", new DateTime(1995, 2, 20, 0, 0, 0, 0, DateTimeKind.Unspecified), "Carlos López", false },
                    { 4, "0404040404", "Casado", new DateTime(1992, 8, 15, 0, 0, 0, 0, DateTimeKind.Unspecified), "Ana Torres", false },
                    { 5, "0505050505", "Soltero", new DateTime(1988, 1, 5, 0, 0, 0, 0, DateTimeKind.Unspecified), "Luis Castillo", false }
                });

            migrationBuilder.InsertData(
                table: "Users",
                columns: new[] { "Id", "Contrasena", "Nombre" },
                values: new object[] { 1, "MONSTER1", "MONSTER" });

            migrationBuilder.InsertData(
                table: "Cuentas",
                columns: new[] { "Id", "ClienteBancoId", "NumeroCuenta", "Saldo", "TipoCuenta" },
                values: new object[,]
                {
                    { 1, 1, "1234567890", 1800.00m, 1 },
                    { 2, 2, "9876543210", 2400.00m, 1 },
                    { 3, 3, "5556667771", 900.00m, 2 },
                    { 4, 4, "5556667772", 3200.00m, 1 },
                    { 5, 5, "5556667773", 1500.00m, 1 }
                });

            migrationBuilder.InsertData(
                table: "Movimientos",
                columns: new[] { "Id", "CuentaId", "Fecha", "Monto", "Tipo" },
                values: new object[,]
                {
                    { 1, 1, new DateTime(2025, 10, 6, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7457), 700.00m, "Deposito" },
                    { 2, 1, new DateTime(2025, 10, 21, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7463), 300.00m, "Deposito" },
                    { 3, 1, new DateTime(2025, 10, 22, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7465), 150.00m, "Retiro" },
                    { 4, 1, new DateTime(2025, 10, 11, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7467), 500.00m, "Deposito" },
                    { 5, 1, new DateTime(2025, 10, 24, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7468), 200.00m, "Retiro" },
                    { 6, 1, new DateTime(2025, 10, 16, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7470), 650.00m, "Deposito" },
                    { 7, 1, new DateTime(2025, 10, 26, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7471), 220.00m, "Deposito" },
                    { 8, 1, new DateTime(2025, 10, 28, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7473), 120.00m, "Retiro" },
                    { 9, 1, new DateTime(2025, 10, 29, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7474), 400.00m, "Deposito" },
                    { 10, 1, new DateTime(2025, 10, 30, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7476), 80.00m, "Retiro" },
                    { 11, 2, new DateTime(2025, 10, 16, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7477), 1200.00m, "Deposito" },
                    { 12, 2, new DateTime(2025, 10, 26, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7479), 400.00m, "Retiro" },
                    { 13, 2, new DateTime(2025, 10, 3, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7480), 300.00m, "Deposito" },
                    { 14, 2, new DateTime(2025, 10, 9, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7482), 450.00m, "Deposito" },
                    { 15, 2, new DateTime(2025, 10, 13, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7483), 200.00m, "Retiro" },
                    { 16, 2, new DateTime(2025, 10, 19, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7485), 600.00m, "Deposito" },
                    { 17, 2, new DateTime(2025, 10, 23, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7486), 90.00m, "Retiro" },
                    { 18, 2, new DateTime(2025, 10, 25, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7488), 720.00m, "Deposito" },
                    { 19, 2, new DateTime(2025, 10, 27, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7489), 50.00m, "Retiro" },
                    { 20, 2, new DateTime(2025, 10, 30, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7490), 510.00m, "Deposito" },
                    { 21, 3, new DateTime(2025, 10, 1, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7492), 250.00m, "Deposito" },
                    { 22, 3, new DateTime(2025, 10, 4, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7493), 60.00m, "Retiro" },
                    { 23, 3, new DateTime(2025, 10, 7, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7495), 140.00m, "Deposito" },
                    { 24, 3, new DateTime(2025, 10, 10, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7496), 310.00m, "Deposito" },
                    { 25, 3, new DateTime(2025, 10, 12, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7498), 80.00m, "Retiro" },
                    { 26, 3, new DateTime(2025, 10, 15, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7500), 500.00m, "Deposito" },
                    { 27, 3, new DateTime(2025, 10, 17, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7501), 40.00m, "Retiro" },
                    { 28, 3, new DateTime(2025, 10, 20, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7503), 280.00m, "Deposito" },
                    { 29, 3, new DateTime(2025, 10, 22, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7504), 55.00m, "Retiro" },
                    { 30, 3, new DateTime(2025, 10, 28, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7506), 360.00m, "Deposito" },
                    { 31, 4, new DateTime(2025, 10, 1, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7507), 900.00m, "Deposito" },
                    { 32, 4, new DateTime(2025, 10, 6, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7508), 1100.00m, "Deposito" },
                    { 33, 4, new DateTime(2025, 10, 8, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7510), 300.00m, "Retiro" },
                    { 34, 4, new DateTime(2025, 10, 10, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7511), 750.00m, "Deposito" },
                    { 35, 4, new DateTime(2025, 10, 13, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7513), 200.00m, "Retiro" },
                    { 36, 4, new DateTime(2025, 10, 16, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7514), 600.00m, "Deposito" },
                    { 37, 4, new DateTime(2025, 10, 21, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7516), 500.00m, "Deposito" },
                    { 38, 4, new DateTime(2025, 10, 24, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7517), 150.00m, "Retiro" },
                    { 39, 4, new DateTime(2025, 10, 27, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7518), 820.00m, "Deposito" },
                    { 40, 4, new DateTime(2025, 10, 29, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7520), 100.00m, "Retiro" },
                    { 41, 5, new DateTime(2025, 10, 2, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7521), 500.00m, "Deposito" },
                    { 42, 5, new DateTime(2025, 10, 5, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7523), 300.00m, "Deposito" },
                    { 43, 5, new DateTime(2025, 10, 9, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7524), 120.00m, "Retiro" },
                    { 44, 5, new DateTime(2025, 10, 11, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7525), 450.00m, "Deposito" },
                    { 45, 5, new DateTime(2025, 10, 14, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7527), 200.00m, "Deposito" },
                    { 46, 5, new DateTime(2025, 10, 18, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7528), 90.00m, "Retiro" },
                    { 47, 5, new DateTime(2025, 10, 22, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7530), 380.00m, "Deposito" },
                    { 48, 5, new DateTime(2025, 10, 25, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7531), 70.00m, "Retiro" },
                    { 49, 5, new DateTime(2025, 10, 28, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7532), 600.00m, "Deposito" },
                    { 50, 5, new DateTime(2025, 10, 30, 2, 31, 29, 779, DateTimeKind.Utc).AddTicks(7534), 250.00m, "Deposito" }
                });

            migrationBuilder.CreateIndex(
                name: "IX_AmortizacionesCredito_CreditoBancoId",
                table: "AmortizacionesCredito",
                column: "CreditoBancoId");

            migrationBuilder.CreateIndex(
                name: "IX_CreditosBanco_ClienteBancoId",
                table: "CreditosBanco",
                column: "ClienteBancoId");

            migrationBuilder.CreateIndex(
                name: "IX_Cuentas_ClienteBancoId",
                table: "Cuentas",
                column: "ClienteBancoId");

            migrationBuilder.CreateIndex(
                name: "IX_Movimientos_CuentaId",
                table: "Movimientos",
                column: "CuentaId");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "AmortizacionesCredito");

            migrationBuilder.DropTable(
                name: "Movimientos");

            migrationBuilder.DropTable(
                name: "Users");

            migrationBuilder.DropTable(
                name: "CreditosBanco");

            migrationBuilder.DropTable(
                name: "Cuentas");

            migrationBuilder.DropTable(
                name: "ClientesBanco");
        }
    }
}
