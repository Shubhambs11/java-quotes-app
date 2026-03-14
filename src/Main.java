import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Main {
    private static List<String> quotes;

    public static void main(String[] args) throws IOException {

        // Load quotes from file
        quotes = loadQuotesFromFile("quotes.txt");

        if (quotes.isEmpty()) {
            System.err.println("No quotes found in the file.");
            return;
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Serve HTML UI
        server.createContext("/", exchange -> {

            String html = new String(
                    Files.readAllBytes(Paths.get("index.html")),
                    StandardCharsets.UTF_8
            );

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(html.getBytes());
            }
        });

        // API endpoint for quotes
        server.createContext("/quote", exchange -> {

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            String quote = getRandomQuote();

            String jsonResponse = "{\"quote\": \"" + quote.replace("\"", "\\\"") + "\"}";

            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        });

        server.start();
        System.out.println("Server running at http://localhost:8000");
    }

    private static String getRandomQuote() {
        Random random = new Random();
        return quotes.get(random.nextInt(quotes.size()));
    }

    private static List<String> loadQuotesFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error reading quotes file: " + e.getMessage());
            return List.of();
        }
    }
}
