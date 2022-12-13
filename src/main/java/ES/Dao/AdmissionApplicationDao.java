package ES.Dao;

import ES.Entity.AdmissionApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface AdmissionApplicationDao {
    List<AdmissionApplication> getList0();
    List<AdmissionApplication> getList1();

    int update(
            @Param("aa_id") String aa_id,
            @Param("acc") int acc,
            @Param("opinion")String opinion,
            @Param("lastUpdateTime") Timestamp lastUpdateTime
    );

    AdmissionApplication selectById(String aa_id);

    int countUser();

    String selectUnameByID(@Param("UID") String aa_uid);

    void updateUser(String aa_uid);

    int getIscholarSum(String verified);
}
