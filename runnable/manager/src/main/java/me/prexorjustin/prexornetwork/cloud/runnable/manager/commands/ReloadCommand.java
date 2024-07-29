package me.prexorjustin.prexornetwork.cloud.runnable.manager.commands;

import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfig;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.manager.ManagerConfigNodes;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.dummys.message.Messages;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.restapi.CloudRestAPIReloadEvent;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandAdapter;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.commands.CommandInfo;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.utils.TerminalStorageLine;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.addresses.Addresses;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.group.GroupList;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.whitelist.Whitelist;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.entry.RouteEntry;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.events.PacketOutCloudRestAPIReloadEvent;
import me.prexorjustin.prexornetwork.cloud.runnable.manager.PrexorCloudManager;

import java.util.ArrayList;
import java.util.stream.Collectors;

@CommandInfo(command = "reload", description = "command-reload-description", aliases = {"rl"})
public class ReloadCommand extends CommandAdapter {

    @Override
    public void performCommand(CommandAdapter command, String[] args) {
        if (args.length == 1) {
            NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutCloudRestAPIReloadEvent());
            Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudRestAPIReloadEvent());

            if (args[0].equalsIgnoreCase("all")) {
                Driver.getInstance().getModuleDriver().reload();

                reloadConfig();
            } else if (args[0].equalsIgnoreCase("modules")) {
                Driver.getInstance().getModuleDriver().reload();
            } else if (args[0].equalsIgnoreCase("config")) {
                reloadConfig();
            } else sendHelp();

            Driver.getInstance().getTerminalDriver().log(
                    Type.COMMAND,
                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-reload-successful")
            );
        } else sendHelp();
    }

    @Override
    public ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args) {
        ArrayList<String> returns = new ArrayList<>();
        if (args.length == 0) {
            returns.add("all");
            returns.add("config");
            returns.add("modules");
        }

        return returns;
    }

    private void reloadConfig() {
        NettyDriver.getInstance().getNettyServer().sendToAllSynchronized(new PacketOutCloudRestAPIReloadEvent());
        Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudRestAPIReloadEvent());

        Messages msg = (Messages) new ConfigDriver("./local/messages.json").read(Messages.class);
        Driver.getInstance().getWebServer().updateRoute("/message/default", new ConfigDriver().convert(msg));

        Whitelist whitelistConfig = new Whitelist();
        PrexorCloudManager.config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);
        whitelistConfig.setWhitelist(PrexorCloudManager.config.getWhitelist());
        Driver.getInstance().getWebServer().updateRoute("/default/whitelist", new ConfigDriver().convert(whitelistConfig));

        Addresses AddressesConfig = new Addresses();
        ArrayList<String> addresses = PrexorCloudManager.config.getNodes().stream().map(ManagerConfigNodes::getAddress).collect(Collectors.toCollection(ArrayList::new));
        AddressesConfig.setAddresses(addresses);
        Driver.getInstance().getWebServer().updateRoute("/default/addresses", new ConfigDriver().convert(AddressesConfig));

        GroupList groupList = new GroupList();
        groupList.setGroups(Driver.getInstance().getGroupDriver().getAllStrings());
        Driver.getInstance().getWebServer().updateRoute("/cloudgroup/general", new ConfigDriver().convert(groupList));

        Driver.getInstance().getGroupDriver().getAll().forEach(group -> {
            if (Driver.getInstance().getWebServer().getRoute("/cloudgroup/" + group.getName()) == null) {
                Driver.getInstance().getWebServer().addRoute(new RouteEntry("/cloudgroup/" + group.getName(), new ConfigDriver().convert(group)));
            } else {
                Driver.getInstance().getWebServer().updateRoute("/cloudgroup/" + group.getName(), new ConfigDriver().convert(group));
            }
        });
    }

    private void sendHelp() {
        Driver.getInstance().getTerminalDriver().log(Type.COMMAND,
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-reload-help-1"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-reload-help-2"),
                Driver.getInstance().getLanguageDriver().getLanguage().getMessage("command-reload-help-3")
        );
    }
}
