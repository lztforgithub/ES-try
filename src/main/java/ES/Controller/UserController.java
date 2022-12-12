package ES.Controller;

import ES.Common.EsUtileService;
import ES.Common.JwtUtil;
import ES.Common.Response;
import ES.Entity.ToEmail;
import ES.Service.ToEmailService;
import ES.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Repeatable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    ToEmailService toEmailService;

    //注册
    @PostMapping("/register")
    public Response<Object> register(@RequestBody Map<String, String> map){
        String username,pwd,email;
        username = map.get("username");
        pwd = map.get("password");
        email = map.get("email");
        //验证验证码,5分钟有效
        String verCode = (String) map.get("vercode");
        ToEmail toEmail = toEmailService.selectByEmail(email);
        if (toEmail == null){
            return Response.fail("验证码未发送！");
        }
        Timestamp timestamp = toEmail.getCode_time();
        Timestamp nowtime = new Timestamp(new Date().getTime()-5*60*1000);
        if (nowtime.after(timestamp)){
            return Response.fail("验证码已过期，请重新发送！");
        }
        if (!verCode.equals(toEmail.getVercode())){
            return Response.fail("验证码错误！");
        }

        return userService.register(username,pwd,email);
    }

    //登录
    @PostMapping("/login")
    public Response<Object> login(HttpServletRequest request,@RequestBody Map<String, String> map){
        String username,pwd;
        username = map.get("username");
        pwd = map.get("password");
        return userService.login(username,pwd);
    }

    @RequestMapping("/personInfo")
    public Response<Object> personInfo(HttpServletRequest request){
        String token = request.getHeader("token");
        String uid = JwtUtil.getUserId(token);
        return userService.personInfo(uid);
    }

    @RequestMapping("/personInfo/account")
    public Response<Object> getEmail(HttpServletRequest request)
    {
        String token = request.getHeader("token");
        String uid = JwtUtil.getUserId(token);
        return userService.getEmail(uid);
    }

    @RequestMapping("/personInfo/accountedit")
    public Response<Object> getPassword(HttpServletRequest request)
    {
        String token = request.getHeader("token");
        String uid = JwtUtil.getUserId(token);
        return userService.getPassword(uid);
    }

    @PostMapping("/personInfo/accountedit2")
    public Response<Object> setInfos(HttpServletRequest request,@RequestBody Map<String, String> map)
    {
        String token = request.getHeader("token");
        String uid = JwtUtil.getUserId(token);
        String password = map.get("Upassword");
        //String email = map.get("Uemail");
        return userService.setInfos(uid, password, null);
    }

    @PostMapping("/personInfo/edit")
    public Response<Object> editInfo(HttpServletRequest request,@RequestBody Map<String,String> map){
        String token = request.getHeader("token");
        String uid = JwtUtil.getUserId(token);
        String ufield = map.get("Ufield");
        String uinterest = map.get("Uinterest");
        return userService.editInfo(uid,ufield,uinterest);
    }

}
