package me.prexorjustin.prexornetwork.cloud.driver.storage.uuid;

import lombok.SneakyThrows;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class UUIDDriver {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static ArrayList<UUIDStorage> UUIDS;

    @SneakyThrows
    public static UUID getUUID(String name) {
        if (UUIDS == null) UUIDS = new ArrayList<>();

        if (UUIDS.stream().anyMatch(uuidStorage -> uuidStorage.username().equalsIgnoreCase(name)))
            return Objects.requireNonNull(UUIDS.stream().filter(uuidStorage -> uuidStorage.username().equalsIgnoreCase(name)).findFirst().orElse(null)).uuid();
        else {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://playerdb.co/api/player/minecraft/" + name)).GET().build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            JSONObject jsonResponse = new JSONObject(response.body());
            UUID uuid = UUID.fromString(jsonResponse.getJSONObject("data").getJSONObject("player").getString("id"));

            UUIDS.add(new UUIDStorage(name, uuid));

            return uuid;
        }
    }

    @SneakyThrows
    public static String getUserName(UUID uuid) {
        if (UUIDS == null) UUIDS = new ArrayList<>();

        if (UUIDS.stream().anyMatch(uuidStorage -> uuidStorage.uuid().equals(uuid)))
            return Objects.requireNonNull(UUIDS.stream().filter(uuidStorage -> uuidStorage.uuid().equals(uuid)).findFirst().orElse(null)).username();
        else {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://playerdb.co/api/player/minecraft/" + uuid.toString())).GET().build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            JSONObject jsonResponse = new JSONObject(response.body());
            String userName = jsonResponse.getJSONObject("data").getJSONObject("player").getString("username");

            UUIDS.add(new UUIDStorage(userName, uuid));

            return userName;
        }
    }
}
