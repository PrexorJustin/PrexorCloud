package me.prexorjustin.prexornetwork.cloud.driver.terminal;

import lombok.Getter;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandDriver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.completer.TerminalCompleter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Color;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.logging.SimpleLatestLog;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.SetupDriver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.setups.general.GeneralSetup;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.setup.setups.group.GroupSetup;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.streams.LoggerOutputStream;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorage;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.entry.RouteEntry;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.node.PacketInSendConsole;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.in.node.PacketInSendConsoleFromNode;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Queue;

@Getter
public class TerminalDriver {

    private final LinkedList<TerminalStorage> mainScreenStorage;
    private final Queue<TerminalStorageLine> inputs;

    private final SetupDriver setupDriver;
    private final CommandDriver commandDriver;

    private final Terminal terminal;
    private final LineReader lineReader;
    private final SimpleLatestLog simpleLatestLog;

    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    private boolean inSetup;

    @SneakyThrows
    public TerminalDriver() {
        System.setOut(new PrintStream(new LoggerOutputStream(Type.INFO), true));
        System.setErr(new PrintStream(new LoggerOutputStream(Type.ERROR), true));

        this.commandDriver = new CommandDriver();
        this.setupDriver = new SetupDriver();

        this.mainScreenStorage = new LinkedList<>();
        this.inputs = new LinkedList<>();

        this.simpleLatestLog = new SimpleLatestLog();

        this.inSetup = false;

        this.terminal = TerminalBuilder.builder()
                .system(true)
                .encoding(StandardCharsets.UTF_8)
                .name("PREXOR-CONSOLE")
                .build();

        this.lineReader = LineReaderBuilder.builder()
                .terminal(this.terminal)
                .completer(new TerminalCompleter())
                .appName("PREXOR-READER")
                .option(LineReader.Option.AUTO_REMOVE_SLASH, true)
                .option(LineReader.Option.HISTORY_IGNORE_SPACE, true)
                .option(LineReader.Option.HISTORY_REDUCE_BLANKS, true)
                .option(LineReader.Option.HISTORY_IGNORE_DUPS, true)
                .option(LineReader.Option.EMPTY_WORD_OPTIONS, false)
                .option(LineReader.Option.HISTORY_TIMESTAMPED, false)
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                .option(LineReader.Option.INSERT_TAB, false)
                .variable(LineReader.HISTORY_SIZE, 500)
                .parser(new DefaultParser().eofOnUnclosedQuote(true))
                .build();

        Thread consoleReaderThread = new TerminalReader(this);
        consoleReaderThread.setName("CONSOLE");
        consoleReaderThread.setPriority(Thread.MAX_PRIORITY);
        consoleReaderThread.start();
    }

    public void joinSetup() {
        this.inSetup = true;

        if (!Files.exists(Paths.get("./service.json")) && !Files.exists(Paths.get("./nodeservice.json"))) {
            String joinedLanguages = String.join(", ", Driver.getInstance().getLanguageDriver().getSupportedLanguages());
            clearScreen();
            this.log(
                    Type.EMPTY,
                    Driver.getInstance().getMessageStorage().getAsciiArt()
            );
            this.log(
                    Type.INSTALLATION,
                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-general-question-1")
            );
            this.log(
                    Type.INSTALLATION,
                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-general-question-possible-answers")
                            .replace("%possible_answers%", joinedLanguages)
            );
            this.setupDriver.setSetup(new GeneralSetup());
        } else {
            clearScreen();
            this.log(Type.EMPTY, Driver.getInstance().getMessageStorage().getAsciiArt());
            Driver.getInstance().getTerminalDriver().log(Type.INSTALLATION, Driver.getInstance().getLanguageDriver().getLanguage().getMessage("setup-group-question-1"));
            this.setupDriver.setSetup(new GroupSetup());
        }
    }

