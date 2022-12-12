package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Common.Response;
import ES.Dao.ScholarDao;
import ES.Entity.AdmissionApplication;
import ES.Ret.CoAuthor;
import ES.Ret.RAuthor;
import ES.Ret.ScholarRet;
import ES.Service.ScholarService;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Time;
import java.util.*;

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
        }else {
            if (!s.equals(user_id)) {
                flag = false;
            }
        }

        //共著学者信息
        List<String> RCO = new ArrayList<>();
        List<String> RIN = new ArrayList<>();
        List<RAuthor> RcoauthorList = new ArrayList<>();
        Object q = jsonObject.get("rcoauthor");
        RCO = castList(q,String.class);
        q = jsonObject.get("rcoauthorInstitute");
        RIN = castList(q,String.class);
        for (int i=0;i<RCO.size();i++){
            RcoauthorList.add(new RAuthor(RCO.get(i),RIN.get(i)));
        }

        jsonObject.put("RcoauthorList",RcoauthorList);

        //代表论文信息
        Map<String, Object> map = new HashMap<>();
        map.put("pauthor",researcher_id);
        PageResult<JSONObject> paper = esUtileService.conditionSearch("works",1,20,"",map,null,null,null);

        jsonObject.put("RpaperList",paper.getList());

        //领域名
        q = jsonObject.get("rconcepts");
        List<String> cid = new ArrayList<>();
        String cname = "";
        cid = castList(q,String.class);
        for (String i:cid){
            JSONObject p = esUtileService.queryDocById("concept",i);
            if (p!=null){
                cname = cname +","+p.getString("cname");
            }
        }
        if (cname.equals("")){
            cname = " -";
        }

        jsonObject.put("Cname",cname.substring(1));
        jsonObject.put("flag",flag);

        return Response.success("门户信息如下:", jsonObject);
    }

    @Override
    public Response<Object> selectResearcherByNameAndInstitute(String researcher_name, String institute) throws IOException {
        Map<String,Object> map = new HashMap<>();
        map.put("rname",researcher_name);
        map.put("rinstitute",institute);
        PageResult<JSONObject> t = esUtileService.conditionSearch("researcher",1,20,"",map,null,null,null);
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

    @Override
    public Response<Object> editPortal2(
            String researcher_id,
            String avatar,
            String contact,
            String interestedAreas,
            String homepage,
            String introduction){

            JSONObject jsonObject = esUtileService.queryDocById("researcher",researcher_id);
            jsonObject.put("ravatar",avatar);
            jsonObject.put("rcustomconcepts",interestedAreas);
            jsonObject.put("rcontact",contact);
        if (!homepage.equals("")) {
            //可能为空
            jsonObject.put("rpersonalPage", homepage);
        }
        jsonObject.put("rgateinfo",introduction);

        esUtileService.updateDoc("researcher",researcher_id,jsonObject);

        return Response.success("更新成功!",jsonObject);
    }
}
