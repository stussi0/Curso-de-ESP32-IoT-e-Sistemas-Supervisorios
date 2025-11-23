import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SimpleButtonServer {
    
    public static void main(String[] args) throws IOException {
        // Cria o servidor na porta 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Cria o contexto para a rota principal
        HttpContext context = server.createContext("/");
        context.setHandler(new ButtonHandler());
        
        // Inicia o servidor
        server.setExecutor(null); // usa executor padrão
        server.start();
        
        System.out.println("Servidor iniciado na porta 8080");
        System.out.println("Acesse: http://localhost:8080/");
    }
    
    static class ButtonHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String response = "";
            
            if ("GET".equalsIgnoreCase(method)) {
                response = getHtmlPage();
            } else if ("POST".equalsIgnoreCase(method)) {
                // Processa o formulário POST
                String query = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                response = processForm(query);
            }
            
            // Configura os headers de resposta
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            
            // Envia a resposta
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
        
        private String getHtmlPage() {
            return "<!DOCTYPE html>" +
                   "<html>" +
                   "<head>" +
                   "<title>Controle de Dispositivo</title>" +
                   "<meta charset='UTF-8'>" +
                   "</head>" +
                   "<body>" +
                   "<div style='text-align: center; margin-top: 100px; font-family: Arial, sans-serif;'>" +
                   "<h1 style='color: #333;'>Controle do Dispositivo</h1>" +
                   "<form method='POST' style='margin: 30px 0;'>" +
                   "<button type='submit' name='action' value='H' " + 
                   "style='background: linear-gradient(to bottom, #4CAF50, #45a049); " +
                   "color: white; padding: 18px 36px; text-align: center; " +
                   "text-decoration: none; display: inline-block; font-size: 18px; " +
                   "margin: 8px 16px; cursor: pointer; border: none; border-radius: 12px; " +
                   "box-shadow: 0 4px 8px rgba(0,0,0,0.2); transition: all 0.3s; " +
                   "font-weight: bold;'>🔵 LIGAR (H)</button>" +
                   
                   "<button type='submit' name='action' value='L' " + 
                   "style='background: linear-gradient(to bottom, #f44336, #d32f2f); " +
                   "color: white; padding: 18px 36px; text-align: center; " +
                   "text-decoration: none; display: inline-block; font-size: 18px; " +
                   "margin: 8px 16px; cursor: pointer; border: none; border-radius: 12px; " +
                   "box-shadow: 0 4px 8px rgba(0,0,0,0.2); transition: all 0.3s; " +
                   "font-weight: bold;'>🔴 DESLIGAR (L)</button>" +
                   "</form>" +
                   "<p style='color: #666; font-size: 14px;'>Clique em um botão para enviar o comando</p>" +
                   "</div>" +
                   "<script>" +
                   "// Adiciona efeitos hover aos botões" +
                   "document.addEventListener('DOMContentLoaded', function() {" +
                   "  var buttons = document.querySelectorAll('button');" +
                   "  buttons.forEach(function(button) {" +
                   "    button.addEventListener('mouseover', function() {" +
                   "      this.style.transform = 'translateY(-2px)';" +
                   "      this.style.boxShadow = '0 6px 12px rgba(0,0,0,0.3)';" +
                   "    });" +
                   "    button.addEventListener('mouseout', function() {" +
                   "      this.style.transform = 'translateY(0)';" +
                   "      this.style.boxShadow = '0 4px 8px rgba(0,0,0,0.2)';" +
                   "    });" +
                   "    button.addEventListener('mousedown', function() {" +
                   "      this.style.transform = 'translateY(1px)';" +
                   "    });" +
                   "    button.addEventListener('mouseup', function() {" +
                   "      this.style.transform = 'translateY(-2px)';" +
                   "    });" +
                   "  });" +
                   "});" +
                   "</script>" +
                   "</body>" +
                   "</html>";
        }
        
        private String getHtmlPageWithStatus(String status) {
            String statusColor = status.contains("Erro") ? "#f44336" : "#4CAF50";
            
            return "<!DOCTYPE html>" +
                   "<html>" +
                   "<head>" +
                   "<title>Controle de Dispositivo</title>" +
                   "<meta charset='UTF-8'>" +
                   "</head>" +
                   "<body>" +
                   "<div style='text-align: center; margin-top: 80px; font-family: Arial, sans-serif;'>" +
                   "<h1 style='color: #333;'>Controle do Dispositivo</h1>" +
                   "<div style='background-color: " + statusColor + "; color: white; padding: 15px; " +
                   "margin: 20px auto; border-radius: 8px; max-width: 600px;'>" +
                   "<strong>Status:</strong> " + status +
                   "</div>" +
                   "<form method='POST' style='margin: 30px 0;'>" +
                   "<button type='submit' name='action' value='H' " + 
                   "style='background: linear-gradient(to bottom, #4CAF50, #45a049); " +
                   "color: white; padding: 18px 36px; text-align: center; " +
                   "text-decoration: none; display: inline-block; font-size: 18px; " +
                   "margin: 8px 16px; cursor: pointer; border: none; border-radius: 12px; " +
                   "box-shadow: 0 4px 8px rgba(0,0,0,0.2); transition: all 0.3s; " +
                   "font-weight: bold;'>🔵 LIGAR (H)</button>" +
                   
                   "<button type='submit' name='action' value='L' " + 
                   "style='background: linear-gradient(to bottom, #f44336, #d32f2f); " +
                   "color: white; padding: 18px 36px; text-align: center; " +
                   "text-decoration: none; display: inline-block; font-size: 18px; " +
                   "margin: 8px 16px; cursor: pointer; border: none; border-radius: 12px; " +
                   "box-shadow: 0 4px 8px rgba(0,0,0,0.2); transition: all 0.3s; " +
                   "font-weight: bold;'>🔴 DESLIGAR (L)</button>" +
                   "</form>" +
                   "<p style='color: #666; font-size: 14px;'>Clique em um botão para enviar o comando</p>" +
                   "</div>" +
                   "<script>" +
                   "// Adiciona efeitos hover aos botões" +
                   "document.addEventListener('DOMContentLoaded', function() {" +
                   "  var buttons = document.querySelectorAll('button');" +
                   "  buttons.forEach(function(button) {" +
                   "    button.addEventListener('mouseover', function() {" +
                   "      this.style.transform = 'translateY(-2px)';" +
                   "      this.style.boxShadow = '0 6px 12px rgba(0,0,0,0.3)';" +
                   "    });" +
                   "    button.addEventListener('mouseout', function() {" +
                   "      this.style.transform = 'translateY(0)';" +
                   "      this.style.boxShadow = '0 4px 8px rgba(0,0,0,0.2)';" +
                   "    });" +
                   "    button.addEventListener('mousedown', function() {" +
                   "      this.style.transform = 'translateY(1px)';" +
                   "    });" +
                   "    button.addEventListener('mouseup', function() {" +
                   "      this.style.transform = 'translateY(-2px)';" +
                   "    });" +
                   "  });" +
                   "});" +
                   "</script>" +
                   "</body>" +
                   "</html>";
        }
        
        private String processForm(String query) {
            // Parse do query string
            String[] params = query.split("=");
            if (params.length >= 2 && ("action".equals(params[0]))) {
                String action = params[1];
                String urlString = "";
                
                if ("H".equals(action)) {
                    urlString = "http://192.168.23.48/H";
                } else if ("L".equals(action)) {
                    urlString = "http://192.168.23.48/L";
                }
                
                String status = enviarRequisicao(urlString);
                return getHtmlPageWithStatus(status);
            }
            
            return getHtmlPage();
        }
        
        private String enviarRequisicao(String urlString) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return "Comando enviado com sucesso para: " + urlString;
                } else {
                    return "Erro ao enviar comando. Código HTTP: " + responseCode;
                }
                
            } catch (Exception e) {
                return "Erro de conexão: " + e.getMessage();
            }
        }
    }
}