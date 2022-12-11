package ES.Controller;

import ES.Common.EsUtileService;
import ES.Common.JwtUtil;
import ES.Common.Response;
import ES.Entity.AdmissionApplication;
import ES.Ret.ScholarRet;
import ES.Service.ScholarService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
public class ScholarController {

    @Autowired
    ScholarService scholarService;

    //学者门户
    @PostMapping("/scholarPortal")
    public Response<Object> scholarPortal(HttpServletRequest request, @RequestBody Map<String, String> map) throws IOException {
        String token = request.getHeader("token");
        String user_id;
        if (token == null){
            user_id = "";
        }
        else{
            user_id = JwtUtil.getUserId(token);
        }
        String researcher_id = map.get("RID");
        return scholarService.scholarPortal(researcher_id,user_id);
    }

    //申请入驻1，查询所有同名学者
    @PostMapping("/applyPortal1")
    public Response<Object> applyPortal1(HttpServletRequest request, @RequestBody Map<String, String> map) throws IOException {
        String researcher_name = map.get("Rname");
        String institute = map.get("Rinstitute");
        return scholarService.selectResearcherByNameAndInstitute(researcher_name,institute);
    }

    //申请入驻2，新建入驻申请
    @PostMapping("/applyPortal2")
    public Response<Object> applyPortal2(HttpServletRequest request, @RequestBody Map<String, String> map) throws IOException {
        String token = request.getHeader("token");
        String user_id= JwtUtil.getUserId(token);
        String researcher_id = map.get("RID");
        String researcher_name = map.get("Rname");
        String institute = map.get("Rinstitute");
        String contact = map.get("Rcontact");
        String interestedAreas = map.get("Rconcepts");
        String homepage = map.get("RpersonalPage");
        String introduction = map.get("Rgateinfo");
        AdmissionApplication admissionApplication = new AdmissionApplication(
                user_id,
                researcher_id,
                researcher_name,
                institute,
                contact,
                interestedAreas,
                homepage,
                introduction
        );
        return scholarService.applyPortal(admissionApplication);
    }

    //编辑门户,通过接收到的RID返回对应的值
    @PostMapping("/editPortal")
    public Response<Object> editPortal(HttpServletRequest request, @RequestBody Map<String, String> map){
        String researcher_id = map.get("RID");
        return scholarService.editPortal(researcher_id);
    }

    //编辑门户2，更改门户值
    @PostMapping("/editPortal2")
    public Response<Object> editPortal2(HttpServletRequest request, @RequestBody Map<String, String> map){
        String researcher_id = map.get("RID");
        String avatar = map.get("Ravatar");
        String contact = map.get("Rcontact");
        String interestedAreas = map.get("Rconcepts");
        String homepage = map.get("RpersonalPage");
        String introduction = map.get("Rgateinfo");
        return scholarService.editPortal2(
                researcher_id,
                avatar,
                contact,
                interestedAreas,
                homepage,
                introduction
        );
    }

}
