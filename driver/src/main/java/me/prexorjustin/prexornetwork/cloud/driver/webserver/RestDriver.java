package me.prexorjustin.prexornetwork.cloud.driver.webserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.authentication.AuthenticatorKey;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.interfaces.IRest;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.rest.RestMethod;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@NoArgsConstructor
public class RestDriver {

    private static final Gson GSON = (new GsonBuilder()).serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
    private static final HttpClient CLIENT = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    private String ip;
    private int port;

    public RestDriver(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @SneakyThrows
    public IRest convert(String json, Class<? extends IRest> tClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, tClass);
    }

    public String convert(IRest IRest) {
        return GSON.toJson(IRest);
    }

    @SneakyThrows
    public String update(String route, IRest content) {
        URI uri = this.createAuthenticatedRouteURI(route, RestMethod.PUT);

        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(GSON.toJson(content)))
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() == 200) {
            return response.body();
        }

        return null;
    }

    @SneakyThrows
    public String update(String route, String content) {
        URI uri = this.createAuthenticatedRouteURI(route, RestMethod.PUT);

        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(GSON.toJson(content)))
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() == 200) {
            return response.body();
        }

        return null;
    }

    @SneakyThrows
    public String create(String route, IRest content) {
        URI uri = this.createAuthenticatedRouteURI(route, RestMethod.POST);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(content)))
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() == 200) {
            return response.body();
        }

        return null;
    }

    @SneakyThrows
    public String create(String route, String content) {
        URI uri = this.createAuthenticatedRouteURI(route, RestMethod.POST);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(content)))
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() == 200) {
            return response.body();
        }

        return null;
    }

    @SneakyThrows
    public IRest get(String route, Class<? extends IRest> tClass) {
        URI uri = this.createAuthenticatedRouteURI(route, RestMethod.GET);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() == 200) {
            return new ObjectMapper().readValue(response.body(), tClass);
        }

        return null;
    }

    @SneakyThrows
    public String get(String route) {
        URI uri = this.createAuthenticatedRouteURI(route, RestMethod.GET);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() == 200) {
            return response.body();
        }

        return null;
    }

    @SneakyThrows
    public String delete(String route) {
        URI uri = this.createAuthenticatedRouteURI(route, RestMethod.DELETE);

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() == 200) {
            return response.body();
        }

        return null;
    }

    @SneakyThrows
    public String getWithoutAuthentication(String urls) {
        URI uri = URI.create(urls);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .timeout(Duration.ofSeconds(5))
                .build();
        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        if (response.statusCode() == 200) {
            return response.body();
        }

        return null;
    }

    private URI createAuthenticatedRouteURI(String route, RestMethod method) {
        ConfigDriver configDriver = new ConfigDriver("./connection.key");
        AuthenticatorKey authConfig = (AuthenticatorKey) configDriver.read(AuthenticatorKey.class);
        String authCheckKey = Driver.getInstance().getMessageStorage().base64ToUTF8(authConfig.getKey());

        return switch (method) {
            case POST, PUT -> URI.create(String.format("http://%s:%d/%s%s", this.ip, this.port, authCheckKey, route));
            case GET, DELETE -> URI.create("http://" + ip + ":" + port + "/" + authCheckKey + route);
        };
    }
}