    public void leaveSetup() {
        clearScreen();

        if (Driver.getInstance().getMessageStorage().isOpenServiceScreen()) {
            Driver.getInstance().getMessageStorage().setOpenServiceScreen(false);
            Driver.getInstance().getMessageStorage().setScreenForm("");
        }

        this.inSetup = false;

        if (setupDriver.getSetup() instanceof GroupSetup) {
            Driver.getInstance().getGroupDriver().getAll().forEach(group -> {
                if (group == null) return;

                if (Driver.getInstance().getWebServer().getRoute("/" + group.getName()) == null)
                    Driver.getInstance().getWebServer().addRoute(new RouteEntry("/" + group.getName(), new ConfigDriver().convert(group)));
                else
                    Driver.getInstance().getWebServer().updateRoute("/" + group.getName(), new ConfigDriver().convert(group));
            });
        }

        this.setupDriver.setSetup(null);
        this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);

        if (this.mainScreenStorage.size() > 200) {
            for (int i = this.mainScreenStorage.size() - 200; i != this.mainScreenStorage.size(); i++) {
                TerminalStorage storage = this.mainScreenStorage.get(i);

                if (storage.type() == Type.EMPTY) {
                    String msg = storage.message();
                    this.terminal.writer().println("\r" + getColoredString(msg + Color.RESET.getAnsiCode()));
                } else {
                    this.terminal.writer().println("\r" + getColoredString("§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §b" + storage.type().toString().toUpperCase() + "§7: §r" + storage.message() + Color.RESET.getAnsiCode()));
                }
            }
        } else {
            for (int i = this.mainScreenStorage.size() > 60 ? this.mainScreenStorage.size() - 30 : 0; i != this.mainScreenStorage.size(); i++) {
                TerminalStorage storage = this.mainScreenStorage.get(i);
                if (storage.type() == Type.EMPTY) {
                    String msg = storage.message();
                    this.terminal.writer().println("\r" + getColoredString(msg + Color.RESET.getAnsiCode()));
                } else {
                    this.terminal.writer().println("\r" + getColoredString("§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §b" + storage.type().toString().toUpperCase() + "§7: §r" + storage.message() + Color.RESET.getAnsiCode()));
                }
            }
        }

