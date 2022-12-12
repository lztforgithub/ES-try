package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Common.Response;
import ES.Dao.PaperDao;
import ES.Entity.*;
import ES.Ret.CoAuthor;
import ES.Ret.CommentRet;
import ES.Ret.Rpaper;
import ES.Service.PaperService;
import ES.storage.VenueStorage;
import ES.storage.WorkStorage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.common.recycler.Recycler;
import org.junit.Test;
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

import static ES.Common.EsUtileService.castList;

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

        //参考文献
        List<String> PreferencesID = new ArrayList<>();
        List<Rpaper> Preferences = new ArrayList<>();
        Object q = jsonObject.get("preferences");
        PreferencesID = castList(q,String.class);

        if (PreferencesID!=null) {
            for (String i : PreferencesID) {
                JSONObject t = esUtileService.queryDocById("works", "W"+i.split("W")[1]);
                if (t != null) {
                    System.out.println(i);
                    Preferences.add(new Rpaper(
                            i,
                            t.getString("pname"),
                            t.getString("plink"),
                            t.getString("pdate"),
                            t.getString("pcite"),
                            t.getString("pauthor")
                    ));
                }
            }
        }
        jsonObject.put("Preferences",Preferences);

        //相关文献
        List<String> PrelatedID = new ArrayList<>();
        List<Rpaper> Prelateds = new ArrayList<>();
        q = jsonObject.get("prelated");
        PrelatedID = castList(q,String.class);

        if (PreferencesID!=null) {
            for (String i : PrelatedID) {
                JSONObject t = esUtileService.queryDocById("works", "W"+i.split("W")[1]);
                if (t != null) {
                    Prelateds.add(new Rpaper(
                            i,
                            t.getString("pname"),
                            t.getString("plink"),
                            t.getString("pdate"),
                            t.getString("pcite"),
                            t.getString("pauthor")
                    ));
                }
            }
        }
        jsonObject.put("Prelateds",Prelateds);

        //共著作者
        List<String> PauthorID = new ArrayList<>();
        List<CoAuthor> Pauthors = new ArrayList<>();
        q = jsonObject.get("pauthor");
        PauthorID = castList(q,String.class);
        if (PauthorID!=null) {
            for (String i : PauthorID) {
                JSONObject t = esUtileService.queryDocById("researcher", i);
                if (t != null) {
                    Pauthors.add(new CoAuthor(
                            t.getString("rinstitute"),
                            i,
                            t.getString("rname"),
                            t.getString("ravatar")
                    ));
                }
            }
        }

        jsonObject.put("Pauthor",Pauthors);

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
                jsonObject.getString("pbecited"));
    }

    @Override
    public Response<Object> systemTags(String paper_id){
        JSONObject jsonObject = esUtileService.queryDocById("works",paper_id);
        if (jsonObject==null){
            return Response.fail("PID错误!");
        }
        return Response.success("标签如下:",
                jsonObject.getString("psystemTags"));
    }

    @Override
    public Response<Object> viewComment(String paper_id, String user_id){
        List<Comment> comments = paperDao.selectByPID(paper_id);
        List<CommentRet> commentRets = new ArrayList<>();
        LikeRecords likeRecords;
        for (Comment i:comments){
            likeRecords=paperDao.isLike(i.getCID(),user_id);
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
        String next_url = "https://api.openalex.org/works?sort=cited_by_count:desc&per_page=5&filter=concepts.id:"+Cid;
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
        for(; i<5; i++)
        {
            JSONObject result = results.getJSONObject(i);
            String pID = result.getString("id");
            new WorkStorage().storeWork(pID);
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
        return Response.success("返回推荐文献成功", JSON.toJSON(ret));
    }

    @Override
    public Response<Object> getRecommendConf() {
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
        String next_url = "https://api.openalex.org/venues?sort=cited_by_count:desc&per_page=50&filter=concepts.id:"+Cid;
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


        int count = 0;
        JSONObject jsonObject1 = JSON.parseObject(content.toString());

        Recommend ret = new Recommend();

        JSONArray results = jsonObject1.getJSONArray("results");
        int i=0;
        for(; i<results.size()&&count<5; i++)
        {
            JSONObject result = results.getJSONObject(i);
            String type = result.getString("type");
            if(!type.equals("conference"))
            {
                continue;
            }
            ConfInfo confInfo = new ConfInfo();
            String vName = result.getString("display_name");
            String vID = "V"+result.getString("id").split("V")[1];
            new VenueStorage().storeFirstPageVenuesByURL("http://api.openalex.org/venues?filter=openalex:"+vID);
            String vAbbrname = result.getString("abbreviated_title");
            confInfo.setvAbbrname(vAbbrname);
            confInfo.setvName(vName);
            int vCite = 0;
            JSONArray counts = result.getJSONArray("counts_by_year");
            for(int j=0; j<3; j++)
            {
                JSONObject countInfo = counts.getJSONObject(j);
                int year = Integer.parseInt(countInfo.getString("year"));
                int num = Integer.parseInt(countInfo.getString("cited_by_count"));
                if(year>=2020 && year<=2022)
                {
                    vCite += num;
                }
            }
            confInfo.setvCite(vCite);
            ret.addPaperResults(confInfo);
            count += 1;
        }

        JSONObject conceptInfo = esUtileService.queryDocById("concept", Cid);
        String cName = conceptInfo.getString("cname");
        ret.setCount(ret.getPaperResults().size());
        ret.setcName(cName);
        return Response.success("返回推荐会议成功", JSON.toJSON(ret));
    }

    @Override
    public Response<Object> getRecommendJournal() {
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
        String next_url = "https://api.openalex.org/venues?sort=cited_by_count:desc&per_page=50&filter=concepts.id:"+Cid;
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


        int count = 0;
        JSONObject jsonObject1 = JSON.parseObject(content.toString());

        Recommend ret = new Recommend();

        JSONArray results = jsonObject1.getJSONArray("results");
        int i=0;
        for(; i<results.size()&&count<5; i++)
        {
            JSONObject result = results.getJSONObject(i);
            String type = result.getString("type");
            if(!type.equals("journal"))
            {
                continue;
            }
            ConfInfo confInfo = new ConfInfo();
            String vName = result.getString("display_name");
            String vID = "V"+result.getString("id").split("V")[1];
            new VenueStorage().storeFirstPageVenuesByURL("http://api.openalex.org/venues?filter=openalex:"+vID);
            String vAbbrname = result.getString("abbreviated_title");
            confInfo.setvAbbrname(vAbbrname);
            confInfo.setvName(vName);
            int vCite = 0;
            JSONArray counts = result.getJSONArray("counts_by_year");
            for(int j=0; j<3; j++)
            {
                JSONObject countInfo = counts.getJSONObject(j);
                int year = Integer.parseInt(countInfo.getString("year"));
                int num = Integer.parseInt(countInfo.getString("cited_by_count"));
                if(year>=2020 && year<=2022)
                {
                    vCite += num;
                }
            }
            confInfo.setvCite(vCite);
            ret.addPaperResults(confInfo);
            count += 1;
        }

        JSONObject conceptInfo = esUtileService.queryDocById("concept", Cid);
        String cName = conceptInfo.getString("cname");
        ret.setCount(ret.getPaperResults().size());
        ret.setcName(cName);
        return Response.success("返回推荐期刊成功", JSON.toJSON(ret));
    }

    @Test
    public void crawlWorkURLByAuthor() throws IOException {
        ArrayList<String> rIDs = new ArrayList<>();
        ArrayList<String> wIDs = new ArrayList<>();
        WorkStorage workStorage = new WorkStorage();
        PageResult<JSONObject> authors = esUtileService.conditionSearch("researcher", 6, 500, "", null, null, null, null);
        boolean flag = false;
        for(JSONObject authorObj:authors.getList())
        {
            if(((String) authorObj.get("rID")).equals("A2688455705"))
            {
                flag = true;
            }
            if(flag)
            {
                rIDs.add((String) authorObj.get("rID"));
            }
        }
        String URL = "https://api.openalex.org/works?sort=cited_by_count:desc&per_page=5&filter=author.id:";
        for(String id:rIDs)
        {
            String url = URL+id;
            InputStreamReader reader = null;
            BufferedReader in = null;
            StringBuffer content = new StringBuffer();

            try {
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000);
                reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                in = new BufferedReader(reader);

                String line = null;

                while ((line = in.readLine())!=null)
                {
                    content.append(line);
                }
                System.out.println("crawl "+url+" done.");
            } catch (IOException e) {
                System.out.println("can't crawl "+url);
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
            JSONArray results = jsonObject.getJSONArray("results");
            for(int i=0; i<results.size(); i++)
            {
                JSONObject result = results.getJSONObject(i);
                String wID = "https://api.openalex.org/works/W"+result.getString("id").split("W")[1];
                workStorage.storeWork(wID);
                wIDs.add(wID);
                System.out.println(wIDs.size());
            }
        }
        System.out.println("----done----");
    }

    public void crawlWorkByRelate() throws IOException {
        WorkStorage workStorage = new WorkStorage();
        int count = 0;
        boolean flag = false;
        PageResult<JSONObject> works = esUtileService.conditionSearch("works", 1, 200, "", null, null, null, null);
        for(JSONObject obj:works.getList())
        {
            System.out.println("now is "+obj.get("pID"));
            JSONArray relates = obj.getJSONArray("prelated");
            for(int i=0; i<relates.size(); i++)
            {
                String wID = "W"+relates.getString(i).split("W")[1];
                count += 1;
//                workStorage.storeWork("http://api.openalex.org/works/"+wID);
            }
        }
        System.out.println("count="+count);
    }

    public static void main(String[] args) throws IOException {
        PaperServiceImpl paperService = new PaperServiceImpl();
        paperService.crawlWorkByRelate();
        System.out.println("----done----");
    }

}
