package me.prexorjustin.prexornetwork.cloud.plugin.velocity.command;

import com.velocitypowered.api.command.SimpleCommand;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;

public class VelocityEndCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        CloudAPI.getInstance().getCloudService().shutdown();
    }

}
