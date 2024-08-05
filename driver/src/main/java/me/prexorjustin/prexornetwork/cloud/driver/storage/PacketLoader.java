package me.prexorjustin.prexornetwork.cloud.driver.storage;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.rest.GeneralConfig;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.rest.SoftwareConfig;
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
import java.util.List;

@NoArgsConstructor
public final class PacketLoader {

    private final HttpClient client = HttpClient.newHttpClient();

    @SneakyThrows
    public void loadCloudAPI() {
        GeneralConfig updateConfig = (GeneralConfig) new ConfigDriver().convert(getHttpResponse(RestAPIEndpoints.GENERAL), GeneralConfig.class);
        String url = updateConfig.getConfig().get("cloud-api");
        downloadFile(url, "./local/GLOBAL/EVERY/plugins/prexorcloud-api.jar");
    }

    @SneakyThrows
    public void loadCloudPlugin() {
        GeneralConfig updateConfig = (GeneralConfig) new ConfigDriver().convert(getHttpResponse(RestAPIEndpoints.GENERAL), GeneralConfig.class);
        downloadFile(updateConfig.getConfig().get("cloud-plugin"), "./local/GLOBAL/EVERY/plugins/prexorcloud-plugin.jar");
    }

    public List<String> getAvailableBungeecords() {
        SoftwareConfig softwareConfig = (SoftwareConfig) new ConfigDriver().convert(getHttpResponse(RestAPIEndpoints.SOFTWARE), SoftwareConfig.class);

        return softwareConfig.getProxies().keySet().stream().toList();
    }

    public List<String> getAvailableSpigots() {
        SoftwareConfig softwareConfig = (SoftwareConfig) new ConfigDriver().convert(getHttpResponse(RestAPIEndpoints.SOFTWARE), SoftwareConfig.class);

        return softwareConfig.getSpigots().keySet().stream().toList();
    }

    public void loadBungeecord(String bungeecordVersion, String groupName) {
        SoftwareConfig softwareConfig = (SoftwareConfig) new ConfigDriver().convert(getHttpResponse(RestAPIEndpoints.SOFTWARE), SoftwareConfig.class);
        downloadFile(softwareConfig.getProxies().get(bungeecordVersion.toUpperCase()), "./local/templates/" + groupName + "/server.jar");
    }

    public void loadSpigot(String spigotVersion, String groupName) {
        SoftwareConfig softwareConfig = (SoftwareConfig) new ConfigDriver().convert(getHttpResponse(RestAPIEndpoints.SOFTWARE), SoftwareConfig.class);
        downloadFile(softwareConfig.getSpigots().get(spigotVersion.toUpperCase()), "./local/templates/" + groupName + "/server.jar");
    }

    @SneakyThrows
    private String getHttpResponse(RestAPIEndpoints restAPIEndpoint) {
        HttpRequest request = HttpRequest.newBuilder(restAPIEndpoint.getUri()).build();
        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        return response.body();
    }

    @SneakyThrows
    private void downloadFile(String url, String destination) {
        HttpRequest downloadAPIRequest = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<InputStream> downloadAPIResponse = this.client.send(downloadAPIRequest, HttpResponse.BodyHandlers.ofInputStream());

        try (InputStream stream = downloadAPIResponse.body()) {
            Path destinationPath = Paths.get(destination);
            Files.createDirectories(destinationPath.getParent());
            Files.write(destinationPath, stream.readAllBytes());
        }
    }
}
