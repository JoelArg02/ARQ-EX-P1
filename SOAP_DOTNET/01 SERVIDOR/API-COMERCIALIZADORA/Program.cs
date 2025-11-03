using System.Reflection;
using System.Text;
using API_Comercializadora.Configuration;
using CoreWCF;
using CoreWCF.Configuration;
using CoreWCF.Description;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");
builder.Services.AddDbContext<AppDbContext>(options => options.UseSqlServer(connectionString));

builder.Services.AddServiceModelServices();
builder.Services.AddServiceModelMetadata();
builder.Services.AddSingleton<IServiceBehavior, UseRequestHeadersForMetadataAddressBehavior>();

var controllers = Assembly
    .GetExecutingAssembly()
    .GetTypes()
    .Where(t => t.Namespace != null && t.Namespace.EndsWith("Views") && t.IsClass && !t.IsAbstract)
    .ToList();

foreach (var controller in controllers)
{
    builder.Services.AddTransient(controller);
}

builder.Logging.ClearProviders();
builder.Logging.AddConsole();
builder.WebHost.ConfigureKestrel(options =>
{
    options.ListenAnyIP(5004);
});

var app = builder.Build();

// Lista de servicios disponibles
var serviceList = new List<string>();

app.UseServiceModel(serviceBuilder =>
{
    var addServiceEndpointMethod = typeof(IServiceBuilder)
        .GetMethods()
        .First(m => m.Name == "AddServiceEndpoint" && m.IsGenericMethodDefinition);

    foreach (var controller in controllers)
    {
        var interfaceType = controller
            .GetInterfaces()
            .FirstOrDefault(i => i.Name == "I" + controller.Name);
        if (interfaceType == null)
            continue;

        var serviceName = controller.Name.Replace("Controller", "Service");
        var servicePath = $"/{serviceName}.svc";
        
        serviceList.Add(servicePath);

        serviceBuilder.AddService(
            controller,
            options =>
            {
                options.DebugBehavior.IncludeExceptionDetailInFaults = true;
            }
        );

        var generic = addServiceEndpointMethod.MakeGenericMethod(controller, interfaceType);
        generic.Invoke(
            serviceBuilder,
            new object[] { new BasicHttpBinding(), servicePath }
        );
    }
});

var serviceMetadataBehavior = app.Services.GetRequiredService<ServiceMetadataBehavior>();
serviceMetadataBehavior.HttpGetEnabled = true;

// Página de inicio con lista de servicios
app.MapGet("/", async context =>
{
    var html = new StringBuilder();
    html.AppendLine("<!DOCTYPE html>");
    html.AppendLine("<html lang='es'>");
    html.AppendLine("<head>");
    html.AppendLine("    <meta charset='UTF-8'>");
    html.AppendLine("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
    html.AppendLine("    <title>API Comercializadora - Servicios SOAP</title>");
    html.AppendLine("    <style>");
    html.AppendLine("        body { font-family: Arial, sans-serif; max-width: 900px; margin: 50px auto; padding: 20px; background: #f5f5f5; }");
    html.AppendLine("        .container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
    html.AppendLine("        h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }");
    html.AppendLine("        .service-list { list-style: none; padding: 0; }");
    html.AppendLine("        .service-item { background: #ecf0f1; margin: 10px 0; padding: 15px; border-radius: 5px; border-left: 4px solid #3498db; }");
    html.AppendLine("        .service-item h3 { margin: 0 0 10px 0; color: #2c3e50; }");
    html.AppendLine("        .service-item a { color: #3498db; text-decoration: none; margin-right: 15px; }");
    html.AppendLine("        .service-item a:hover { text-decoration: underline; }");
    html.AppendLine("        .info { background: #d5f4e6; padding: 15px; border-radius: 5px; margin-top: 20px; border-left: 4px solid #27ae60; }");
    html.AppendLine("    </style>");
    html.AppendLine("</head>");
    html.AppendLine("<body>");
    html.AppendLine("    <div class='container'>");
    html.AppendLine("        <h1>API Comercializadora - Servicios SOAP</h1>");
    html.AppendLine("        <p>Servicios web disponibles en esta API:</p>");
    html.AppendLine("        <ul class='service-list'>");
    
    foreach (var service in serviceList)
    {
        var serviceName = service.Replace("/", "").Replace(".svc", "");
        html.AppendLine($"            <li class='service-item'>");
        html.AppendLine($"                <h3>{serviceName}</h3>");
        html.AppendLine($"                <a href='{service}' target='_blank'>Servicio</a>");
        html.AppendLine($"                <a href='{service}?wsdl' target='_blank'>WSDL</a>");
        html.AppendLine($"            </li>");
    }
    
    html.AppendLine("        </ul>");
    html.AppendLine("        <div class='info'>");
    html.AppendLine("            <strong>ℹInformación:</strong><br>");
    html.AppendLine($"            Base URL: <code>{context.Request.Scheme}://{context.Request.Host}</code><br>");
    html.AppendLine($"            Total de servicios: {serviceList.Count}");
    html.AppendLine("        </div>");
    html.AppendLine("    </div>");
    html.AppendLine("</body>");
    html.AppendLine("</html>");
    
    context.Response.ContentType = "text/html; charset=utf-8";
    await context.Response.WriteAsync(html.ToString());
});

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    await db.Database.EnsureCreatedAsync();
}

Console.WriteLine("=============================================");
Console.WriteLine("API COMERCIALIZADORA - Servicios SOAP");
Console.WriteLine("=============================================");
foreach (var service in serviceList)
{
    Console.WriteLine($"Servicio: http://localhost:5000{service}");
    Console.WriteLine($"WSDL: http://localhost:5000{service}?wsdl");
}
Console.WriteLine("=============================================");
Console.WriteLine("Página de inicio: http://localhost:5000/");
Console.WriteLine("=============================================");

app.Run();
