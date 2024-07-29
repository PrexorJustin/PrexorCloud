package me.prexorjustin.prexornetwork.cloud.driver.storage;

import com.google.common.io.BaseEncoding;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.rest.GeneralConfig;
import me.prexorjustin.prexornetwork.cloud.driver.event.EventDriver;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.rest.RestAPIEndpoints;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

@Getter
public class MessageStorage {

    private final String version = "1.0";
    @Setter
    private Integer canUseMemory;
    @Setter
    private boolean shutdownAccept;
    private final PacketLoader packetLoader;
    @Setter
    private EventDriver eventDriver;
    @Setter
    private String screenForm;
    @Setter
    private String language;
    @Setter
    private boolean openServiceScreen;
    private boolean printConsoleToManager;
    private String printConsoleToManagerName;
    private LinkedList<String> consoleInput;

    public MessageStorage() {
        this.packetLoader = new PacketLoader();
        this.eventDriver = new EventDriver();
        this.shutdownAccept = false;
        this.openServiceScreen = false;
        this.canUseMemory = 0;
        this.screenForm = "";
    }

    public int getCPULoad() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        if (operatingSystemMXBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
            return (int) (sunOsBean.getCpuLoad() * 100);
        }

        return -1;
    }

    public String getAsciiArt() {
        return "";
    }

    public String[] dropFirstString(String[] input) {
        String[] string = new String[input.length - 1];
        System.arraycopy(input, 1, string, 0, input.length - 1);
        return string;
    }

    @SneakyThrows
    public GeneralConfig loadGeneralConfig() {
        HttpRequest request = HttpRequest.newBuilder(RestAPIEndpoints.GENERAL.getUri()).build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        return (GeneralConfig) new ConfigDriver().convert(response.body(), GeneralConfig.class);
    }

    @SneakyThrows
    public boolean checkUpdateAvailability() {
        GeneralConfig generalConfig = loadGeneralConfig();

        return !generalConfig.getConfig().get("current-version").equalsIgnoreCase(this.version);
    }

    @SneakyThrows
    public String getNewVersionName() {
        if (!checkUpdateAvailability()) return this.version;
        GeneralConfig generalConfig = loadGeneralConfig();
        return generalConfig.getConfig().get("current-version");
    }

    @SneakyThrows
    public String utf8ToBase64(String string) {
        return BaseEncoding.base64().encode(string.getBytes(StandardCharsets.UTF_8));
    }

    public String base64ToUTF8(String string) {
        byte[] decode = BaseEncoding.base64().decode(string);
        return new String(decode, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public String getVelocityToml(int port, int maxPlayers, boolean useProtocol) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://84.247.173.227/cloud/defaults/velocity.toml")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        String operatingSystem = System.getProperty("os.name").toLowerCase();
        val isLinux = operatingSystem.contains("nix") || operatingSystem.contains("aix") || operatingSystem.contains("nux");

        return response.body()
                .replaceAll("%port%", String.valueOf(port))
                .replace("%maxPlayer%", String.valueOf(maxPlayers))
                .replace("%proxy%", String.valueOf(useProtocol))
                .replace("%forceKeyAuthentication%", isLinux ? "false" : "true")
                .replace("%tcpFastOpen%", isLinux ? "true" : "false");
    }

    @SneakyThrows
    public String getBungeecordConfig(int port, int maxPlayers, boolean useProtocol) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://84.247.173.227/cloud/defaults/bungeeConfig.yml")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        return response.body()
                .replaceAll("%port%", String.valueOf(port))
                .replace("%maxPlayer%", String.valueOf(maxPlayers))
                .replace("%proxyProtocol%", String.valueOf(useProtocol));
    }

    @SneakyThrows
    public String getBukkitYML() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://84.247.173.227/cloud/defaults/bukkit.yml")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    @SneakyThrows
    public String getSpigotYML() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://84.247.173.227/cloud/defaults/spigot.yml")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    @SneakyThrows
    public String getSpigotServerProperties(int port) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://84.247.173.227/cloud/defaults/server.properties")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        return response.body().replace("%port%", String.valueOf(port));
    }
}
