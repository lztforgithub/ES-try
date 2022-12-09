package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.Response;
import ES.Ret.ScholarRet;
import ES.Service.ScholarService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScholarServiceImpl implements ScholarService {

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
}
