package ES.Controller;

import ES.Common.JwtUtil;
import ES.Common.Response;
import ES.Service.AdmissionApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;
import java.util.Map;

@RestController
public class AdmissionApplicationController {

    @Autowired
    AdmissionApplicationService admissionApplicationService;

    //获取未审核入驻申请列表
    @RequestMapping("AA/getList0")
    public Response<Object> getList0(HttpServletRequest request){
        String token = request.getHeader("token");
        Map<String, Object> info = JwtUtil.getInfo(token);
        if (info.get("type").equals("admin")) {
            return Response.success("列表如下:", admissionApplicationService.getList0());
        }
        return Response.fail("无权限!");
    }


    //获取已审核入驻申请列表
    @RequestMapping("AA/getList1")
    public Response<Object> getList1(HttpServletRequest request){
        String token = request.getHeader("token");
        Map<String, Object> info = JwtUtil.getInfo(token);
        if (info.get("type").equals("admin")) {
            return Response.success("列表如下:", admissionApplicationService.getList1());
        }
        return Response.fail("无权限!");
    }

    //审核入驻申请
    @PostMapping("AA/accept")
    public Response<Object> accept(HttpServletRequest request, @RequestBody Map<String, Object> map){
        String token = request.getHeader("token");
        Map<String, Object> info = JwtUtil.getInfo(token);
        if (info.get("type").equals("admin")) {
            String AA_id = (String) map.get("AAID");
            int acc= (int) map.get("accept");
            String opinion = (String) map.get("opinion");
            return admissionApplicationService.update(AA_id,acc,opinion);
        }
        return Response.fail("无权限!");
    }

}