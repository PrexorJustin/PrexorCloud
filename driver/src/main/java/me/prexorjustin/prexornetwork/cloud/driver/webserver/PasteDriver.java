package me.prexorjustin.prexornetwork.cloud.driver.webserver;

import lombok.SneakyThrows;
import nl.kyllian.enums.PasteFormat;
import nl.kyllian.models.Paste;

public class PasteDriver {

    private static final Paste PASTE = new Paste("http://localhost:8080/").setPasteFormat(PasteFormat.SYNTAX_HIGHLIGHTING);

    @SneakyThrows
    public String paste(String text) {
        return PASTE.setMessage(text).encrypt().send();
    }
}