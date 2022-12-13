package ES.Service.ServiceImpl;

import ES.Common.JwtUtil;
import ES.Common.Response;
import ES.Dao.UserDao;
import ES.Entity.ToEmail;
import ES.Entity.User;
import ES.Service.ToEmailService;
import ES.Service.UserService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    ToEmailService toEmailService;

    @Override
    public Response<Object> register(String username, String pwd, String email){
        if (userDao.selectByUsername(username)!=null){
            return Response.fail("用户名已存在!");
        }
        if (userDao.selectByEmail(email)!=null){
            return Response.fail("邮箱已存在!");
        }
        User user = new User(username,pwd,email);
        if (userDao.insertUser(user)>0){
            return Response.success("注册成功",user);
        }
        return Response.fail("注册失败");
    }

    @Override
    public Response<Object> login(String username, String pwd){
        User user = new User();
        user = userDao.selectByUsername(username);
        System.out.println(user);
        if (user == null){
            return Response.fail("用户名不存在");
        }
        if (user.getUpassword().equals(pwd)){
            //准备存放在IWT中的自定义数据
            Map<String, Object> info = new HashMap<>();
            info.put("type", user.getUtype());
            info.put("username", username);

            //生成JWT字符串
            String token = JwtUtil.sign(user.getUID(), info);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token",token);
            jsonObject.put("type",user.getUtype());
            return Response.success("登录成功",jsonObject);
        }
        return Response.fail("密码错误");
    }

    @Override
    public Response<Object> getEmail(String uid) {
        String email = userDao.getEmail(uid);
        if(email==null)
        {
            return Response.fail("用户不存在");
        }
        else
        {
            return Response.success("找到用户邮箱信息", email);
        }
    }

    @Override
    public Response<Object> getPassword(String uid) {
        String password = userDao.getPassword(uid);
        if(password==null)
        {
            return Response.fail("用户不存在");
        }
        else
        {
            return Response.success("找到用户密码信息", password);
        }
    }

    @Override
    public Response<Object> setInfos(String uid, String password, String email) {
        if(password!=null && email!=null)
        {
            userDao.setPasswordAndEmail(uid, password, email);
        }
        else if(password!=null && email==null)
        {
            userDao.setPassword(uid, password);
        }
        else if(email!=null && password==null)
        {
            userDao.setEmail(uid, email);
        }
        return Response.success("修改成功");
    }

    @Override
    public User selectByEmail(String email){
        return userDao.selectByEmail(email);
    }

    @Override
    public Response<Object> personInfo(String uid){
        return Response.success("个人信息如下:",userDao.selectByID(uid));
    }

    @Override
    public Response<Object> editInfo(String uid, String ufield, String uinterest){
        if (userDao.update(uid,ufield,uinterest)>0){
            return Response.success("更新成功!");
        }
        return Response.fail("更新失败!");
    }

    @Override
    public Response<Object> confiemVerCode(String email, String verCode){
        ToEmail toEmail = toEmailService.selectByEmail(email);
        if (toEmail == null){
            return Response.fail("验证码未发送！");
        }
        Timestamp timestamp = toEmail.getCode_time();
        Timestamp nowtime = new Timestamp(new Date().getTime()-5*60*1000);
        if (nowtime.after(timestamp)){
            return Response.fail("验证码已过期，请重新发送！");
        }

        System.out.println(toEmail.getVercode());
        if (!verCode.equals(toEmail.getVercode())){
            return Response.fail("验证码错误！");
        }
        return Response.success("验证码通过!可以开始找回密码。");
    }

    @Override
    public Response<Object> changePassword(String email, String password){
        if (userDao.changePassword(email,password)>0){
            return Response.success("密码找回成功!新密码为:",password);
        }
        return Response.fail("密码找回失败!");
    }

}
