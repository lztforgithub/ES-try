package ES.Dao;

import ES.Entity.AdmissionApplication;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdmissionApplicationDao {
    List<AdmissionApplication> getList0();
    List<AdmissionApplication> getList1();
}
