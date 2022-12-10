package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Common.Response;
import ES.Dao.ScholarDao;
import ES.Entity.AdmissionApplication;
import ES.Ret.ScholarRet;
import ES.Service.ScholarService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ScholarServiceImpl implements ScholarService {

    @Autowired
    ScholarDao scholarDao;

    @Autowired
    EsUtileService esUtileService = new EsUtileService();

    @Override
    public Response<Object> scholarPortal(String researcher_id) {
        JSONObject jsonObject = esUtileService.queryDocById("researcher", researcher_id);
        String s = jsonObject.getString("R_UID");
        boolean flag = true;
        if (s == null) {
            flag = false;
        }
        return Response.success("门户信息如下:", new ScholarRet(jsonObject, flag));
    }

    @Override
    public Response<Object> selectResearcherByNameAndInstitute(String researcher_name, String institute) throws IOException {
        Map<String,Object> map = new HashMap<>();
        map.put("Rname",researcher_name);
        map.put("Rinstitute",institute);
        PageResult<JSONObject> t = esUtileService.conditionSearch("researcher",100,20,"",map,null,null,null);
        return Response.success("匹配的学者如下:",t);
    }

    @Override
    public Response<Object> applyPortal(AdmissionApplication admissionApplication){
        if (scholarDao.applyPortal(admissionApplication) > 0){
            return Response.success("申请已提交!",admissionApplication);
        }
        return Response.fail("申请失败!");
    }
}
