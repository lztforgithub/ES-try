package ES.Controller;

import ES.Common.Response;
import ES.Common.VerCodeGenerateUtil;
import ES.Entity.ToEmail;
import ES.Entity.User;
import ES.Service.ToEmailService;
import ES.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;


/**
 * TODO 邮箱验证码
 */
@RestController

public class EmailController {
    //	引入邮件接口
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ToEmailService toEmailService;

    @Autowired
    private UserService userService;


    //	获得发件人信息
    @Value("${spring.mail.username}")
    private String from;
    @PostMapping("/sendEmail")
    public Response<Object> commonEmail(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        String to = (String) map.get("to");
        User user = userService.selectByEmail(to);

        if (user != null){
            return Response.fail("邮箱已被注册！");
        }
//        创建邮件消息
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);

        //message.setTo(toEmail.getTos());
        message.setTo(to);

        message.setSubject("您本次的验证码是");

        String verCode = VerCodeGenerateUtil.generateVerCode();

        message.setText("尊敬的AceGate用户,您好:\n"
                + "\n本次注册请求的邮件验证码为:" + verCode + ",本验证码 5 分钟内有效，请及时输入。（请勿泄露此验证码）\n"
                + "\n如您未注册过  AceGate  ，请忽略该邮件。\n(这是一封通过自动发送的邮件，请不要直接回复）");
        try{
            mailSender.send(message);
        }catch (Exception e){
            return Response.fail("邮箱不合法！");
        }

        ToEmail toEmail = toEmailService.selectByEmail(to);
        if (toEmail == null){
            toEmail = new ToEmail(to, verCode);
            toEmailService.insertEmail(toEmail);
        }
        else{
            toEmail.setVercode(verCode);
            toEmail.setCode_time(new Timestamp(new Date().getTime()));
            toEmailService.updateEmail(toEmail);
        }
        return Response.success("发送成功");
    }

    @PostMapping("/sendEmail")
    public Response<Object> commonEmail2(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        String to = (String) map.get("to");

//        创建邮件消息
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);

        //message.setTo(toEmail.getTos());
        message.setTo(to);

        message.setSubject("您本次的验证码是");

        String verCode = VerCodeGenerateUtil.generateVerCode();

        message.setText("尊敬的AceGate用户,您好:\n"
                + "\n本次找回请求的邮件验证码为:" + verCode + ",本验证码 5 分钟内有效，请及时输入。（请勿泄露此验证码）\n"
                + "\n如您更改过  AceGate  的密码，请忽略该邮件。\n(这是一封通过自动发送的邮件，请不要直接回复）");
        try{
            mailSender.send(message);
        }catch (Exception e){
            return Response.fail("邮箱不合法！");
        }

        ToEmail toEmail = toEmailService.selectByEmail(to);
        if (toEmail == null){
            toEmail = new ToEmail(to, verCode);
            toEmailService.insertEmail(toEmail);
        }
        else{
            toEmail.setVercode(verCode);
            toEmail.setCode_time(new Timestamp(new Date().getTime()));
            toEmailService.updateEmail(toEmail);
        }
        return Response.success("发送成功");
    }
}
