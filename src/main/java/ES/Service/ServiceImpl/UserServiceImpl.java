package ES.Service.ServiceImpl;

import ES.Common.Response;
import ES.Dao.UserDao;
import ES.Entity.User;
import ES.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.event.WindowStateListener;

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
}
