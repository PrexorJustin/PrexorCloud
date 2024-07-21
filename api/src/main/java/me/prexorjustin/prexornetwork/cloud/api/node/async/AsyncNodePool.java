package me.prexorjustin.prexornetwork.cloud.api.node.async;

import me.prexorjustin.prexornetwork.cloud.api.node.interfaces.ICloudNode;
import me.prexorjustin.prexornetwork.cloud.api.node.interfaces.ICloudNodePool;

import java.util.ArrayList;

public class AsyncNodePool extends ICloudNodePool {

    public AsyncNodePool(ArrayList<ICloudNode> connectedNodes) {
        super(connectedNodes);
    }

}
