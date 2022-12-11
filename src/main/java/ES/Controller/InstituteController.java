package ES.Controller;

import ES.Common.Response;
import ES.Service.InstituteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class InstituteController {
    @Autowired
    InstituteService instituteService;
    @PostMapping("/institute/scholarlist")
    public Response<Object> getScholarList(HttpServletRequest request, @RequestBody Map<String, String> map)
    {
        String IID = map.get("IID");
        return instituteService.getScholarList(IID);
    }

    @PostMapping("/institute/info")
    public Response<Object> getInstitutionInfo(HttpServletRequest request, @RequestBody Map<String, String> map)
    {
        String IID = map.get("IID");
        return instituteService.getInstitutionInfo(IID);
    }
}
