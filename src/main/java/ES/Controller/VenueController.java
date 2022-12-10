package ES.Controller;

import ES.Common.Response;
import ES.Service.VenueService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class VenueController {

    @Autowired
    VenueService venueService;

    //查看出版物详细信息
    @PostMapping("/venue/view")
    public Response<Object> view(HttpServletRequest request, @RequestBody Map<String, String> map){
        String venue_id = map.get("VID");
        return venueService.view(venue_id);
    }

    //查看出版物的论文
    @PostMapping("/venue/paper")
    public Response<Object> paper(HttpServletRequest request, @RequestBody Map<String, String> map){
        String venue_id = map.get("VID");
        return venueService.paper(venue_id);
    }
}
