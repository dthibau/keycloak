using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Mvc.RazorPages;
using System.Text.Json;
using System.Text;

namespace Frontend.Pages;

public class IndexModel : PageModel
{
    public string? AccessToken { get; private set; }
    public string? AccessTokenJson { get; private set; }
    public string? ApiResponse { get; private set; }

    public async Task OnGetAsync()
    {
        if (User.Identity?.IsAuthenticated ?? false)
        {
            var authResult = await HttpContext.AuthenticateAsync();
            AccessToken = authResult.Properties?.GetTokenValue("access_token");
            if (!string.IsNullOrEmpty(AccessToken))
            {
                AccessTokenJson = FormatJwtAsJson(AccessToken);
            }
        }
    }

    public async Task OnPostCallApiAsync()
    {
        var accessToken = (await HttpContext.AuthenticateAsync()).Properties?.GetTokenValue("access_token");

        using var httpClient = new HttpClient();
        httpClient.DefaultRequestHeaders.Authorization = new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", accessToken);

        var response = await httpClient.GetAsync("http://localhost:8001/protected"); // ton backend
        ApiResponse = await response.Content.ReadAsStringAsync();

        Console.WriteLine("Réponse du backend :");
        Console.WriteLine($"StatusCode : {response.StatusCode}");
        Console.WriteLine($"Content-Type : {response.Content.Headers.ContentType}");
        Console.WriteLine($"Body : {ApiResponse}");

        // Recharge aussi l’access_token si besoin
        AccessToken = accessToken;
    }

    private string FormatJwtAsJson(string jwt)
    {
        var parts = jwt.Split('.');
        if (parts.Length != 3) return "Token non valide";

        string payload = parts[1];

        // Convertir Base64URL → Base64 standard
        payload = payload.Replace('-', '+').Replace('_', '/');
        switch (payload.Length % 4)
        {
            case 2: payload += "=="; break;
            case 3: payload += "="; break;
            case 0: break;
            default: return "Format Base64 invalide";
        }

        try
        {
            var bytes = Convert.FromBase64String(payload);
            var json = Encoding.UTF8.GetString(bytes);
            using var doc = JsonDocument.Parse(json);
            return JsonSerializer.Serialize(doc.RootElement, new JsonSerializerOptions { WriteIndented = true });
        }
        catch (Exception ex)
        {
            return $"Erreur de décodage : {ex.Message}";
        }
    }

}
