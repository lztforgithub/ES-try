package ES.Controller;

import ES.Common.EsUtileService;
import ES.Common.JwtUtil;
import ES.Common.Response;
import ES.Service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class SearchController {
    @Autowired
    SearchService searchService;

    //默认搜索
    @PostMapping("/DefaultSearchResults")
    public Response<Object> defaultSearch(HttpServletRequest request, @RequestBody Map<String,Object> map) throws IOException {
        //取用户id,判断是否收藏,未登录则user_id=""
        String token = request.getHeader("token");
        String user_id;
        if (token==null){
            user_id="";
        }
        else{
            user_id=JwtUtil.getUserId(token);
        }
        //搜索关键词
        String normalSearch = (String) map.get("normalSearch");
        //起止时间，存疑
        Timestamp start_time = (Timestamp) map.get("startTime");
        Timestamp end_time = (Timestamp) map.get("endTime");
        //包含作者
        String filterAuthors = (String) map.get("filterAuthors");
        //包含出版类型
        String filterPublicationTypes = (String) map.get("filterPublicationTypes");
        //sort,排序方式
        String sort = (String) map.get("sort");

        return searchService.defaultSearch(
                user_id,
                normalSearch,
                start_time,
                end_time,
                filterAuthors,
                filterPublicationTypes,
                sort
        );
    }





    @RequestMapping("/author/hello")
    public Response<Object> hello() {
        return Response.success("hello world");
    }

    @RequestMapping("/auth/login")
    public Response<Object> login() {
        //假设数据库中查询到了该用户，这里测试先所及生成一个UUID，作为用户的id
        String userId = UUID.randomUUID().toString();

        //准备存放在IWT中的自定义数据
        Map<String, Object> info = new HashMap<>();
        info.put("username", "tom");
        info.put("pass", "admin");

        //生成JWT字符串
        String token = JwtUtil.sign(userId, info);

        return Response.success("token:" + token);
    }
}
