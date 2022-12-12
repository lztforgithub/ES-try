package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Common.Response;
import ES.Ret.VConcepts;
import ES.Service.VenueService;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ES.Common.EsUtileService.castList;

@Service
public class VenueServiceImpl implements VenueService {

    @Autowired
    EsUtileService esUtileService = new EsUtileService();

    @Override
    public Response<Object> view(String venue_id){
        JSONObject jsonObject = esUtileService.queryDocById("venue",venue_id);
        if (jsonObject==null){
            return Response.fail("VID错误!");
        }
        //return Response.success("测试",jsonObject);
        List<String> CID = new ArrayList<>();
        List<VConcepts> VC = new ArrayList<>();
        Object q = jsonObject.get("vconceptIDs");
        CID = castList(q,String.class);

        //return Response.success("测试",CID);

        for (String i: CID){
            JSONObject t = esUtileService.queryDocById("concept",i);
            if (t!=null) VC.add(new VConcepts(
                    i,
                    t.getString("cname")
            ));
        }

        jsonObject.put("VConcepts",VC);
        return Response.success("出版物信息如下:",
                jsonObject);
    }

    @Override
    public Response<Object> paper(String venue_id) throws IOException {
        JSONObject jsonObject = esUtileService.queryDocById("venue",venue_id);
        if (jsonObject==null){
            return Response.fail("VID错误!");
        }

        Map<String,Object> map = new HashMap<>();
        map.put("p_VID",venue_id);
        PageResult<JSONObject> t = esUtileService.conditionSearch("works",1,100,"",map,null,null,null);
        return Response.success("出版物论文如下:",t);
    }


}
