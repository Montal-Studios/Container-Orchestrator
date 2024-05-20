package dev.tiertests;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

import java.time.Duration;
import java.util.Map;

public class Main {
    private static final DockerClientConfig dockerConfig;
    private static final DockerHttpClient dockerHttpClient;
    private static final DockerClient dockerClient;

    static {
        dockerConfig = DefaultDockerClientConfig.createDefaultConfigBuilder().build();

        dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerConfig.getDockerHost())
                .sslConfig(dockerConfig.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        dockerClient = DockerClientImpl.getInstance(dockerConfig, dockerHttpClient);
    }

    public static void main(String[] args) throws Exception {
        Consumer.initializeConsumer();

        if (System.getenv("PORT") == null) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            Map<String, String> env = processBuilder.environment();
            env.put("PORT", "25570");

            try {
                processBuilder.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static DockerClient getDockerClient() {
        return dockerClient;
    }
}