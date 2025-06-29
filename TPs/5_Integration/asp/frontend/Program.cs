using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authentication.OpenIdConnect;
using Microsoft.IdentityModel.Tokens;

var builder = WebApplication.CreateBuilder(args);

// Ajoute Razor Pages
builder.Services.AddRazorPages();

// Ajoute IHttpClientFactory pour appel au backend
builder.Services.AddHttpClient();

// Authentification OIDC + Cookie
builder.Services.AddAuthentication(options =>
{
    options.DefaultScheme = CookieAuthenticationDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = OpenIdConnectDefaults.AuthenticationScheme;
})
.AddCookie()
.AddOpenIdConnect("oidc", options =>
{
	// Configurer OIDC
});

var app = builder.Build();


// 👇 C’est ici qu’on parle du **pipeline HTTP**
// Il définit l’ordre dans lequel les middlewares sont exécutés

app.UseHttpsRedirection();
app.UseStaticFiles();

app.UseRouting();

// Auth + Authorization doivent être APPELÉS dans le bon ordre
app.UseAuthentication();
app.UseAuthorization();

// Razor pages
app.MapRazorPages();

// Endpoints pour login/logout
app.MapGet("/signin", async context =>
{
    await context.ChallengeAsync("oidc", new AuthenticationProperties
    {
        RedirectUri = "/"
    });
});

app.MapGet("/signout", async context =>
{
    await context.SignOutAsync("Cookies");
    await context.SignOutAsync("oidc", new AuthenticationProperties
    {
        RedirectUri = "/"
    });
});

app.Run();