        this.redisplay();
    }

    public void redirect() {
        this.clearScreen();
        this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);

        if (this.mainScreenStorage.size() > 200) {
            for (int i = this.mainScreenStorage.size() - 200; i != this.mainScreenStorage.size(); i++) {
                TerminalStorage storage = this.mainScreenStorage.get(i);

                if (storage.type() == Type.EMPTY) {
                    String message = storage.message();
                    this.terminal.writer().println("\r" + getColoredString(message + Color.RESET.getAnsiCode()));
                } else {
                    this.terminal.writer().println("\r" + getColoredString(
                            "§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §b" + storage.type().toString().toUpperCase() + "§7: §r" + storage.message() + Color.RESET.getAnsiCode()
                    ));
                }
            }
        } else {
            for (int i = this.mainScreenStorage.size() > 60 ? this.mainScreenStorage.size() - 30 : 0; i != this.mainScreenStorage.size(); i++) {
                TerminalStorage storage = this.mainScreenStorage.get(i);

                if (storage.type() == Type.EMPTY) {
                    String message = storage.message();
                    this.terminal.writer().println("\r" + getColoredString(message + Color.RESET.getAnsiCode()));
                } else {
                    this.terminal.writer().println("\r" + getColoredString(
                            "§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §b" + storage.type().toString().toUpperCase() + "§7: §r" + storage.message() + Color.RESET.getAnsiCode()
                    ));
                }
            }
        }

        this.redisplay();
    }

    public void log(String service, String message) {
        this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
        this.terminal.writer().println(
                "\r" + getColoredString("§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §b" + service + "§7: §r" + message + Color.RESET.getAnsiCode())
        );

        this.lineReader.getTerminal().flush();
        this.simpleLatestLog.log(getColoredString(getClearSting(
                "§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §c" + service + "§7: §r" + message
        )));

        this.redisplay();
    }

    public void log(Type type, String... messages) {
        this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);

        for (int i = 0; i != messages.length; i++) {
            if (Driver.getInstance().getMessageStorage().isPrintConsoleToManager() && (Driver.getInstance().getMessageStorage().getPrintConsoleToManagerName() == null || Driver.getInstance().getMessageStorage().getPrintConsoleToManagerName().equalsIgnoreCase("")))
                NettyDriver.getInstance().getNettyClient().sendPacketSynchronized(new PacketInSendConsoleFromNode(messages[i]));

            this.terminal.writer().println(
                    "\r" + getColoredString("§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §b" + type.toString().toUpperCase()
                            .replace("INFORMATION", "§bINFORMATION")
                            .replace("ERROR", "§cERROR")
                            .replace("WARNING", "§eWARN") + "§7: §r" + messages[i] + Color.RESET.getAnsiCode())
            );
            this.simpleLatestLog.log(getClearSting(
                    "§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §b" + type.toString().toUpperCase() + "§7: §r" + messages[i]
            ));
        }

        this.redisplay();
    }

    public void log(Type type, String message) {
        if (Driver.getInstance().getMessageStorage().isPrintConsoleToManager() && (Driver.getInstance().getMessageStorage().getPrintConsoleToManagerName() == null || Driver.getInstance().getMessageStorage().getPrintConsoleToManagerName().equalsIgnoreCase("")))
            NettyDriver.getInstance().getNettyClient().sendPacketSynchronized(new PacketInSendConsoleFromNode(message));

        if (Driver.getInstance().getMessageStorage().isOpenServiceScreen())
            this.mainScreenStorage.add(new TerminalStorage(type, message));
        else if (!this.inSetup) {
            this.mainScreenStorage.add(new TerminalStorage(type, message));

            switch (type) {
                case EMPTY -> {
                    this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
                    this.terminal.writer().println("\r" + getColoredString(message + Color.RESET.getAnsiCode()));
                    this.lineReader.getTerminal().flush();
                    simpleLatestLog.log(getClearSting(getClearSting(message)));

                    if (!this.lineReader.isReading()) return;
                    this.lineReader.callWidget(LineReader.REDRAW_LINE);
                    this.lineReader.callWidget(LineReader.REDISPLAY);
                }
                case ERROR -> {
                    this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
                    this.terminal.writer().println(
                            "\r" + getColoredString("§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §c" + type.toString().toUpperCase() + "§7: §r" + message + Color.RESET.getAnsiCode())
                    );
                    simpleLatestLog.log(
                            getClearSting("[" + this.timeFormat.format(System.currentTimeMillis()) + "] §c" + type.toString().toUpperCase() + "§7: §r" + message)
                    );

                    this.lineReader.getTerminal().flush();
                    if (!this.lineReader.isReading()) return;
                    this.lineReader.callWidget(LineReader.REDRAW_LINE);
                    this.lineReader.callWidget(LineReader.REDISPLAY);
                }

                case INFO -> {
                    this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
                    this.terminal.writer().println(
                            "\r" + getColoredString("§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §b" + type.toString().toUpperCase() + "§7: §r" + message + Color.RESET.getAnsiCode())
                    );
                    simpleLatestLog.log(
                            getClearSting("[" + this.timeFormat.format(System.currentTimeMillis()) + "] §b" + type.toString().toUpperCase() + "§7: §r" + message)
                    );

                    this.lineReader.getTerminal().flush();
                    if (!this.lineReader.isReading()) return;
                    this.lineReader.callWidget(LineReader.REDRAW_LINE);
                    this.lineReader.callWidget(LineReader.REDISPLAY);
                }

                case WARN -> {
                    this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
                    this.terminal.writer().println(
                            "\r" + getColoredString("§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §e" + type.toString().toUpperCase() + "§7: §r" + message + Color.RESET.getAnsiCode())
                    );
                    simpleLatestLog.log(getClearSting(
                            "§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §e" + type.toString().toUpperCase() + "§7: §r" + message)
                    );

                    this.lineReader.getTerminal().flush();
                    if (!this.lineReader.isReading()) return;
                    this.lineReader.callWidget(LineReader.REDRAW_LINE);
                    this.lineReader.callWidget(LineReader.REDISPLAY);
                }
                default -> {
                    this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
                    this.terminal.writer().println(
                            "\r" + getColoredString("§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §b" + type.toString().toUpperCase() + "§7: §r" + message + Color.RESET.getAnsiCode())
                    );
                    simpleLatestLog.log(getClearSting(
                            "§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §b" + type.toString().toUpperCase() + "§7: §r" + message)
                    );

                    this.lineReader.getTerminal().flush();
                    if (!this.lineReader.isReading()) return;
                    this.lineReader.callWidget(LineReader.REDRAW_LINE);
                    this.lineReader.callWidget(LineReader.REDISPLAY);
                }
            }
        } else {
            switch (type) {
                case INSTALLATION -> {
                    this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
                    this.terminal.writer().println(
                            "\r" + getColoredString("§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §bINSTALLATION§7: §r" + message + Color.RESET.getAnsiCode())
                    );
                    simpleLatestLog.log(
                            getClearSting("[" + this.timeFormat.format(System.currentTimeMillis()) + "] SETUP: " + message)
                    );

                    this.lineReader.getTerminal().flush();

                    if (!this.lineReader.isReading()) return;
                    this.lineReader.callWidget(LineReader.REDRAW_LINE);
                    this.lineReader.callWidget(LineReader.REDISPLAY);
                }

                case SETUP_ERROR -> {
                    this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
                    this.terminal.writer().println(
                            "\r" + getColoredString("§7[§f" + this.timeFormat.format(System.currentTimeMillis()) + "§7] §bINCORRECT§7: §r" + message + Color.RESET.getAnsiCode())
                    );
                    simpleLatestLog.log(
                            getClearSting("[" + this.timeFormat.format(System.currentTimeMillis()) + "] INCORRECT: " + message)
                    );

                    this.lineReader.getTerminal().flush();

                    if (!this.lineReader.isReading()) return;
                    this.lineReader.callWidget(LineReader.REDRAW_LINE);
                    this.lineReader.callWidget(LineReader.REDISPLAY);
                }

                case EMPTY -> {
                    this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
                    this.terminal.writer().println("\r" + getColoredString(message + Color.RESET.getAnsiCode()));
                    simpleLatestLog.log(getClearSting("\r" + getClearSting(message)));

                    this.lineReader.getTerminal().flush();
                    if (!this.lineReader.isReading()) return;
                    this.lineReader.callWidget(LineReader.REDRAW_LINE);
                    this.lineReader.callWidget(LineReader.REDISPLAY);
                }

                default -> this.mainScreenStorage.add(new TerminalStorage(type, message));
            }
        }
    }

    public String getColoredString(String text) {
        if (Driver.getInstance().getMessageStorage().isPrintConsoleToManager())
            NettyDriver.getInstance().getNettyClient().sendPacketSynchronized(
                    new PacketInSendConsole(Driver.getInstance().getMessageStorage().getPrintConsoleToManagerName(), text)
            );

        for (Color value : Color.values()) {
            text = text.replace('§' + String.valueOf(value.getIndex()), value.getAnsiCode());
        }

        return text;
    }

    public String getClearSting(String text) {
        text = text.replace("§r", "")
                .replace("§f", "")
                .replace("§0", "")
                .replace("§c", "")
                .replace("§e", "")
                .replace("§9", "")
                .replace("§a", "")
                .replace("§5", "")
                .replace("§6", "")
                .replace("§7", "")
                .replace("§4", "")
                .replace("§8", "")
                .replace("§1", "")
                .replace("§2", "")
                .replace("§b", "")
                .replace("§3", "");

        return text;
    }

    public void clearScreen() {
        if (!this.inSetup && !Driver.getInstance().getMessageStorage().isOpenServiceScreen())
            this.mainScreenStorage.clear();

        this.terminal.puts(InfoCmp.Capability.clear_screen);
        this.terminal.flush();
        this.redraw();
    }

    public void redraw() {
        if (this.lineReader.isReading()) {
            this.lineReader.callWidget(LineReader.REDRAW_LINE);
            this.lineReader.callWidget(LineReader.REDISPLAY);
        }
    }

    private void redisplay() {
        this.lineReader.getTerminal().flush();
        if (!this.lineReader.isReading()) return;
        this.lineReader.callWidget(LineReader.REDRAW_LINE);
        this.lineReader.callWidget(LineReader.REDISPLAY);
    }
}
