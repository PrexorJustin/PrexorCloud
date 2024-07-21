package me.prexorjustin.prexornetwork.cloud.api.node.sync;

import me.prexorjustin.prexornetwork.cloud.api.node.interfaces.ICloudNode;
import me.prexorjustin.prexornetwork.cloud.api.node.interfaces.ICloudNodePool;

import java.util.ArrayList;

public class NodePool extends ICloudNodePool {

    public NodePool(ArrayList<ICloudNode> connectedNodes) {
        super(connectedNodes);
    }

}