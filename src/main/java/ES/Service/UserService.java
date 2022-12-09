package ES.Service;

import ES.Common.Response;

public interface UserService {
    Response<Object> register(String username, String pwd, String email,String bio);
}
