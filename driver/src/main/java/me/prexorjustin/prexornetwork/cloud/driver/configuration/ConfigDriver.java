package me.prexorjustin.prexornetwork.cloud.driver.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

public class ConfigDriver {

    private final String location;
    private final JsonMapper mapper;

    public ConfigDriver(String location) {
        this.location = location;
        this.mapper = JsonMapper.builder().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT).build();
    }

    public ConfigDriver() {
        this.location = "";
        this.mapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .build();
    }

    @SneakyThrows
    public IConfigAdapter read(Class<? extends IConfigAdapter> tClass) {
        try (InputStream inputStream = new FileInputStream(this.location)) {
            return this.mapper.readValue(inputStream, tClass);
        } catch (IOException exception) {
            return null;
        }
    }

    public boolean exists() {
        return new File(this.location).exists();
    }

    public boolean canBeRead(Class<? extends IConfigAdapter> tClass) {
        try {
            this.mapper.readValue(new File(this.location), tClass);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    @SneakyThrows
    public IConfigAdapter convert(String json, Class<? extends IConfigAdapter> tClass) {
        return mapper.readValue(json, tClass);
    }

    @SneakyThrows
    public String convert(IConfigAdapter iConfigAdapter) {
        return this.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(iConfigAdapter);
    }

    public void save(IConfigAdapter iConfigAdapter) {
        CompletableFuture.runAsync(() -> {
            try {
                File file = new File(this.location);
                if (!file.exists()) file.createNewFile();

                Files.writeString(file.toPath(), this.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(iConfigAdapter));
            } catch (IOException ignored) {
            }
        });
    }
}
