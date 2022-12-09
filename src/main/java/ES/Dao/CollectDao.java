package ES.Dao;

import ES.Entity.CollectRecords;
import ES.Entity.Collected;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollectDao {
    List<Collected> viewCollect(String user_id);

    CollectRecords selectByCTIDandPID(String collected_id, String paper_id);
}
