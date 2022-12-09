package ES.Common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response<T> {

    private Integer code;
    private String message;
    private T data;

    public Response<Object> asObject() {
        return new Response<>(code, message, data);
    }

    public static <T> Response<T> success() {
        return new Response<>(200, "OK", null);
    }

    public static <T> Response<T> success(String message) {
        return new Response<>(200, message, null);
    }

    public static <T> Response<T> success(String message, T data) {
        return new Response<>(200, message, data);
    }

    public static <T> Response<T> fail(String message) {
        return new Response<>(500, message, null);
    }

    public static <T> Response<T> not_login() {
        return new Response<>(401, "用户未登录!", null);
    }

    public static <T> Response<T> have_login() {
        return new Response<>(201, "用户已登录!", null);
    }
}
