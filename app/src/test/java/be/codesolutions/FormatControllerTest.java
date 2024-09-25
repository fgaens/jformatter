package be.codesolutions;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@Disabled
@MicronautTest
class FormatControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    FormatService formatService;

    @Test
    void testIndexPage() {
        String response = client.toBlocking().retrieve(HttpRequest.GET("/"));
        assertTrue(response.contains("Java Object Formatter"));
        assertTrue(response.contains("<form action=\"/format\" method=\"post\">"));
    }

    @Test
    void testFormatValidInput() {
        String input = "SomeJavaObject";
        String formattedOutput = "Formatted: SomeJavaObject";

        String response = client.toBlocking().retrieve(
                HttpRequest.POST("/format", "input=" + input)
        );

        assertTrue(response.contains(formattedOutput));
        verify(formatService).formatJavaObject(input);
    }

    @Test
    void testFormatEmptyInput() {
        String response = client.toBlocking().retrieve(
                HttpRequest.POST("/format", "input=")
        );

        assertFalse(response.contains("Formatted:"));
        verify(formatService, never()).formatJavaObject(any());
    }
}
