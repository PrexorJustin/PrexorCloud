package me.prexorjustin.prexornetwork.cloud.driver.template.interfaces;

import java.util.ArrayList;

public interface ITemplateDriver {

    void create(String template, boolean bungee, boolean isStatic);

    void copy(String template, String directory);

    void delete(String template);

    void install(String template, boolean bungee);

    ArrayList<String> get();

}
