package cc.xiaoxu.cloud.api.demo.bean;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Integer code;

	@Getter
	@Setter
	private String message;

	@Getter
	@Setter
	private T data;

	public static <T> R<T> ok() {
		return restResult(null, 0, null);
	}

	public static <T> R<T> ok(T data) {
		return restResult(data, 0, null);
	}

	public static <T> R<T> ok(T data, String message) {
		return restResult(data, 0, message);
	}

	public static <T> R<T> failed() {
		return restResult(null, 1, null);
	}

	public static <T> R<T> failed(String message) {
		return restResult(null, 1, message);
	}

	public static <T> R<T> failed(T data) {
		return restResult(data, 1, null);
	}

	public static <T> R<T> failed(Integer code, String message) {
		return restResult(null, code, message);
	}

	public static <T> R<T> failed(T data, Integer code, String message) {
		return restResult(data, code, message);
	}

	static <T> R<T> restResult(T data, Integer code, String message) {
		R<T> apiResult = new R<>();
		apiResult.setCode(code);
		apiResult.setData(data);
		apiResult.setMessage(message);
		return apiResult;
	}

}
