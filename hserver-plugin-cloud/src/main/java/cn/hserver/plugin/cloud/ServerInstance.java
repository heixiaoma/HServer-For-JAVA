package cn.hserver.plugin.cloud;

import java.util.HashMap;
import java.util.Map;

public class ServerInstance {
    /**
     * instance ip.
     */
    private String ip;

    /**
     * instance port.
     */
    private int port;

    /**
     * instance weight.
     */
    private double weight = 1.0D;

    /**
     * instance health status.
     */
    private boolean healthy = true;
    /**
     * Service information of instance.
     */
    private String serviceName;

    /**
     * user extended attributes.
     */
    private Map<String, String> metadata = new HashMap<>();

    public ServerInstance() {
    }

    public ServerInstance(String ip, int port, double weight, boolean healthy, String serviceName, Map<String, String> metadata) {
        this.ip = ip;
        this.port = port;
        this.weight = weight;
        this.healthy = healthy;
        this.serviceName = serviceName;
        this.metadata = metadata;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getEq() {
        return this.getIp()+getPort()+getServiceName()+isHealthy();
    }

}
