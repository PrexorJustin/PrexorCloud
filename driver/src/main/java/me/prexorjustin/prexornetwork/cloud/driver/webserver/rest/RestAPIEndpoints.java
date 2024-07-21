package me.prexorjustin.prexornetwork.cloud.driver.webserver.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URI;

@Getter
@AllArgsConstructor
public enum RestAPIEndpoints {

    GENERAL(URI.create("http://84.247.173.227/cloud/rest/global.json")),
    MODULES(URI.create("http://84.247.173.227/cloud/rest/modules.json")),
    SOFTWARE(URI.create("http://84.247.173.227/cloud/rest/software.json"));

    private final URI uri;

}
