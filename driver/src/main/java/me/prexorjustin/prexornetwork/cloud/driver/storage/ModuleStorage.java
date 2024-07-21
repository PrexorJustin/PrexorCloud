package me.prexorjustin.prexornetwork.cloud.driver.storage;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.rest.ModuleConfig;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.rest.RestAPIEndpoints;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@NoArgsConstructor
public class ModuleStorage {

    private final HttpClient client = HttpClient.newHttpClient();

    @SneakyThrows
    public void downloadModule(String moduleName) {
        ModuleConfig moduleConfig = getModules();
        if (moduleConfig.getModules().isEmpty()) return;
        if (!moduleConfig.getModules().containsKey(moduleName)) return;

        String downloadURL = moduleConfig.getModules().get(moduleName);
        HttpRequest downloadAPIRequest = HttpRequest.newBuilder(URI.create(downloadURL)).GET().build();
        HttpResponse<InputStream> downloadAPIResponse = this.client.send(downloadAPIRequest, HttpResponse.BodyHandlers.ofInputStream());

        try (InputStream stream = downloadAPIResponse.body()) {
            Files.write(
                    Paths.get("./modules/prexorcloud-" + moduleName.toLowerCase() + ".jar"),
                    stream.readAllBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
            );
        }
    }

    @SneakyThrows
    public void downloadAllModules() {
        this.getModules().getModules().keySet().forEach(this::downloadModule);
    }

    @SneakyThrows
    public void updateModule(String name) {
        Path modulePath = Paths.get("./modules/prexorcloud-" + name.toLowerCase() + ".jar");
        if (!Files.exists(modulePath)) return;

        modulePath.toFile().delete();
        this.downloadModule(name);
    }

    @SneakyThrows
    public void updateAllModules() {
        getModules().getModules().keySet().forEach(this::updateModule);
    }

    @SneakyThrows
    private ModuleConfig getModules() {
        return (ModuleConfig) new ConfigDriver().convert(this.getHttpResponse(RestAPIEndpoints.MODULES), ModuleConfig.class);
    }

    @SneakyThrows
    private String getHttpResponse(RestAPIEndpoints restAPIEndpoint) {
        HttpRequest request = HttpRequest.newBuilder(restAPIEndpoint.getUri()).build();
        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        return response.body();
    }
}
