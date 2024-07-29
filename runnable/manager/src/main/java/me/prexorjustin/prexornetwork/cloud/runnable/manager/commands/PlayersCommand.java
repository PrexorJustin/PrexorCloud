package me.prexorjustin.prexornetwork.cloud.runnable.manager.commands;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.cloudplayer.CloudPlayerRestCache;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.storage.uuid.UUIDDriver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandInfo;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.RestDriver;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.player.PlayerGeneral;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerConnect;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerKick;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.player.api.PacketOutAPIPlayerMessage;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;

import java.util.*;
import java.util.concurrent.TimeUnit;

@CommandInfo(command = "cloudplayers", description = "command-player-description", aliases = {"players", "cp"})
public class PlayersCommand extends CommandAdapter {

    @Override
    public void performCommand(CommandAdapter command, String[] args) {
        switch (args.length) {
            case 0 -> sendHelp();

            case 1 -> {
                if (args[0].equalsIgnoreCase("list")) {
                    PlayerGeneral general = (PlayerGeneral) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/general"), PlayerGeneral.class);
                    if (!general.getPlayers().isEmpty()) {
                        ArrayList<String> sortedPlayers = general.getPlayers();
                        Collections.sort(sortedPlayers);
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < sortedPlayers.size(); i++) {
                            String player = sortedPlayers.get(i);
                            CloudPlayerRestCache cache = (CloudPlayerRestCache) (new RestDriver()).convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/" + player), CloudPlayerRestCache.class);
                            builder.append(cache.getName()).append(" (").append(cache.getUuid()).append(")");
                            if (i < sortedPlayers.size() - 1) builder.append(", ");
                        }
                        Driver.getInstance().getTerminalDriver().log(Type.COMMAND, builder.toString());
                    } else {
                        Driver.getInstance().getTerminalDriver().log(
                                Type.COMMAND,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-no-player-online")
                        );
                    }

                    return;
                }

                sendHelp();
            }

            case 2 -> {
                String username = args[1];

                switch (args[0].toLowerCase()) {
                    case "op", "deop" -> {
                        PlayerGeneral general = (PlayerGeneral) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/general"), PlayerGeneral.class);
                        if (general.getPlayers().stream().anyMatch(s -> s.equalsIgnoreCase(Objects.requireNonNull(UUIDDriver.getUUID(username)).toString()))) {
                            CloudPlayerRestCache player = (CloudPlayerRestCache) (new RestDriver()).convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/" + UUIDDriver.getUUID(username)), CloudPlayerRestCache.class);
                            PrexorCloudManager.serviceDriver.getService(player.getService()).handleExecute(
                                    args[0].equalsIgnoreCase("op")
                                            ? "op " + player.getName()
                                            : "deop " + player.getName()
                            );

                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage(
                                            args[0].equalsIgnoreCase("op")
                                                    ? "command-player-op"
                                                    : "command-player-deop"
                                    ));
                        } else {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-not-found")
                            );
                        }
                    }

                    case "info" -> {
                        PlayerGeneral general = (PlayerGeneral) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/general"), PlayerGeneral.class);
                        if (general.getPlayers().stream().anyMatch(s -> s.equalsIgnoreCase(Objects.requireNonNull(UUIDDriver.getUUID(args[0])).toString()))) {
                            CloudPlayerRestCache player = (CloudPlayerRestCache) (new RestDriver()).convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/" + UUIDDriver.getUUID(username)), CloudPlayerRestCache.class);
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND, "name: " + player.getName());
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND, "uuid: " + player.getUuid());
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND, "proxy: " + player.getProxy());
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND, "server: " + player.getService());
                            int time = Integer.parseInt(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - player.getConnectTime())));
                            Driver.getInstance().getTerminalDriver().log(Type.COMMAND, "time: " + time + " Second(s)");
                        } else {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-not-found")
                            );
                        }
                    }

                    default -> sendHelp();
                }
            }

            case 3 -> {
                String username = args[1];

                if (args[0].equalsIgnoreCase("send")) {
                    PlayerGeneral general = (PlayerGeneral) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/general"), PlayerGeneral.class);
                    if (general.getPlayers().stream().anyMatch(s -> s.equalsIgnoreCase(Objects.requireNonNull(UUIDDriver.getUUID(username)).toString()))) {
                        String service = args[2];
                        if (PrexorCloudManager.serviceDriver.getService(service) != null) {
                            CloudPlayerRestCache player = (CloudPlayerRestCache) (new RestDriver()).convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/" + UUIDDriver.getUUID(args[0])), CloudPlayerRestCache.class);
                            NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutAPIPlayerConnect(player.getName(), service));

                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-send")
                            );
                        } else {
                            Driver.getInstance().getTerminalDriver().log(
                                    Type.COMMAND,
                                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-service-service-not-found")
                                            .replace("%service%", service)
                            );
                        }
                    } else {
                        Driver.getInstance().getTerminalDriver().log(
                                Type.COMMAND,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-not-found")
                        );
                    }
                    return;
                }

                sendHelp();
            }

            default -> {
                String username = args[1];
                if (args[0].equalsIgnoreCase("sendMessage")) {
                    PlayerGeneral general = (PlayerGeneral) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/general"), PlayerGeneral.class);
                    if (general.getPlayers().stream().anyMatch(s -> s.equalsIgnoreCase(Objects.requireNonNull(UUIDDriver.getUUID(username)).toString()))) {
                        CloudPlayerRestCache player = (CloudPlayerRestCache) (new RestDriver()).convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/" + UUIDDriver.getUUID(username)), CloudPlayerRestCache.class);
                        String message = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

                        NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutAPIPlayerMessage(player.getName(), message));

                        Driver.getInstance().getTerminalDriver().log(
                                Type.COMMAND,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-message")
                        );
                    } else {
                        Driver.getInstance().getTerminalDriver().log(
                                Type.COMMAND,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-not-found")
                        );

                    }
                } else if (args[0].equalsIgnoreCase("kick")) {
                    PlayerGeneral general = (PlayerGeneral) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/general"), PlayerGeneral.class);
                    if (general.getPlayers().stream().anyMatch(s -> s.equalsIgnoreCase(username))) {
                        CloudPlayerRestCache player = (CloudPlayerRestCache) (new RestDriver()).convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/" + UUIDDriver.getUUID(username)), CloudPlayerRestCache.class);
                        String message = String.join(" ", Arrays.copyOfRange(args, 3, args.length));

                        NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutAPIPlayerKick(player.getName(), message));

                        Driver.getInstance().getTerminalDriver().log(
                                Type.COMMAND,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-kick")
                        );
                    } else {
                        Driver.getInstance().getTerminalDriver().log(
                                Type.COMMAND,
                                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-not-found")
                        );
                    }
                } else sendHelp();
            }
        }
    }

    @Override
    public ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args) {
        ArrayList<String> returns = new ArrayList<>();
        if (args.length == 0) {
            returns.add("list");
            returns.add("info");
            returns.add("kick");
            returns.add("sendMessage");
            returns.add("op");
            returns.add("deop");
            returns.add("connect");
        } else if (args.length == 1) {
            if (!args[0].equalsIgnoreCase("list")) {
                PlayerGeneral general = (PlayerGeneral) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute("/cloudplayer/general"), PlayerGeneral.class);
                general.getPlayers().forEach(s -> returns.add(UUIDDriver.getUsername(UUID.fromString(s))));
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("send")) {
            PrexorCloudManager.serviceDriver.getServices().forEach(taskedService -> returns.add(taskedService.getEntry().getServiceName()));
        }
        return returns;
    }

    private void sendHelp() {
        Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-help-1"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-help-2"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-help-3"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-help-4"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-help-5"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-help-6"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-player-help-7")
        );
    }
}
