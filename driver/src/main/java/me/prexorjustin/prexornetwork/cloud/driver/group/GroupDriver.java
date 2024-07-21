package me.prexorjustin.prexornetwork.cloud.driver.group;

import lombok.NoArgsConstructor;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.group.CloudGroupCreateEvent;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.group.interfaces.IGroupDriver;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group.PacketOutGroupCreate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;

@NoArgsConstructor
public class GroupDriver implements IGroupDriver {

    @Override
    public Group load(String name) {
        if (find(name)) return (Group) new ConfigDriver("./local/groups/" + name + ".json").read(Group.class);

        return null;
    }

    @Override
    public String loadJson(String name) {
        if (find(name)) return new ConfigDriver().convert(load(name));

        return null;
    }

    @Override
    public boolean find(String name) {
        return Files.exists(Paths.get("./local/groups/" + name + ".json"));
    }

    @Override
    public void create(Group group) {
        if (!find(group.getName())) {
            if (!Driver.getInstance().getTemplateDriver().get().contains(group.getStorage().getTemplate())) {
                boolean isProxy = group.getGroupType().equalsIgnoreCase("PROXY");
                boolean runStatic = group.isRunStatic();

                if (group.getStorage().getRunningNode().equalsIgnoreCase("InternalNode")) {
                    Driver.getInstance().getTemplateDriver().create(group.getName(), isProxy, runStatic);
                }
            }

            Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudGroupCreateEvent(group.getName()));
            if (NettyDriver.getInstance() != null) NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(
                    new PacketOutGroupCreate(group.getName())
            );

            if (Driver.getInstance().getWebServer() != null) {

            }
        }
    }

    @Override
    public void delete(String group) {

    }

    @Override
    public ArrayList<Group> getAll() {
        return null;
    }

    @Override
    public ArrayDeque<String> getAllStrings() {
        return null;
    }

    @Override
    public ArrayList<Group> getByNode(String node) {
        return null;
    }

    @Override
    public void update(String name, Group group) {

    }
}
