package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.Response;
import ES.Document.ResearcherDoc;
import ES.Service.InstituteService;
import ES.storage.InstitutionStorage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static ES.Common.EsUtileService.castList;

@Service
public class InstituteServiceImpl implements InstituteService {
    @Autowired
    EsUtileService esUtileService;
    @Override
    public Response<Object> getScholarList(String iid) {
        JSONObject jsonObject = esUtileService.queryDocById("institutions", iid);
        if (jsonObject==null){
            return Response.fail("IID错误!");
        }
        List<String> RID = new ArrayList<>();
        List<JSONObject> RInfo = new ArrayList<>();
        Object q = jsonObject.get("iresearchers");
        RID = castList(q,String.class);

        for (String i: RID){
            JSONObject t = esUtileService.queryDocById("researcher",i);
            RInfo.add(t);
        }
        return Response.success("学者信息如下:",
                JSON.toJSON(RInfo));
    }

    @Override
    public Response<Object> getInstitutionInfo(String iid) {
        JSONObject jsonObject = esUtileService.queryDocById("institutions", iid);
        if (jsonObject==null){
            InstitutionStorage institutionStorage = new InstitutionStorage();
            institutionStorage.storeInstitution("http://api.openalex.org/institutions/"+iid);
            jsonObject = esUtileService.queryDocById("institutions", iid);
        }
        int IschNum = jsonObject.getJSONArray("iresearchers").size();
        jsonObject.put("IschNum", IschNum);
        return Response.success("机构信息如下:",
                jsonObject);
    }
}
