package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.Response;
import ES.Service.VenueService;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return Response.success("出版物信息如下:",
                jsonObject);
    }

}
