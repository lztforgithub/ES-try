package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Common.Response;
import ES.Dao.ScholarDao;
import ES.Entity.AdmissionApplication;
import ES.Ret.CoAuthor;
import ES.Ret.ScholarRet;
import ES.Service.ScholarService;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ES.Common.EsUtileService.castList;

@Service
public class ScholarServiceImpl implements ScholarService {

    @Autowired
    ScholarDao scholarDao;

    @Autowired
    EsUtileService esUtileService = new EsUtileService();

    @Override
    public Response<Object> scholarPortal(String researcher_id,String user_id) throws IOException {
        JSONObject jsonObject = esUtileService.queryDocById("researcher", researcher_id);
        if (jsonObject == null){
            return Response.fail("RID出错!");
        }
        String s = jsonObject.getString("r_UID");
        boolean flag = true;
        if (s == null) {
            flag = false;
        }
        if (!s.equals(user_id)){
            flag = false;
        }

        /*共著学者信息
        List<String> RCOID = new ArrayList<>();
        List<CoAuthor> RcoauthorList = new ArrayList<>();
        Object q = jsonObject.get("rcoauthor");
        RCOID = castList(q,String.class);

        CoAuthor coAuthor;
        for (String i:RCOID){
            JSONObject t = esUtileService.queryDocById("researcher",i);
            if (t!=null){
                coAuthor = new CoAuthor(
                        t.getString("rinstitute"),
                        i,
                        t.getString("rname"),
                        t.getString("ravatar")
                );
                RcoauthorList.add(coAuthor);
            }
        }
        jsonObject.put("RcoauthorList",RcoauthorList);*/

        //代表论文信息
        Map<String, Object> map = new HashMap<>();
        map.put("pauthor",researcher_id);
        PageResult<JSONObject> paper = esUtileService.conditionSearch("works",100,20,"",map,null,null,null);

        jsonObject.put("RpaperList",paper);

        return Response.success("门户信息如下:", new ScholarRet(jsonObject, flag));
    }

    @Override
    public Response<Object> selectResearcherByNameAndInstitute(String researcher_name, String institute) throws IOException {
        Map<String,Object> map = new HashMap<>();
        map.put("rname",researcher_name);
        map.put("rinstitute",institute);
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

    @Override
    public Response<Object> editPortal(String researcher_id){
        JSONObject jsonObject = esUtileService.queryDocById("researcher", researcher_id);
        if (jsonObject == null){
            return Response.fail("RID错误");
        }
        return Response.success("学者信息如下:",jsonObject);
    }
}
