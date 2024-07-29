package me.prexorjustin.prexornetwork.cloud.driver.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.interfaces.IConfigAdapter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class ConfigDriver {

    protected static final Gson GSON = (new GsonBuilder()).serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
    private final String location;
    private final ObjectMapper mapper;

    public ConfigDriver(String location) {
        this.location = location;
        this.mapper = new ObjectMapper();
    }

    public ConfigDriver() {
        this.location = "";
        this.mapper = new ObjectMapper();
    }

    @SneakyThrows
    public IConfigAdapter read(Class<? extends IConfigAdapter> tClass) {
        try (InputStream inputStream = new FileInputStream(this.location)) {
            this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

    public String convert(IConfigAdapter iConfigAdapter) {
        return GSON.toJson(iConfigAdapter);
    }

    public void save(IConfigAdapter IConfigAdapter) {
        CompletableFuture.runAsync(() -> {
            try {
                File file = new File(this.location);
                if (!file.exists()) file.createNewFile();

                try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                    GSON.toJson(IConfigAdapter, writer);
                    writer.flush();
                }
            } catch (IOException ignored) {
            }
        });
    }
}
