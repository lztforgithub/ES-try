package ES.Dao;

import ES.Entity.AdmissionApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdmissionApplicationDao {
    List<AdmissionApplication> getList0();
    List<AdmissionApplication> getList1();

    int update(@Param("aa_id") String aa_id, @Param("acc") int acc, @Param("opinion")String opinion);

    AdmissionApplication selectById(String aa_id);
}
