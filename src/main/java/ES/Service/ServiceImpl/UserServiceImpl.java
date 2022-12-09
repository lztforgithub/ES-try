package ES.Service.ServiceImpl;

import ES.Common.JwtUtil;
import ES.Common.Response;
import ES.Dao.UserDao;
import ES.Entity.User;
import ES.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.event.WindowStateListener;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    @Override
    public Response<Object> register(String username, String pwd, String email, String bio){
        User user = new User(username,pwd,email,bio);
        if (userDao.insertUser(user)>0){
            return Response.success("注册成功",user);
        }
        return Response.fail("注册失败");
    }

    @Override
    public Response<Object> login(String username, String pwd){
        User user = userDao.selectByUsername(username);
        if (user == null){
            return Response.fail("用户名不存在");
        }
        if (user.getPasswd().equals(pwd)){
            //准备存放在IWT中的自定义数据
            Map<String, Object> info = new HashMap<>();
            info.put("type", user.getType());
            info.put("username", username);

            //生成JWT字符串
            String token = JwtUtil.sign(user.getUser_id(), info);
            return Response.success("登录成功",token);
        }
        return Response.fail("密码错误");
    }
}
