package me.prexorjustin.prexornetwork.cloud.api.player.interfaces;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.service.interfaces.ICloudService;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.CloudPlayerRestCache;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public abstract class ICloudPlayer {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private final String username;
    private final UUID uuid;

    public abstract ICloudService getProxyServer();

    public abstract ICloudService getService();

    public abstract void sendMessage(String message);

    public void sendMessage(String... messages) {
        Arrays.stream(messages).forEach(this::sendMessage);
    }

    public abstract void connect(ICloudService cloudService);

    public abstract void connect(ICloudPlayer cloudPlayer);

    public abstract void disconnect(String message);

    public void disconnect() {
        disconnect("§cYou got kicked from the Network!");
    }

    public boolean isConnectedToFallback() {
        return getService().isTypeLobby();
    }

    public String getSkinValue() {
        return new JSONObject(getPlayerDBResponse()).getJSONObject("data").getJSONObject("player").getJSONObject("properties").getString("value");
    }

    public String getSkinSignature() {
        return new JSONObject(getPlayerDBResponse()).getJSONObject("data").getJSONObject("player").getJSONObject("properties").getString("signature");
    }

    @SneakyThrows
    private String getPlayerDBResponse() {
        HttpRequest request = HttpRequest.newBuilder(URI.create("https://playerdb.co/api/player/minecraft/" + this.username)).GET().build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).body();
    }

    public String toString() {
        return "username='" + this.username + "', uniqueId='" + this.uuid + "', proxy='" + getProxyServer().getName() + "', service='" + getService().getName() + "', skinValue='" + getSkinValue() + "', skinSignature='" + getSkinSignature() + "', isConnectedOnFallback='" + isConnectedToFallback() + "', currentPlayTime='" + getCurrentPlayTime() + "'";
    }

    public CloudPlayerRestCache getCache() {
        return (CloudPlayerRestCache) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudplayer/" + this.uuid), CloudPlayerRestCache.class);
    }

    public long getCurrentPlayTime() {
        return getCache().getConnectTime();
    }

}
