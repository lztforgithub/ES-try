package ES.Controller;

import ES.Common.EsUtileService;
import ES.Common.Response;
import ES.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping("/personInfo/account")
    public Response<Object> getEmail(@RequestBody Map<String, String> map)
    {
        String uid = map.get("UID");
        return userService.getEmail(uid);
    }

    @PostMapping("/personInfo/accountedit")
    public Response<Object> getPassword(@RequestBody Map<String, String> map)
    {
        String uid = map.get("UID");
        return userService.getPassword(uid);
    }

    @PostMapping("/personInfo/accountedit2")
    public Response<Object> setInfos(@RequestBody Map<String, String> map)
    {
        String uid = map.get("UID");
        String password = map.get("Upassword");
        String email = map.get("Uemail");
        return userService.setInfos(uid, password, email);
    }

}
