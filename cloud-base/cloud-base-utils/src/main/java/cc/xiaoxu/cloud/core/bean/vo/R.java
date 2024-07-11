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

    /**
     * 使用系统内置数据构造
     * @param rEnum 系统内置数据
     */
    public R(REnum rEnum) {
        this.respMsg = rEnum.getIntroduction();
        this.respCode = rEnum.getCode();
        this.success = this.getRespCode() <= 19999;
    }

    /**
     * 禁止空参实例化
     */
    private R() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * 成功
     * @param t 数据
     * @return R
     * @param <T> 类型
     */
    public static <T> R<T> success(T t) {

        R<T> respVo = new R<>(REnum.SUCCESS);
        respVo.setBody(t);
        return respVo;
    }

    /**
     * 成功，空参
     * @return R
     * @param <T> 类型
     */
    public static <T> R<T> success() {

        return success(null);
    }

    /**
     * 失败
     * @param msg 失败原因
     * @return R
     * @param <T> String
     */
    public static <T> R<T> fail(String msg) {

        R<T> respVo = new R<>(REnum.CUSTOM);
        respVo.setRespMsg(msg);
        return respVo;
    }

    /**
     * 使用系统内置数据
     * @param rEnum 内置数据
     * @return R
     * @param <T> String
     */
    public static <T> R<T> fail(REnum rEnum) {

        return fail(rEnum, rEnum.getIntroduction());
    }

    /**
     * 使用系统内置数据，附加自定义提示语
     * @param rEnum 内置数据
     * @param msg 提示语
     * @return R
     * @param <T> String
     */
    public static <T> R<T> fail(REnum rEnum, String msg) {

        R<T> respVo = new R<>(rEnum);
        respVo.setRespMsg(msg);
        return respVo;
    }

    /**
     * 失败
     * @param code 失败code
     * @param msg 原因
     * @return R
     * @param <T> 类型
     */
    public static <T> R<T> fail(int code, String msg) {

        return new R<>(code, msg, null, code <= 19999);
    }
}