package ES.Dao;

import ES.Entity.ToEmail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ToEmailDao {
    ToEmail selectByEmail(String to);

    void insertEmail(ToEmail toEmail);

    void updateEmail(ToEmail toEmail);
}
