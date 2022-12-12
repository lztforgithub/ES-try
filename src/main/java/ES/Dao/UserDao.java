package ES.Dao;

import ES.Entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;

@Mapper
public interface UserDao {
    int insertUser(User user);
    User selectByUsername(String username);

    String getEmail(String uid);

    String getPassword(String uid);

    void setPasswordAndEmail(String uid, String password, String email);

    void setPassword(String uid, String password);

    void setEmail(String uid, String email);

    User selectByEmail(String email);

    User selectById(String uid);

    int update(@Param("UID") String uid,
               @Param("Uavatar") String uavatar,
               @Param("Ufield") String ufield,
               @Param("Uinterest") String uinterest);
}
