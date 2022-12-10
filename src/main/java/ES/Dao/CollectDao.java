package ES.Dao;

import ES.Entity.CollectRecords;
import ES.Entity.Collected;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CollectDao {
    List<Collected> viewCollect(String user_id);

    CollectRecords selectByCTIDandPID(@Param("collect_id") String collected_id,@Param("paper_id") String paper_id);

    void deleteCollectRecords(@Param("user_id") String user_id,@Param("paper_id") String paper_id);

    int insertCollectRecords(CollectRecords collectRecords);

    int insertCollected(Collected collected);
}
