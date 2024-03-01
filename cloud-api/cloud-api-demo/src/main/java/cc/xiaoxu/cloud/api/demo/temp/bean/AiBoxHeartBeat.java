package cc.xiaoxu.cloud.api.demo.temp.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiBoxHeartBeat {
    @JsonProperty("DeviceId")
    private String deviceId;
    @JsonProperty("IsNormal")
    private Boolean isNormal;
    @JsonProperty("VideoId")
    private String videoId;

    public AiBoxHeartBeat() {
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getIsNormal() {
        return this.isNormal;
    }

    public void setIsNormal(Boolean isNormal) {
        this.isNormal = isNormal;
    }

    public String getVideoId() {
        return this.videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
