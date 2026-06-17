public class JsonPlaceholderClientTest {

  private static int passed = 0;
  private static int failed = 0;

  public static void main(String[] args) {
    testParsePost();
    testGetPostLive();

    System.out.printf("%n%d passed, %d failed%n", passed, failed);
    if (failed > 0) {
      System.exit(1);
    }
  }

  private static void testParsePost() {
    String json =
        """
        {
          "userId": 1,
          "id": 1,
          "title": "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
          "body": "quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto"
        }
        """;

    Post post = JsonPlaceholderClient.parsePost(json);

    assertEquals("userId", 1, post.userId());
    assertEquals("id", 1, post.id());
    assertEquals(
        "title",
        "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
        post.title());
    assertTrue("body contains newline", post.body().contains("\n"));
  }

  private static void testGetPostLive() {
    JsonPlaceholderClient client = new JsonPlaceholderClient();

    try {
      Post post = client.getPost(1);
      assertEquals("live userId", 1, post.userId());
      assertEquals("live id", 1, post.id());
      assertTrue("live title not blank", !post.title().isBlank());
      assertTrue("live body not blank", !post.body().isBlank());
      System.out.printf(
          "PASS getPost(1) -> id=%d, title=%s%n", post.id(), truncate(post.title(), 40));
    } catch (JsonPlaceholderClient.ApiException e) {
      failed++;
      System.out.printf("FAIL getPost(1): %s%n", e.getMessage());
    }
  }

  private static void assertEquals(String label, int expected, int actual) {
    if (expected == actual) {
      passed++;
      System.out.printf("PASS %s = %d%n", label, actual);
    } else {
      failed++;
      System.out.printf("FAIL %s: expected %d, got %d%n", label, expected, actual);
    }
  }

  private static void assertEquals(String label, String expected, String actual) {
    if (expected.equals(actual)) {
      passed++;
      System.out.printf("PASS %s%n", label);
    } else {
      failed++;
      System.out.printf("FAIL %s: expected %s, got %s%n", label, expected, actual);
    }
  }

  private static void assertTrue(String label, boolean condition) {
    if (condition) {
      passed++;
      System.out.printf("PASS %s%n", label);
    } else {
      failed++;
      System.out.printf("FAIL %s%n", label);
    }
  }

  private static String truncate(String value, int maxLength) {
    if (value.length() <= maxLength) {
      return value;
    }
    return value.substring(0, maxLength) + "...";
  }
}
