package ES.Controller;

import ES.Common.Response;
import ES.Service.AdmissionApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdmissionApplicationController {

    @Autowired
    AdmissionApplicationService admissionApplicationService;

    //获取未审核入驻申请列表
    @RequestMapping("AA/getList0")
    public Response<Object> getList0(){
        return Response.success("列表如下",admissionApplicationService.getList0());
    }


    //获取已审核入驻申请列表
    @RequestMapping("AA/getList1")
    public Response<Object> getList1(){
        return Response.success("列表如下",admissionApplicationService.getList1());
    }

}
