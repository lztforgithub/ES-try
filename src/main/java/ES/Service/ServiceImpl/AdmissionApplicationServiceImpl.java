package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.Response;
import ES.Dao.AdmissionApplicationDao;
import ES.Entity.AdmissionApplication;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Service
public class AdmissionApplicationServiceImpl implements ES.Service.AdmissionApplicationService {

    @Autowired
    AdmissionApplicationDao admissionApplicationDao;
    @Autowired
    EsUtileService esUtileService = new EsUtileService();

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
        AdmissionApplication admissionApplication = null;
        if (acc==1){
            admissionApplication = admissionApplicationDao.selectById(aa_id);
        }
        if (admissionApplicationDao.update(aa_id,acc,opinion)>0){
            if (acc==1){
                //update学者门户
                JSONObject jsonObject = esUtileService.queryDocById("researcher",admissionApplication.getRid());
                jsonObject.put("R_UID",admissionApplication.getUid());
                jsonObject.put("Rverifytime",new Time(new Date().getTime()));
                jsonObject.put("Rcustomconcepts",admissionApplication.getInterestedareas());
                //jsonObject.put("Rinstitute",admissionApplication.getInstitution());
                jsonObject.put("Rcontact",admissionApplication.getEmail());
                jsonObject.put("RpersonalPage",admissionApplication.getHomepage()); //可能为空
                jsonObject.put("Rgateinfo",admissionApplication.getIntroduction());

                esUtileService.updateDoc("researcher",admissionApplication.getRid(),jsonObject);

            }
            return Response.success("审核成功");
        }
        return Response.fail("审核失败!");
    }
}
