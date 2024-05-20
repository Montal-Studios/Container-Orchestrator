package dev.tiertests;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.*;

import java.io.File;
import java.util.Set;

public class Docker {

    /**
     * Create a container with the given name
     * @param name the name of the container
     * @return the port
     */
    public static int createContainer(String name) {
        DockerClient client = Main.getDockerClient();

        String imageId = client.buildImageCmd()
                .withDockerfile(new File("../Dockerfile"))
                .withTags(Set.of("skywars-instance:latest"))
                .exec(new BuildImageResultCallback() {
                    @Override
                    public void onNext(BuildResponseItem item) {
                        super.onNext(item);
                        System.out.println(item.getStream());
                    }
                })
                .awaitImageId();

        int port = getNextPort();

        ExposedPort exposedPort = ExposedPort.tcp(getNextPort());

        PortBinding portBinding = new PortBinding(Ports.Binding.bindPort(port), exposedPort);

        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(6L * 1024 * 1024 * 1024); // 6gb
        hostConfig.withNanoCPUs(2L * 1_000_000_000); // 2 cores
        hostConfig.withPortBindings(portBinding);

        String containerId = client.createContainerCmd(imageId)
                .withName(name)
                .withHostConfig(hostConfig)
                .exec()
                .getId();

        client.startContainerCmd(containerId).exec();

        return port;
    }

    private static int getNextPort() {
        return Integer.parseInt(System.getenv("PORT")) + 1;
    }

    private static void incrementPort() {
        int port = Integer.parseInt(System.getenv("PORT"));
        System.getenv().put("PORT", String.valueOf(port + 1));
    }
}
