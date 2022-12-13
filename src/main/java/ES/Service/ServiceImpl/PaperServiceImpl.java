package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Common.Response;
import ES.Dao.PaperDao;
import ES.Entity.*;
import ES.Ret.CoAuthor;
import ES.Ret.CommentRet;
import ES.Ret.PaperDetails;
import ES.Ret.Rpaper;
import ES.Service.PaperService;
import ES.storage.ConceptStorage;
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
import java.util.*;

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

        int numq;
        int nump;
        //参考文献
        List<String> PreferencesID = new ArrayList<>();
        List<Rpaper> Preferences = new ArrayList<>();
        Object q = jsonObject.get("preferences");
        PreferencesID = castList(q,String.class);
        numq = 0;
        nump = 0;
        if (PreferencesID!=null) {
            for (String i : PreferencesID) {
                nump++;
                if (nump>=10) break;
                JSONObject t = esUtileService.queryDocById("works", "W"+i.split("W")[1]);
                if (t != null) {
                    numq++;
                    if (numq>=5) break;
                    //System.out.println(i);
                    Preferences.add(new Rpaper(
                            "W"+i.split("W")[1],
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
        numq = 0;
        nump = 0;
        if (PreferencesID!=null) {
            for (String i : PrelatedID) {
                nump++;
                if (nump>=10) break;
                JSONObject t = esUtileService.queryDocById("works", "W"+i.split("W")[1]);
                if (t != null) {
                    numq++;
                    if (numq>=5) break;
                    Prelateds.add(new Rpaper(
                            "W"+i.split("W")[1],
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
        numq = 0;
        nump = 0;
        if (PauthorID!=null) {
            for (String i : PauthorID) {
                nump++;
                if (nump>=20) break;
                JSONObject t = esUtileService.queryDocById("researcher", i);
                if (t != null) {
                    numq++;
                    if (numq>=10) break;
                    Pauthors.add(new CoAuthor(
                            t.getString("rinstitute"),
                            i,
                            t.getString("rname"),
                            t.getString("ravatar"),
                            t.getString("r_IID")
                    ));
                }
            }
        }

        if (jsonObject.getString("p_Vurl") == null){
            jsonObject.put("p_Vurl","");
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
        String name;
        for (Comment i:comments){
            likeRecords=paperDao.isLike(i.getCID(),user_id);
            name = paperDao.selectUNameByCUID(i.getC_UID());
            if (likeRecords!=null){
                commentRets.add(new CommentRet(
                        i.getCID(),
                        i.getCcontent(),
                        name,
                        true,
                        i.getCtime(),
                        i.getClikes())
                );
            }
            else {
                commentRets.add(new CommentRet(
                        i.getCID(),
                        i.getCcontent(),
                        name,
                        false,
                        i.getCtime(),
                        i.getClikes())
                );
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

    /*@Override
    public Response<Object> getRecommendWork() {
        String first_url = "https://api.openalex.org/concepts?filter=level:1,ancestors.id:C41008148&per_page=100";

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
        int total = jsonObject.getJSONArray("results").size();
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
            String pID = "W"+result.getString("id").split("W")[1];
            if(esUtileService.queryDocById("works", pID)==null)
            {
                new WorkStorage().storeWork("http://api.openalex.org/works/"+pID);
            }
            String pName = result.getString("display_name");
            String host_name = result.getJSONObject("host_venue").getString("display_name");
            PInfo pInfo = new PInfo();
            pInfo.setpName(pName);
            pInfo.setpVName(host_name);
            pInfo.setpID(pID);
            ret.addPaperResults(pInfo);
        }

        JSONObject conceptInfo = esUtileService.queryDocById("concept", Cid);
        if(conceptInfo==null)
        {
            new ConceptStorage().storeConceptByURL("http://api.openalex.org/concepts?filter=openalex:"+Cid);
            conceptInfo = esUtileService.queryDocById("concept", Cid);
        }
        String cName = conceptInfo.getString("cname");
        ret.setCount(i);
        ret.setcName(cName);
        return Response.success("返回推荐文献成功", JSON.toJSON(ret));
    }*/

    @Override
    public Response<Object> getRecommendConf() {
        String first_url = "https://api.openalex.org/concepts?filter=level:1,ancestors.id:C41008148&per_page=100";

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
        int total = jsonObject.getJSONArray("results").size();
        int choose = (int) (Math.random() * total);
        String Cid = "C"+jsonObject.getJSONArray("results").getJSONObject(choose).getString("id").split("C")[1];
        String next_url = "https://api.openalex.org/venues?sort=cited_by_count:desc&per_page=5&filter=type:conference,concepts.id:"+Cid;
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
        for(; i<results.size(); i++)
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
            if(esUtileService.queryDocById("venue", vID)==null)
            {
                new VenueStorage().storeFirstPageVenuesByURL("http://api.openalex.org/venues?filter=openalex:"+vID);
            }
            String vAbbrname = result.getString("abbreviated_title");
            if(vAbbrname==null)
            {
                JSONArray alternames = result.getJSONArray("alternate_titles");
                if(alternames.size()>0)
                {
                    vAbbrname = alternames.getString(0);
                    for(int j=0; j<alternames.size(); j++)
                    {
                        String temp = alternames.getString(j);
                        if(temp.length()<vAbbrname.length())
                        {
                            vAbbrname = temp;
                        }
                    }
                }
            }
            confInfo.setvAbbrname(vAbbrname);
            confInfo.setvName(vName);
            confInfo.setVID(vID);
            int vCite = 0;
            JSONArray counts = result.getJSONArray("counts_by_year");
            for(int j=0; j<3; j++)
            {
                if(counts.size()>j)
                {
                    JSONObject countInfo = counts.getJSONObject(j);
                    int year = Integer.parseInt(countInfo.getString("year"));
                    int num = Integer.parseInt(countInfo.getString("cited_by_count"));
                    if(year>=2020 && year<=2022)
                    {
                        vCite += num;
                    }
                }
            }
            confInfo.setvCite(vCite);
            ret.addPaperResults(confInfo);
        }

        JSONObject conceptInfo = esUtileService.queryDocById("concept", Cid);
        if(conceptInfo==null)
        {
            new ConceptStorage().storeConceptByURL("http://api.openalex.org/concepts?filter=openalex:"+Cid);
            conceptInfo = esUtileService.queryDocById("concept", Cid);
        }
        String cName = conceptInfo.getString("cname");
        ret.setCount(ret.getPaperResults().size());
        ret.setcName(cName);
        return Response.success("返回推荐会议成功", JSON.toJSON(ret));
    }

    @Override
    public Response<Object> getRecommendJournal() {
        String first_url = "https://api.openalex.org/concepts?filter=level:1,ancestors.id:C41008148&per_page=100";

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
        int total = jsonObject.getJSONArray("results").size();
        int choose = (int) (Math.random() * total);
        String Cid = "C"+jsonObject.getJSONArray("results").getJSONObject(choose).getString("id").split("C")[1];
        String next_url = "https://api.openalex.org/venues?sort=cited_by_count:desc&per_page=5&filter=type:journal,concepts.id:"+Cid;
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
        System.out.println(results.size());
        int i=0;
        for(; i<results.size(); i++)
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
            if(esUtileService.queryDocById("venue", vID)==null)
            {
                new VenueStorage().storeFirstPageVenuesByURL("http://api.openalex.org/venues?filter=openalex:"+vID);
            }
            String vAbbrname = result.getString("abbreviated_title");
            if(vAbbrname==null)
            {
                JSONArray alternames = result.getJSONArray("alternate_titles");
                if(alternames.size()>0)
                {
                    vAbbrname = alternames.getString(0);
                    for(int j=0; j<alternames.size(); j++)
                    {
                        String temp = alternames.getString(j);
                        if(temp.length()<vAbbrname.length())
                        {
                            vAbbrname = temp;
                        }
                    }
                }
            }
            confInfo.setvAbbrname(vAbbrname);
            confInfo.setVID(vID);
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
        }

        JSONObject conceptInfo = esUtileService.queryDocById("concept", Cid);
        if(conceptInfo==null)
        {
            new ConceptStorage().storeConceptByURL("http://api.openalex.org/concepts?filter=openalex:"+Cid);
            conceptInfo = esUtileService.queryDocById("concept", Cid);
        }
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

    private String getRandomConcept()
    {
        String first_url = "https://api.openalex.org/concepts?filter=level:1,ancestors.id:C41008148&per_page=100";

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
        int total = jsonObject.getJSONArray("results").size();
        int choose = (int) (Math.random() * total);
        String Cid = "C"+jsonObject.getJSONArray("results").getJSONObject(choose).getString("id").split("C")[1];
        return Cid;
    }

    private ArrayList<JSONObject> getIntimateWorks(String CID) throws IOException {
        JSONObject conceptInfo = esUtileService.queryDocById("concept", CID);
        if(conceptInfo==null)
        {
            new ConceptStorage().storeConceptByURL("http://api.openalex.org/concepts?filter=openalex:"+CID);
            conceptInfo = esUtileService.queryDocById("concept", CID);
        }
        String cName = conceptInfo.getString("cname");
        Map<String, Object> andMap = new HashMap<>();
        andMap.put("pconcepts", cName);
        PageResult<JSONObject> works = esUtileService.searchForWorks("works", 1, 2000, "", andMap, null, null, null, null, null, "pcite");
        ArrayList<JSONObject> intimateWorks = new ArrayList<>();
        for(JSONObject obj:works)
        {
            JSONArray concepts = obj.getJSONArray("pconcepts");
            if(concepts.size()>0 && concepts.getString(0).equals(cName))
            {
                intimateWorks.add(obj);
            }
            else if(concepts.size()>1 && concepts.getString(1).equals(cName))
            {
                intimateWorks.add(obj);
            }
            else if(concepts.size()>2 && concepts.getString(2).equals(cName))
            {
                intimateWorks.add(obj);
            }
            else if(concepts.size()>3 && concepts.getString(3).equals(cName))
            {
                intimateWorks.add(obj);
            }
            else if(concepts.size()>4 && concepts.getString(4).equals(cName))
            {
                intimateWorks.add(obj);
            }
        }
        return intimateWorks;
    }

    public Response<Object> getRecommendWorks() throws IOException {
        Recommend recommend = new Recommend();
        String cID = getRandomConcept();
        ArrayList<JSONObject> intimateWorks = getIntimateWorks(cID);
        System.out.println("***"+intimateWorks.size());
        recommend.setcName(esUtileService.queryDocById("concept", cID).getString("display_name"));
        for(int i=0; i<5&&i<intimateWorks.size(); i++)
        {
            JSONObject result = intimateWorks.get(i);
            String pID = "W"+result.getString("pID").split("W")[1];
            if(esUtileService.queryDocById("works", pID)==null)
            {
                new WorkStorage().storeWork("http://api.openalex.org/works/"+pID);
            }
            String pName = result.getString("pname");
            String host_ID = result.getString("p_VID");
            JSONObject hostInfo = esUtileService.queryDocById("venue", host_ID);
            System.out.println(host_ID);
            if(hostInfo==null)
            {
                new VenueStorage().storeFirstPageVenuesByURL("http://api.openalex.org/venues?filter=openalex:"+host_ID);
                hostInfo = esUtileService.queryDocById("venue", host_ID);
            }
            String host_name = hostInfo.getString("vfullname");
            PInfo pInfo = new PInfo();
            pInfo.setpName(pName);
            pInfo.setpVName(host_name);
            pInfo.setpID(pID);
            recommend.addPaperResults(pInfo);
        }
        recommend.setCount(recommend.getPaperResults().size());
        return Response.success("推荐文献成功", JSON.toJSON(recommend));
    }

    @Override
    public Response<Object> getDetails(String pid) {
        PaperDetails paperDetails = new PaperDetails();
        JSONObject jsonObject = esUtileService.queryDocById("works", pid);
        JSONArray preferences = jsonObject.getJSONArray("preferences");
        paperDetails.setCiteNum(preferences.size());
        paperDetails.setBeCitedNum(jsonObject.getInteger("pcite"));
        int commentNum = paperDao.getCommentNum(pid);
        paperDetails.setCommentNum(commentNum);
        int collectNum = paperDao.getCollectNum(pid);
        paperDetails.setCollectNum(collectNum);
        JSONArray pcitednum = jsonObject.getJSONArray("pcitednum");
        int year = 2018;
        for(int i=4; i>=0; i--, year++)
        {
            int num = Integer.parseInt((String) pcitednum.getString(i));
            if(num==0)
            {
                continue;
            }
            paperDetails.addCiteNums(num);
            paperDetails.addCiteyears(year);
        }
        return Response.success("文献详情返回成功", paperDetails);
    }

    public void crawlWorkByRelate() throws IOException {
        WorkStorage workStorage = new WorkStorage();
        int count = 0;
        boolean flag = false;
        PageResult<JSONObject> works = esUtileService.conditionSearch("works", 5, 500, "", null, null, null, null);
        for(JSONObject obj:works.getList())
        {
            System.out.println("now is "+obj.get("pID"));
            /*if((obj.get("pID")).equals("W2155250165"))
            {
                flag = true;
            }
            if(flag)
            {*/
                JSONArray relates = obj.getJSONArray("prelated");
                for(int i=0; i<relates.size(); i++)
                {
                    String wID = "W"+relates.getString(i).split("W")[1];
                    count += 1;
                    if(esUtileService.queryDocById("works", wID)==null)
                    {
                        workStorage.storeWork("http://api.openalex.org/works/"+wID);
                    }
                }
//            }
        }
        System.out.println("count="+count);
    }

    public static void main(String[] args) throws IOException {
        PaperServiceImpl paperService = new PaperServiceImpl();
        paperService.crawlWorkByRelate();
        System.out.println("----done----");
    }

}
