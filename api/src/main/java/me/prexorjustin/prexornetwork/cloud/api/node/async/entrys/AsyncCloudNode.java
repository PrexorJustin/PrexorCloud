package me.prexorjustin.prexornetwork.cloud.api.node.async.entrys;

import me.prexorjustin.prexornetwork.cloud.api.CloudAPI;
import me.prexorjustin.prexornetwork.cloud.api.node.interfaces.ICloudNode;
import me.prexorjustin.prexornetwork.cloud.api.service.async.entrys.AsyncCloudService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AsyncCloudNode extends ICloudNode {

    public AsyncCloudNode(String nodeName, String address) {
        super(nodeName, address);
    }

    public CompletableFuture<List<AsyncCloudService>> getService() {
        return CompletableFuture.supplyAsync(() ->
                CloudAPI.getInstance().getAsyncServicePool().getConnectedServices().stream()
                        .filter(cloudService -> cloudService.getGroup().getStorage().getRunningNode().equalsIgnoreCase(getNodeName()))
                        .map(AsyncCloudService.class::cast)
                        .toList()
        );
    }
}
