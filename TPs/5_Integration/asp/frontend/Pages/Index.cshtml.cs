using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Mvc.RazorPages;
using System.Net.Http.Headers;

namespace Frontend.Pages // ← adapte selon ton namespace réel
{
    public class IndexModel : PageModel
    {
        private readonly IHttpClientFactory _httpClientFactory;

        public string? ApiResponse { get; set; } // ✅ La propriété manquante

        public IndexModel(IHttpClientFactory httpClientFactory)
        {
            _httpClientFactory = httpClientFactory;
        }

        public async Task OnGetAsync()
        {
            if (!User.Identity?.IsAuthenticated ?? true)
                return;

            var accessToken = await HttpContext.GetTokenAsync("access_token");

            if (string.IsNullOrEmpty(accessToken))
            {
                ApiResponse = "Aucun token d'accès trouvé.";
                return;
            }

            var client = _httpClientFactory.CreateClient();
            client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", accessToken);

            try
            {
                var response = await client.GetAsync("http://localhost:5001/protected"); // ton backend
                ApiResponse = await response.Content.ReadAsStringAsync();
            }
            catch (Exception ex)
            {
                ApiResponse = $"Erreur lors de l'appel backend : {ex.Message}";
            }
        }
    }
}
