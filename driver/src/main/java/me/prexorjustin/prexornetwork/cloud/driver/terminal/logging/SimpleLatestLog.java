package me.prexorjustin.prexornetwork.cloud.driver.terminal.logging;

import lombok.SneakyThrows;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SimpleLatestLog {

    private final File latestLog;

    @SneakyThrows
    public SimpleLatestLog() {
        File logsDir = new File("./local/logs/");
        if (!logsDir.exists()) logsDir.mkdir();

        if (new File(logsDir, "latest.log").exists()) {
            getAllLogs().stream().filter(s -> !s.equalsIgnoreCase("latest.log"))
                    .map(s -> new File(logsDir, s)).forEach(File::delete);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            File oldLog = new File(logsDir, "latest.log");
            File newLog = new File(logsDir, "Log_" + timeFormatter.format(LocalDateTime.now()) + ".log");
            Files.copy(oldLog.toPath(), newLog.toPath(), StandardCopyOption.REPLACE_EXISTING);
            oldLog.delete();
        }

        this.latestLog = new File(logsDir, "latest.log");
        this.latestLog.createNewFile();
    }

    public void log(String line) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(this.latestLog, true)))) {
            writer.println(line);
        } catch (IOException ignored) {

        }
    }

    private List<String> getAllLogs() {
        File[] files = new File("./local/logs/").listFiles();
        ArrayList<String> logs = new ArrayList<>();

        for (File value : files != null ? files : new File[0]) logs.add(value.getName());

        return logs;
    }
}
