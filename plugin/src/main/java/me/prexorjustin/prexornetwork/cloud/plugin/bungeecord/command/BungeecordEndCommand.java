package me.prexorjustin.prexornetwork.cloud.plugin.bungeecord.command;

import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BungeecordEndCommand extends Command {

    public BungeecordEndCommand() {
        super("end");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        CloudAPI.getInstance().getCloudService().shutdown();
    }
}
