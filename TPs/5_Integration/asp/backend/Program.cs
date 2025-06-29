using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using System.Security.Claims;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
		// Configuration oAuth2
    });

builder.Services.AddAuthorization(options =>
{
   // Protéger sur le role ROLE_USER
});

var app = builder.Build();
app.UseAuthentication();
app.UseAuthorization();
//app.UseSwagger();
//app.UseSwaggerUI();

app.MapGet("/protected", (ClaimsPrincipal user) =>
{
    return Results.Ok($"Bienvenue {user.Identity?.Name}");
}).RequireAuthorization("RequireUserRole");

app.Run();
