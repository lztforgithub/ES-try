package ES.Service.ServiceImpl;

import ES.Common.Response;
import ES.Service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public Response<Object> register(String username, String pwd, String email){
        return Response.fail("G");
    }
}
