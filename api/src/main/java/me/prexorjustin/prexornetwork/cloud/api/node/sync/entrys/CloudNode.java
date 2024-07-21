package me.prexorjustin.prexornetwork.cloud.api.node.sync.entrys;

import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.node.interfaces.ICloudNode;
import me.prexorjustin.prexornetwork.cloud.api.service.sync.entrys.CloudService;

import java.util.List;

public class CloudNode extends ICloudNode {

    public CloudNode(String nodeName, String address) {
        super(nodeName, address);
    }

    public List<CloudService> getServices() {
        return CloudAPI.getInstance().getServicePool().getConnectedServices().stream()
                .filter(cloudService -> cloudService.getGroup().getStorage().getRunningNode().equalsIgnoreCase(getNodeName()))
                .map(CloudService.class::cast)
                .toList();
    }

}
