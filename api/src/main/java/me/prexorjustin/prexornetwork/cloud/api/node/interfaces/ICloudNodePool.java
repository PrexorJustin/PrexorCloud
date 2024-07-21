package me.prexorjustin.prexornetwork.cloud.api.node.interfaces;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
public abstract class ICloudNodePool {

    private final ArrayList<ICloudNode> connectedNodes;

    public void register(ICloudNode cloudNode) {
        this.connectedNodes.add(cloudNode);
    }

}
