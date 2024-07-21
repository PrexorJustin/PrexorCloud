package me.prexorjustin.prexornetwork.cloud.driver.language;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.language.entry.LanguageConfig;
import me.prexorjustin.prexornetwork.cloud.driver.language.entry.LanguagePacket;
import me.prexorjustin.prexornetwork.cloud.driver.language.entry.Languages;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

@Getter
public class LanguageDriver {

    @Getter(AccessLevel.NONE)
    private final HttpClient client = HttpClient.newHttpClient();
    @Getter(AccessLevel.NONE)
    private final ObjectMapper objectMapper = new ObjectMapper();


    private final LanguagePacket language;

    public LanguageDriver() {
        this.language = new LanguagePacket();
        this.reload();
    }

    @SneakyThrows
    public ArrayList<String> getSupportedLanguages() {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://84.247.173.227/cloud/languages/LIST.json")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return this.objectMapper.readValue(response.body(), Languages.class).languages();
    }

    @SneakyThrows
    public void reload() {
        if (Files.exists(Paths.get("./local/storage/messages.storage"))) {
            LanguageConfig languageConfig = (LanguageConfig) new ConfigDriver("./local/storage/messages.storage").read(LanguageConfig.class);
            this.language.update(languageConfig.getMessages());
        } else {
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://84.247.173.227/cloud/languages/" + Driver.getInstance().getMessageStorage().getLanguage().toUpperCase() + ".json")).GET().build();
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            this.language.update(this.objectMapper.readValue(response.body(), HashMap.class));
        }
    }
}
