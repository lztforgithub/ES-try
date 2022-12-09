package ES.Controller;

import ES.Common.EsUtileService;
import ES.Common.JwtUtil;
import ES.Common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class SearchController {
    @Autowired
    EsUtileService esUtileService;

    @GetMapping("/author/hello")
    public Response<Object> hello() {
        return Response.success("hello world");
    }

    @GetMapping("/auth/login")
    public Response<Object> login() {
        //假设数据库中查询到了该用户，这里测试先所及生成一个UUID，作为用户的id
        String userId = UUID.randomUUID().toString();

        //准备存放在IWT中的自定义数据
        Map<String, Object> info = new HashMap<>();
        info.put("username", "tom");
        info.put("pass", "admin");

        //生成JWT字符串
        String token = JwtUtil.sign(userId, info);

        return Response.success("token:" + token);
    }
}
