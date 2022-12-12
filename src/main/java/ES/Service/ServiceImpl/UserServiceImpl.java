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
            return Response.success("登录成功",token);
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
        return Response.fail("还没写");
    }

}
