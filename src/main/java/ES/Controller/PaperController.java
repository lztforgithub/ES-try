package ES.Controller;

import ES.Common.JwtUtil;
import ES.Common.Response;
import ES.Service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@RestController
public class PaperController {

    @Autowired
    PaperService paperService;

    @PostMapping("/paper/Details")
    private Response<Object> getDetails(HttpServletRequest request, @RequestBody Map<String, String> map)
    {
        String pid = map.get("PID");
        return paperService.getDetails(pid);
    }

    //文献详情
    @PostMapping("/paper/view")
    private Response<Object> view(HttpServletRequest request, @RequestBody Map<String, String> map){
        String paper_id = map.get("PID");
        return paperService.view(paper_id);
    }

    //引用格式
    @PostMapping("/paper/cite")
    private Response<Object> cite(HttpServletRequest request, @RequestBody Map<String, String> map){
        String paper_id = map.get("PID");
        return paperService.cite(paper_id);
    }

    //所属领域
    @PostMapping("/paper/systemTags")
    private Response<Object> systemTags(HttpServletRequest request, @RequestBody Map<String, String> map){
        String paper_id = map.get("PID");
        return paperService.systemTags(paper_id);
    }

    //获取评论
    @PostMapping("/paper/viewComment")
    private Response<Object> viewComment(HttpServletRequest request, @RequestBody Map<String, String> map){
        String paper_id = map.get("PID");
        String token = request.getHeader("token");
        String user_id;
        if (token==null){
            user_id="";
        }
        else{
            user_id=JwtUtil.getUserId(token);
        }
        return paperService.viewComment(paper_id,user_id);
    }

    //新增评论
    @PostMapping("/comment/add")
    private Response<Object> commentAdd(HttpServletRequest request, @RequestBody Map<String, String> map){
        String paper_id = map.get("PID");
        String token = request.getHeader("token");
        String user_id=JwtUtil.getUserId(token);
        String content = map.get("Ccontent");
        return paperService.commentAdd(paper_id,user_id,content);
    }

    //点赞
    @PostMapping("/comment/like")
    private Response<Object> like(HttpServletRequest request, @RequestBody Map<String, String> map){
        String token = request.getHeader("token");
        String user_id=JwtUtil.getUserId(token);
        String comment_id = map.get("CID");
        return paperService.like(user_id,comment_id);
    }

    //取消点赞
    @PostMapping("/comment/unlike")
    private Response<Object> unlike(HttpServletRequest request, @RequestBody Map<String, String> map){
        String token = request.getHeader("token");
        String user_id=JwtUtil.getUserId(token);
        String comment_id = map.get("CID");
        return paperService.unlike(user_id,comment_id);
    }


    @GetMapping("/recommendPapers")
    private Response<Object> getRecommendWork(HttpServletRequest request) throws IOException {
        return paperService.getRecommendWorks();
    }

    @GetMapping("/recommendConferences")
    private Response<Object> getRecommendConf(HttpServletRequest request)
    {
        return paperService.getRecommendConf();
    }

    @RequestMapping(value = "/recommendJournals", method=RequestMethod.GET)
    private Response<Object> getRecommendJournal(HttpServletRequest request)
    {
        return paperService.getRecommendJournal();
    }
}
