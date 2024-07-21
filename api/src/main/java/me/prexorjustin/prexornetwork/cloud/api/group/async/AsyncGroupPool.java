package me.prexorjustin.prexornetwork.cloud.api.group.async;

import lombok.NoArgsConstructor;
import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.driver.configuration.ConfigDriver;
import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;
import me.prexorjustin.prexornetwork.cloud.driver.webserver.dummys.group.GroupList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@NoArgsConstructor
public class AsyncGroupPool {

    public CompletableFuture<ArrayDeque<String>> getGroupsByName() {
        GroupList groupList = (GroupList) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudgroup/general"), GroupList.class);
        return CompletableFuture.supplyAsync(groupList::getGroups);
    }

    public CompletableFuture<ArrayList<Group>> getGroups() {
        ArrayList<Group> groups = new ArrayList<>();
        GroupList groupList = (GroupList) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudgroup/general"), GroupList.class);

        groupList.getGroups().forEach(groupName -> groups.add((Group) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudgroup/" + groupName), Group.class)));

        return CompletableFuture.supplyAsync(() -> groups);
    }

    public CompletableFuture<Group> getGroup(String group) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getGroups().get().stream().filter(group1 -> group1.getName().equalsIgnoreCase(group)).findFirst().orElse(null);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
