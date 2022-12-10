package ES.Dao;

import ES.Entity.AdmissionApplication;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ScholarDao {
    int applyPortal(AdmissionApplication admissionApplication);
}
