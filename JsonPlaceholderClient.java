import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class JsonPlaceholderClient {

  private static final String DEFAULT_BASE_URL = "https://jsonplaceholder.typicode.com";

  private final HttpClient httpClient;
  private final String baseUrl;

  public JsonPlaceholderClient() {
    this(DEFAULT_BASE_URL, HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build());
  }

  public JsonPlaceholderClient(String baseUrl) {
    this(baseUrl, HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build());
  }

  JsonPlaceholderClient(String baseUrl, HttpClient httpClient) {
    this.baseUrl = requireNonBlank(baseUrl, "baseUrl");
    this.httpClient = requireNonNull(httpClient, "httpClient");
  }

  public Post getPost(int id) {
    if (id <= 0) {
      throw new IllegalArgumentException("id must be positive");
    }

    String url = baseUrl + "/posts/" + id;
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .timeout(Duration.ofSeconds(10))
        .GET()
        .build();

    try {
      HttpResponse<String> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200) {
        throw new ApiException(
            "GET " + url + " failed with status " + response.statusCode());
      }

      return parsePost(response.body());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ApiException("GET " + url + " failed", e);
    } catch (IOException e) {
      throw new ApiException("GET " + url + " failed", e);
    }
  }

  static Post parsePost(String json) {
    requireNonBlank(json, "json");

    return new Post(
        extractInt(json, "userId"),
        extractInt(json, "id"),
        extractString(json, "title"),
        extractString(json, "body"));
  }

  private static int extractInt(String json, String field) {
    String key = "\"" + field + "\":";
    int start = json.indexOf(key);
    if (start < 0) {
      throw new IllegalArgumentException("Missing field: " + field);
    }

    start += key.length();
    int end = json.indexOf(',', start);
    if (end < 0) {
      end = json.indexOf('}', start);
    }

    return Integer.parseInt(json.substring(start, end).trim());
  }

  private static String extractString(String json, String field) {
    String key = "\"" + field + "\":\"";
    int start = json.indexOf(key);
    if (start < 0) {
      throw new IllegalArgumentException("Missing field: " + field);
    }

    start += key.length();
    StringBuilder value = new StringBuilder();

    for (int i = start; i < json.length(); i++) {
      char current = json.charAt(i);
      if (current == '"') {
        return value.toString();
      }
      if (current == '\\' && i + 1 < json.length()) {
        value.append(json.charAt(++i));
      } else {
        value.append(current);
      }
    }

    throw new IllegalArgumentException("Unterminated string for field: " + field);
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(fieldName + " must not be blank");
    }
    return value;
  }

  private static <T> T requireNonNull(T value, String fieldName) {
    if (value == null) {
      throw new IllegalArgumentException(fieldName + " must not be null");
    }
    return value;
  }

  public static class ApiException extends RuntimeException {
    public ApiException(String message) {
      super(message);
    }

    public ApiException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
