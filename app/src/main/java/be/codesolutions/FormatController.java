package be.codesolutions;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.views.View;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;

@Controller
public class FormatController {

    @Inject
    private FormatService formatService;

    @View("index")
    @Get
    public Map<String, Object> index() {
        return new HashMap<>();
    }

    @Post("/transform")
    public Map<String, String> transform(@Body Map<String, String> payload) {
        var unformattedInput = payload.get("input");
        var formattedOutput = formatService.formatJavaObject(unformattedInput);

        Map<String, String> response = new HashMap<>();
        response.put("output", formattedOutput);
        return response;
    }
}
