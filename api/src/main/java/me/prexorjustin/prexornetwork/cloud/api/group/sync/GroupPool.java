package me.prexorjustin.prexornetwork.cloud.api.group.sync;

import lombok.NoArgsConstructor;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.WebServer;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.group.GroupList;

import java.util.ArrayDeque;
import java.util.ArrayList;

@NoArgsConstructor
public class GroupPool {

    public GroupList getGroupList() {
        return (GroupList) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get(WebServer.Routes.GROUP_GENERAL.getRoute()), GroupList.class);
    }

    public ArrayDeque<String> getGroupNames() {
        return getGroupList().getGroups();
    }

    public ArrayList<Group> getGroups() {
        ArrayList<Group> groups = new ArrayList<>();

        getGroupList().getGroups().forEach(groupName -> groups.add((Group) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudgroup/" + groupName), Group.class)));

        return groups;
    }

    public Group getGroup(String groupName) {
        return getGroups().stream().filter(group -> group.getName().equalsIgnoreCase(groupName)).findFirst().orElse(null);
    }
}
