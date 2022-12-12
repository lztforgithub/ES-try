package ES.Service;

import ES.Common.Response;
import ES.Entity.User;

public interface UserService {
    Response<Object> register(String username, String pwd, String email);

    Response<Object> login(String username, String pwd);

    Response<Object> getEmail(String uid);

    Response<Object> getPassword(String uid);

    Response<Object> setInfos(String uid, String password, String email);

    User selectByEmail(String email);

    Response<Object> personInfo(String uid);

    Response<Object> editInfo(String uid, String ufield, String uinterest);
}
