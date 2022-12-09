package ES.Service.ServiceImpl;

import ES.Common.Response;
import ES.Dao.AdmissionApplicationDao;
import ES.Entity.AdmissionApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdmissionApplicationServiceImpl implements ES.Service.AdmissionApplicationService {

    @Autowired
    AdmissionApplicationDao admissionApplicationDao;

    @Override
    public List<AdmissionApplication> getList0(){
        return admissionApplicationDao.getList0();
    }

    @Override
    public List<AdmissionApplication> getList1(){
        return admissionApplicationDao.getList1();
    }

    @Override
    public Response<Object> update(String aa_id, int acc, String opinion){
        if (admissionApplicationDao.update(aa_id,acc,opinion)>0){
            return Response.success("审核成功");
        }
        return Response.fail("审核失败!");
    }
}
