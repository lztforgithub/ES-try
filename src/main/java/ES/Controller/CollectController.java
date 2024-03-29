package ES.Controller;

import ES.Common.JwtUtil;
import ES.Common.Response;
import ES.Service.CollectService;
import jdk.javadoc.doclet.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static ES.Common.EsUtileService.castList;

@RestController
public class CollectController {
    @Autowired
    CollectService collectService;

    //获取用户所有的收藏夹
    @RequestMapping("/user/viewCollect")
    private Response<Object> viewCollect(HttpServletRequest request){
        String token = request.getHeader("token");
        String user_id = JwtUtil.getUserId(token);
        return collectService.viewCollect(user_id);
    }

    //获取论文所在收藏夹
    @PostMapping("/user/viewPaperCollect")
    private Response<Object> viewPaperCollect(HttpServletRequest request, @RequestBody Map<String, String> map){
        String token = request.getHeader("token");
        String user_id = JwtUtil.getUserId(token);
        String paper_id = map.get("PID");
        return collectService.viewPaperCollect(user_id,paper_id);
    }

    //新建|修改论文所在收藏夹
    @PostMapping("/user/CollectPaper")
    private Response<Object> CollectPaper(HttpServletRequest request, @RequestBody Map<String, String> map){
        String token = request.getHeader("token");
        String user_id = JwtUtil.getUserId(token);
        String paper_id = map.get("PID");
        String q = map.get("CTID");
        List<String> collect_id = List.of(q.split(","));
        return collectService.CollectPaper(user_id,paper_id,collect_id);
    }

    //新建收藏夹
    @PostMapping("/user/AddCollect")
    private Response<Object> AddCollect(HttpServletRequest request, @RequestBody Map<String, String> map){
        String token = request.getHeader("token");
        String user_id = JwtUtil.getUserId(token);
        String collect_name = map.get("CTname");
        return collectService.AddCollect(user_id,collect_name);
    }

    //获取收藏夹下的所有文件
    @RequestMapping("/user/viewCollectPaper")
    private Response<Object> viewCollectPaper(HttpServletRequest request){
        String token = request.getHeader("token");
        String user_id = JwtUtil.getUserId(token);
        return collectService.viewCollectPaper(user_id);
    }

    //月老师要求的取消收藏接口
    @PostMapping("/user/CancelCollect")
    private Response<Object> CancelCollect(HttpServletRequest request, @RequestBody Map<String, String> map){
        String token = request.getHeader("token");
        String user_id = JwtUtil.getUserId(token);
        String cid = map.get("cid");
        String pid = map.get("pid");
        return collectService.CancelCollect(cid,pid);
    }

}
