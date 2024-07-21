package me.prexorjustin.prexornetwork.cloud.driver.webserver.interfaces;

import java.net.Socket;

public interface IWebServer {

    void handleConnection() throws Exception;

    void close();

    void writeAndFlush(Socket connection, String status, String response);
}
