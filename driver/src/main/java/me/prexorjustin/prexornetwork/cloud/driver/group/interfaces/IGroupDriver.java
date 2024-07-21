package me.prexorjustin.prexornetwork.cloud.driver.group.interfaces;

import me.prexorjustin.prexornetwork.cloud.driver.group.dummys.Group;

import java.util.ArrayDeque;
import java.util.ArrayList;

public interface IGroupDriver {

    Group load(String name);

    String loadJson(String name);

    boolean find(String name);

    void create(Group group);

    void delete(String group);

    ArrayList<Group> getAll();

    ArrayDeque<String> getAllStrings();

    ArrayList<Group> getByNode(String node);

    void update(String name, Group group);
}
