package me.prexorjustin.prexornetwork.cloud.api.node.interfaces;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ICloudNode {

    private String nodeName, address;

}
