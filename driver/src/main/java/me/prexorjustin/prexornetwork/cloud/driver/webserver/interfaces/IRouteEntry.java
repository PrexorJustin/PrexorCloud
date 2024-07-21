package me.prexorjustin.prexornetwork.cloud.driver.webserver.interfaces;

public interface IRouteEntry {

    String channelRead();

    void channelWrite(String option);

    String readROUTE();

    void channelUpdate(String update);
}
