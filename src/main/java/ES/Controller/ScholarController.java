package ES.Controller;

import ES.Common.EsUtileService;
import ES.Common.Response;
import ES.Ret.ScholarRet;
import ES.Service.ScholarService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class ScholarController {

    @Autowired
    ScholarService scholarService;

    @PostMapping("/scholarPortal")
    public Response<Object> scholarPortal(HttpServletRequest request, @RequestBody Map<String, String> map){
        String researcher_id = map.get("RID");
        return scholarService.scholarPortal(researcher_id);
    }
}
