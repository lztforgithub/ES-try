package ES.Dao;

import ES.Entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    int insertUser(User user);
    User selectByUsername(String username);

    String getEmail(String uid);

    String getPassword(String uid);
}
