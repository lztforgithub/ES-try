package ES.Controller;

import ES.Common.EsUtileService;
import ES.Common.Response;
import ES.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Repeatable;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    //注册
    @PostMapping("/register")
    public Response<Object> register(@RequestBody Map<String, String> map){
        String username,pwd,email,bio;
        username = map.get("username");
        pwd = map.get("password");
        email = map.get("email");
        bio = map.get("bio");
        return userService.register(username,pwd,email,bio);
    }

    //登录
    @PostMapping("/login")
    public Response<Object> login(@RequestBody Map<String, String> map){
        String username,pwd;
        username = map.get("username");
        pwd = map.get("password");
        return userService.login(username,pwd);
    }
}
