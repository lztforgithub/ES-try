package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Common.Response;
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
        List<String> CID = new ArrayList<>();
        List<String> Cname = new ArrayList<>();
        Object q = jsonObject.get("VconceptIDs");
        CID = castList(q,String.class);

        for (String i: CID){
            JSONObject t = esUtileService.queryDocById("concept",i);
            Cname.add(t.getString("Cname"));
        }

        jsonObject.put("Cname",Cname);
        return Response.success("出版物信息如下:",
                jsonObject);
    }

    @Override
    public Response<Object> paper(String venue_id) throws IOException {
        Map<String,Object> map = new HashMap<>();
        map.put("P_VID",venue_id);
        PageResult<JSONObject> t = esUtileService.conditionSearch("works",100,20,"",map,null,null,null);
        return Response.success("出版物论文如下:",t);
    }




    public static <T> List<T> castList(Object obj, Class<T> clazz)
    {
        List<T> result = new ArrayList<T>();
        if(obj instanceof List<?>)
        {
            for (Object o : (List<?>) obj)
            {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }


}
