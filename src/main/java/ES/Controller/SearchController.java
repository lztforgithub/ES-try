package ES.Controller;

import ES.Common.EsUtileService;
import ES.Common.JwtUtil;
import ES.Common.Response;
import ES.Service.SearchService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ES.Common.EsUtileService.castList;

@RestController
public class SearchController {
    @Autowired
    SearchService searchService;

    //默认搜索
    @PostMapping("/DefaultSearchResults")
    public Response<Object> defaultSearch(HttpServletRequest request, @RequestBody Map<String,Object> map) throws IOException, ParseException {
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
        /*Timestamp start_time = (Timestamp) map.get("startTime");
        Timestamp end_time = (Timestamp) map.get("endTime");*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp start_time = new Timestamp(
                simpleDateFormat.parse(
                        (String) map.get("startTime")

                ).getTime()
        );
        Timestamp end_time = new Timestamp(
                simpleDateFormat.parse(
                        (String) map.get("endTime")
                ).getTime()
        );

        //System.out.println(start_time);
        //System.out.println(end_time);

        //包含作者
        String filterAuthors = (String) map.get("filterAuthors");
        //包含出版类型
        String filterPublicationTypes = (String) map.get("filterPublicationTypes");
        //当前页数
        int page = (int) map.get("page");
        //sort,排序方式
        String sort = (String) map.get("sort");
        if (sort!=null) {
            if (sort.equals("mostRecent")) {
                sort = "pdate";
            }
            if (sort.equals("mostCited")) {
                sort = "pcite";
            }
            if (sort.equals("default")) {
                sort = null;
            }
        }


        return searchService.defaultSearch(
                user_id,
                normalSearch,
                start_time,
                end_time,
                filterAuthors,
                filterPublicationTypes,
                sort,
                page
        );
    }

    //高级搜索
    @PostMapping("/AdvancedSearchResults")
    public Response<Object> AdvancedSearch(HttpServletRequest request, @RequestBody Map<String,Object> map) throws IOException, ParseException {
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
        Object q = map.get("advancedSearch");
        List<JSONObject> advancedSearch = new ArrayList<>();
        advancedSearch = castList(q,JSONObject.class);
        //起止时间，存疑
//        Timestamp start_time = (Timestamp) map.get("startTime");
//        Timestamp end_time = (Timestamp) map.get("endTime");
//        Timestamp adv_start_time = (Timestamp) map.get("advStartTime");
//        Timestamp adv_end_time = (Timestamp) map.get("advEndTime");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Timestamp start_time = new Timestamp(
                simpleDateFormat.parse(
                        (String) map.get("startTime")

                ).getTime()
        );
        Timestamp end_time = new Timestamp(
                simpleDateFormat.parse(
                        (String) map.get("endTime")

                ).getTime()
        );
        Timestamp adv_start_time = new Timestamp(
                simpleDateFormat.parse(
                        (String) map.get("advStartTime")

                ).getTime()
        );
        Timestamp adv_end_time = new Timestamp(
                simpleDateFormat.parse(
                        (String) map.get("advEndTime")

                ).getTime()
        );
        Timestamp from = start_time;
        if (start_time.before(adv_start_time)){
            from = adv_start_time;
        }
        Timestamp to = end_time;
        if (adv_end_time.before(end_time)){
            to = adv_end_time;
        }
        //包含作者
        String filterAuthors = (String) map.get("filterAuthors");
        //包含出版类型
        String filterPublicationTypes = (String) map.get("filterPublicationTypes");
        //当前页数
        int page = (int) map.get("page");
        //sort,排序方式
        String sort = (String) map.get("sort");
        System.out.println(sort);
        if (sort.equals("mostRecent")){
            sort = "pdate";
        }
        if (sort.equals("mostCited")){
            sort = "pcite";
        }
        if (sort.equals("default")){
            sort = null;
        }

        return searchService.advancedSearch(
                user_id,
                advancedSearch,
                from,
                to,
                filterAuthors,
                filterPublicationTypes,
                sort,
                page
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
