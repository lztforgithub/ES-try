package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.Response;
import ES.Dao.PaperDao;
import ES.Entity.Comment;
import ES.Entity.LikeRecords;
import ES.Entity.PInfo;
import ES.Entity.Recommend;
import ES.Ret.CommentRet;
import ES.Service.PaperService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class PaperServiceImpl implements PaperService {

    @Autowired
    EsUtileService esUtileService = new EsUtileService();
    @Autowired
    PaperDao paperDao;

    @Override
    public Response<Object> view(String paper_id){
        JSONObject jsonObject = esUtileService.queryDocById("works",paper_id);
        if (jsonObject==null){
            return Response.fail("PID错误!");
        }
        return Response.success("文献详情如下:",
                jsonObject);
    }

    @Override
    public Response<Object> cite(String paper_id){
        JSONObject jsonObject = esUtileService.queryDocById("works",paper_id);
        if (jsonObject==null){
            return Response.fail("PID错误!");
        }
        return Response.success("引用格式如下:",
                jsonObject.getString("Pbecited"));
    }

    @Override
    public Response<Object> viewComment(String paper_id, String user_id){
        List<Comment> comments = paperDao.selectByPID(paper_id);
        List<CommentRet> commentRets = new ArrayList<>();
        LikeRecords likeRecords;
        for (Comment i:comments){
            likeRecords=paperDao.isLike(i.getComment_id(),user_id);
            if (likeRecords!=null){
                commentRets.add(new CommentRet(i,true));
            }
            else {
                commentRets.add(new CommentRet(i,false));
            }
        }
        return Response.success("评论如下:",commentRets);
    }

    @Override
    public Response<Object> commentAdd(String paper_id, String user_id, String content){
        Comment comment = new Comment(paper_id,user_id,content);
        if (paperDao.insertComment(comment)>0){
            return Response.success("评论成功!",comment);
        }
        return Response.fail("评论失败!");
    }

    @Override
    public Response<Object> like(String user_id, String comment_id){
        LikeRecords likeRecords = new LikeRecords(user_id,comment_id);
        if (paperDao.insertLikeRecords(likeRecords)>0){
            if (paperDao.updateLike(comment_id)>0){
                return Response.success("点赞成功!",likeRecords);
            }
            paperDao.deleteLikeRecords(user_id,comment_id);
        }
        return Response.fail("点赞失败!");
    }

    @Override
    public Response<Object> unlike(String user_id, String comment_id){

        if (paperDao.deleteLikeRecords(user_id,comment_id)>0){
            if (paperDao.updateUnLike(comment_id)>0){
                return Response.success("取消点赞成功!");
            }
            LikeRecords likeRecords = new LikeRecords(user_id,comment_id);
            paperDao.insertLikeRecords(likeRecords);
        }
        return Response.fail("取消点赞失败!");
    }

    @Override
    public Response<Object> getRecommendWork() {
        String first_url = "https://api.openalex.org/concepts?filter=level:1,ancestors.id:C41008148";

        InputStreamReader reader = null;
        BufferedReader in = null;
        StringBuffer content = new StringBuffer();

        try {
            URLConnection connection = new URL(first_url).openConnection();
            connection.setConnectTimeout(1000);
            reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
            in = new BufferedReader(reader);

            String line = null;

            while ((line = in.readLine())!=null)
            {
                content.append(line);
            }
            System.out.println("crawl "+first_url+" done.");
        } catch (IOException e) {
            System.out.println("can't crawl "+first_url);;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println("cannot close buffered reader!!!");
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("cannot close inputstream reader!!!");
                }
            }
        }

        JSONObject jsonObject = JSON.parseObject(content.toString());
        int total = Integer.parseInt(jsonObject.getJSONObject("meta").getString("count"));
        int choose = (int) (Math.random() * total);
        String Cid = "C"+jsonObject.getJSONArray("results").getJSONObject(choose).getString("id").split("C")[1];
        String next_url = "https://api.openalex.org/works?sort=cited_by_count:desc&per_page=25&filter=concepts.id:"+Cid;
        try {
            content = new StringBuffer();
            URLConnection connection = new URL(next_url).openConnection();
            connection.setConnectTimeout(1000);
            reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
            in = new BufferedReader(reader);

            String line = null;

            while ((line = in.readLine())!=null)
            {
                content.append(line);
            }
            System.out.println("crawl "+next_url+" done.");
        } catch (IOException e) {
            System.out.println("can't crawl "+next_url);;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println("cannot close buffered reader!!!");
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("cannot close inputstream reader!!!");
                }
            }
        }

        JSONObject jsonObject1 = JSON.parseObject(content.toString());

        Recommend ret = new Recommend();

        JSONArray results = jsonObject1.getJSONArray("results");
        int i=0;
        for(; i<results.size()&&i<5; i++)
        {
            JSONObject result = results.getJSONObject(i);
            String pName = result.getString("display_name");
            String host_name = result.getJSONObject("host_venue").getString("display_name");
            PInfo pInfo = new PInfo();
            pInfo.setpName(pName);
            pInfo.setpVName(host_name);
            ret.addPaperResults(pInfo);
        }

        JSONObject conceptInfo = esUtileService.queryDocById("concept", Cid);
        String cName = conceptInfo.getString("cname");
        ret.setCount(i);
        ret.setcName(cName);
        return Response.success("返回推荐文献成功", ret);
    }
}
