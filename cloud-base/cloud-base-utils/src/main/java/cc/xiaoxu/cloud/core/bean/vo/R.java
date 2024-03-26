package cc.xiaoxu.cloud.core.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class R<T> {

    /**
     * 成功
     */
    public static final int SUCCESS = 200;

    /**
     * 失败
     */
    public static final int FAIL = 500;

    @Schema(name = "返回编码", example = "0")
    public int respCode;

    @Schema(name = "返回消息", example = "操作成功")
    private String respMsg;

    @Schema(name = "响应参数 body")
    private T body;

    @Schema(name = "是否调用成功", example = "true")
    private Boolean success;

    public R(REnum rEnum) {
        this.respMsg = rEnum.getIntroduction();
        this.respCode = rEnum.getCode();
        this.success = this.getRespCode() <= 19999;
    }

    private R() {
    }

    public static <T> R<T> success(T t) {

        R<T> respVo = new R<>(REnum.SUCCESS);
        respVo.setBody(t);
        return respVo;
    }

    public static <T> R<T> success() {

        return success(null);
    }

    public static <T> R<T> fail(String msg) {

        R<T> respVo = new R<>(REnum.CUSTOM);
        respVo.setRespMsg(msg);
        return respVo;
    }

    public static <T> R<T> fail(REnum rEnum, String msg) {

        R<T> respVo = new R<>(rEnum);
        respVo.setRespMsg(msg);
        return respVo;
    }

    public static <T> R<T> fail(int code, String msg) {

        return new R<>(code, msg, null, code <= 19999);
    }

    public static <T> R<T> fail(REnum rEnum) {

        return fail(rEnum, null);
    }
}