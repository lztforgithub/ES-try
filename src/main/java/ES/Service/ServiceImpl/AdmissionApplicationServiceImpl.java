package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Common.Response;
import ES.Dao.AdmissionApplicationDao;
import ES.Dao.UserDao;
import ES.Entity.AdmissionApplication;
import ES.Ret.BaseRet;
import ES.Service.AdmissionApplicationService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdmissionApplicationServiceImpl implements AdmissionApplicationService {

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
        Timestamp timestamp = new Timestamp(new Date().getTime());
        if (admissionApplicationDao.update(aa_id,acc,opinion,timestamp)>0){
            if (acc==1){
                //update学者门户
                JSONObject jsonObject = esUtileService.queryDocById("researcher",admissionApplication.getAA_RID());
                jsonObject.put("r_UID",admissionApplication.getAA_UID());
                jsonObject.put("rverifytime",new Time(new Date().getTime()));
                jsonObject.put("rcustomconcepts",admissionApplication.getAAinterestedareas());
                //jsonObject.put("Rinstitute",admissionApplication.getInstitution());
                jsonObject.put("rcontact",admissionApplication.getAAemail());
                if (admissionApplication.getAAhomepage()!=null) {
                    //可能为空
                    jsonObject.put("rpersonalPage", admissionApplication.getAAhomepage());
                }
                jsonObject.put("rgateinfo",admissionApplication.getAAintroduction());

                esUtileService.updateDoc("researcher",admissionApplication.getAA_RID(),jsonObject);

            }
            return Response.success("审核成功");
        }
        return Response.fail("审核失败!");
    }

    @Override
    public Response<Object> RUID() throws IOException {
        PageResult<JSONObject> t;
        Map<String,Object> map = new HashMap<>();
        map.put("r_UID","*");
        t = esUtileService.conditionSearch("researcher",100,20,"",map,null,null,null);
        return Response.success("已入驻学者名单如下:",t);
    }


    @Override
    public Response<Object> base() throws IOException {
        int userSum = admissionApplicationDao.countUser();

        PageResult<JSONObject> t;
        Map<String,Object> map = new HashMap<>();
        map.put("r_UID","*");
        t = esUtileService.conditionSearch("researcher",100,20,"",map,null,null,null);
        int iScholarSum = (int) t.getTotal();

        t = esUtileService.conditionSearch("researcher",100,20,"",null,null,null,null);
        int scholarSum = (int) t.getTotal();

        t = esUtileService.conditionSearch("concept",100,20,"",null,null,null,null);
        int fieldSum = (int) t.getTotal();

        t = esUtileService.conditionSearch("works",100,20,"",null,null,null,null);
        int paperSum = (int) t.getTotal();

        t = esUtileService.conditionSearch("institutions",100,20,"",null,null,null,null);
        int insSum = (int) t.getTotal();

        BaseRet baseRet = new BaseRet(userSum,iScholarSum,scholarSum,fieldSum,paperSum,insSum);

        return Response.success("平台基本信息如下:",baseRet);
    }
}
