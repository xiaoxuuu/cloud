package cc.xiaoxu.cloud.api.demo.webClient;

import lombok.Data;

@Data
public class ResultResponse {

    private String msg;
    private ResultDetail result;
    private int code;
    private int tdt;
    private String error;
}