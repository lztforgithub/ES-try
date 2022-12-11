package ES.Service;

import ES.Common.Response;

public interface UserService {
    Response<Object> register(String username, String pwd, String email,String bio);

    Response<Object> login(String username, String pwd);

    Response<Object> getEmail(String uid);

    Response<Object> getPassword(String uid);
}
