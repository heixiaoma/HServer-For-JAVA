package cn.hserver.core.server.context;


public class ServerConfig {
    private Integer taskPool;
    private Integer workerPool;
    private Integer backLog;
    private Boolean humOpen;
    private Integer humPort;
    private Integer preProtocolMaxSize;
    private String trackExtPackages;
    private String trackNoPackages;
    private String appName;
    private String ports;
    private String persistPath;
    private Boolean track;
    private String logbackName;
    private String log;
    private String ioMode;

    public String getIoMode() {
        return ioMode;
    }

    public void setIoMode(String ioMode) {
        this.ioMode = ioMode;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLogbackName() {
        return logbackName;
    }

    public void setLogbackName(String logbackName) {
        this.logbackName = logbackName;
    }

    public String getPorts() {
        return ports;
    }

    public void setPorts(String ports) {
        this.ports = ports;
    }

    public Integer getTaskPool() {
        return taskPool;
    }

    public void setTaskPool(Integer taskPool) {
        this.taskPool = taskPool;
    }

    public Integer getWorkerPool() {
        return workerPool;
    }

    public void setWorkerPool(Integer workerPool) {
        this.workerPool = workerPool;
    }

    public Integer getBackLog() {
        return backLog;
    }

    public void setBackLog(Integer backLog) {
        this.backLog = backLog;
    }

    public Boolean getHumOpen() {
        return humOpen;
    }

    public void setHumOpen(Boolean humOpen) {
        this.humOpen = humOpen;
    }

    public Integer getHumPort() {
        return humPort;
    }

    public void setHumPort(Integer humPort) {
        this.humPort = humPort;
    }

    public Integer getPreProtocolMaxSize() {
        return preProtocolMaxSize;
    }

    public void setPreProtocolMaxSize(Integer preProtocolMaxSize) {
        this.preProtocolMaxSize = preProtocolMaxSize;
    }

    public String getTrackExtPackages() {
        return trackExtPackages;
    }

    public void setTrackExtPackages(String trackExtPackages) {
        this.trackExtPackages = trackExtPackages;
    }

    public String getTrackNoPackages() {
        return trackNoPackages;
    }

    public void setTrackNoPackages(String trackNoPackages) {
        this.trackNoPackages = trackNoPackages;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPersistPath() {
        return persistPath;
    }

    public void setPersistPath(String persistPath) {
        this.persistPath = persistPath;
    }

    public Boolean getTrack() {
        return track;
    }

    public void setTrack(Boolean track) {
        this.track = track;
    }
}
