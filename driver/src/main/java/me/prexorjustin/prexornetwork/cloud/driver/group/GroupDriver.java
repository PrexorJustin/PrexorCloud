package me.prexorjustin.prexornetwork.cloud.driver.group;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.prexorjustin.prexornetwork.cloud.driver.Driver;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.group.CloudGroupCreateEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.group.CloudGroupDeleteEvent;
import me.prexorjustin.prexornetwork.cloud.driver.event.events.group.CloudGroupUpdateEditEvent;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.group.interfaces.IGroupDriver;
import me.prexorjustin.prexornetwork.cloud.driver.terminal.enums.Type;
import me.prexorjustin.prexornetwork.cloud.driver.timer.TimerBase;
import me.prexorjustin.prexornetwork.cloud.driver.timer.utils.TimeUtil;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.group.GroupList;
import me.prexorjustin.prexornetwork.cloud.networking.NettyDriver;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group.PacketOutGroupCreate;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group.PacketOutGroupDelete;
import me.prexorjustin.prexornetwork.cloud.networking.packet.packets.out.service.group.PacketOutGroupEdit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                GroupList groupList = (GroupList) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute(WebServer.Routes.GROUP_GENERAL.getRoute()), GroupList.class);
                groupList.getGroups().add(group.getName());

            }

            new ConfigDriver("./local/groups/" + group.getName() + ".json").save(group);
            Driver.getInstance().getTerminalDriver().log(
                    Type.SUCCESS,
                    Driver.getInstance().getLanguageDriver().getLanguage().getMessage("group-created")
                            .replace("%group%", group.getName())
            );
        }
    }

    @Override
    public void delete(String group) {
        if (!find(group)) return;

        GroupList groupList = (GroupList) new ConfigDriver().convert(Driver.getInstance().getWebServer().getRoute(WebServer.Routes.GROUP_GENERAL.getRoute()), GroupList.class);
        groupList.getGroups().removeIf(s -> s.equals(group));

        Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudGroupDeleteEvent(group));
        NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutGroupDelete(group));

        Driver.getInstance().getWebServer().updateRoute(WebServer.Routes.GROUP_GENERAL.getRoute(), new ConfigDriver().convert(groupList));
        Driver.getInstance().getWebServer().removeRoute(WebServer.Routes.GROUP.getRoute() + group);

        new TimerBase().schedule(new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                Files.delete(Paths.get("./local/groups/" + group + ".json"));
            }
        }, 10, TimeUtil.SECONDS);

    }

    @SneakyThrows
    @Override
    public ArrayList<Group> getAll() {
        return getAllStrings().stream().map(this::load).collect(Collectors.toCollection(ArrayList::new));
    }

    @SneakyThrows
    @Override
    public ArrayDeque<String> getAllStrings() {
        ArrayDeque<String> groups = new ArrayDeque<>();

        try (Stream<Path> stream = Files.list(Paths.get("./local/groups"))) {
            stream.forEach(path -> groups.add(path.toFile().getName().split(".json")[0]));
        }

        return groups;
    }

    @Override
    public ArrayList<Group> getByNode(String node) {
        return getAll().stream().filter(group -> group.getStorage().getRunningNode().equalsIgnoreCase(node)).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void update(Group group) {
        if (!find(group.getName())) return;

        Driver.getInstance().getMessageStorage().getEventDriver().executeEvent(new CloudGroupUpdateEditEvent(group.getName()));
        NettyDriver.getInstance().getNettyServer().sendToAllAsynchronous(new PacketOutGroupEdit(group.getName()));
        new ConfigDriver("./local/groups/" + group.getName() + ".json").save(group);
    }
}
