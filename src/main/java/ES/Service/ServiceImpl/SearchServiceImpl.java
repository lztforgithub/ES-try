package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Common.Response;
import ES.Ret.*;
import ES.Service.SearchService;
import ES.config.ElasticSearchConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.incrementer.HanaSequenceMaxValueIncrementer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

import static ES.Common.EsUtileService.castList;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    EsUtileService esUtileService = new EsUtileService();

    @Override
    public Response<Object> defaultSearch(
            String user_id,
            String normalSearch,
            Timestamp start_time,
            Timestamp end_time,
            String filterAuthors,
            String filterPublicationTypes,
            String sort,
            int page) throws IOException {
        try {
            Map<String, Object> andmap = new HashMap<>();
            andmap.put("pname", normalSearch);
            if (filterAuthors != null) {
                List<String> tmp = new ArrayList<>();
                tmp.add(filterAuthors);
                andmap.put("pauthor", tmp);
            }

            //搜索
            PageResult<JSONObject> t = esUtileService.defaultSearch("works", page, 10, "", andmap, null, null, null, null, null, start_time, end_time, sort);
            System.out.println(t.getTotal());
            //初始化最终结果
            List<JSONObject> result = new ArrayList<>();
            JSONObject result_i;

            //统计存在学者发表的论文数，以及出版物,同时加入学者姓名
            List<String> now_authors = new ArrayList<>();
            List<CoAuthor> coAuthors = new ArrayList<>();
            Object q;

            //学者
            Map<String, Integer> R_map = new HashMap<>();
            Map<String, String> R_map_name = new HashMap<>();

            //出版物
            Map<String, Integer> V_map = new HashMap<>();

            //临时变量
            int qs;
            JSONObject p;

            //return Response.success("GG",t);
            System.out.println(t.getList().size());
            for (JSONObject i : t.getList()) {
                //出版类型统计
                String v = i.getString("p_VID");
                p = null;
                if (v!=null) {
                    p = esUtileService.queryDocById("venue", v);
                }
                if (p!=null) {
                    v = p.getString("vtype");
                    //不同的出版类型忽略
                    if (filterPublicationTypes != null) {
                        if (!v.equals(filterPublicationTypes)) {
                            continue;
                        }
                    }
                    if (V_map.containsKey(v)) {
                        qs = V_map.get(v);
                        V_map.put(v, qs + 1);
                    } else {
                        V_map.put(v, 1);
                    }
                }
                //学者统计
                coAuthors = new ArrayList<>();
                q = i.get("pauthor");
                now_authors = castList(q, String.class);
                if (now_authors!=null) {
                    for (String nowAuthor : now_authors) {
                        p = esUtileService.queryDocById("researcher", nowAuthor);
                        if (p != null) {
                            coAuthors.add(new CoAuthor(
                                    p.getString("rinstitute"),
                                    nowAuthor,
                                    p.getString("rname"),
                                    p.getString("ravatar"),
                                    p.getString("r_IID")
                            ));
                            if (R_map.containsKey(nowAuthor)) {
                                qs = R_map.get(nowAuthor);
                                R_map.put(nowAuthor, qs + 1);
                            } else {
                                R_map.put(nowAuthor, 1);
                                R_map_name.put(nowAuthor, p.getString("rname"));
                            }
                        }
                    }
                }
                result_i = i;
                result_i.put("PAuthor", coAuthors);
                result.add(result_i);
            }

            //计算前三的学者
            int max1 = 0, max2 = 0, max3 = 0, now;
            String id1 = "", id2 = "", id3 = "";
            for (String i : R_map.keySet()) {
                now = R_map.get(i);
                if (now >= max1) {
                    id3 = id2;
                    max3 = max2;
                    id2 = id1;
                    max2 = max1;
                    id1 = i;
                    max1 = now;
                    continue;
                }
                if (now >= max2) {
                    id3 = id2;
                    max3 = max2;
                    id2 = i;
                    max2 = now;
                    continue;
                }
                if (now >= max3) {
                    id3 = i;
                    max3 = now;
                    continue;
                }
            }
            List<SimpleAuthor> simpleAuthors = new ArrayList<>();
            simpleAuthors.add(new SimpleAuthor(id1, R_map_name.get(id1), max1));
            simpleAuthors.add(new SimpleAuthor(id2, R_map_name.get(id2), max2));
            simpleAuthors.add(new SimpleAuthor(id3, R_map_name.get(id3), max3));

            //统计前三的出版物
            max1 = 0;
            max2 = 0;
            max3 = 0;
            id1 = "";
            id2 = "";
            id3 = "";
            for (String i : V_map.keySet()) {
                now = V_map.get(i);
                if (now >= max1) {
                    id3 = id2;
                    max3 = max2;
                    id2 = id1;
                    max2 = max1;
                    id1 = i;
                    max1 = now;
                    continue;
                }
                if (now >= max2) {
                    id3 = id2;
                    max3 = max2;
                    id2 = i;
                    max2 = now;
                    continue;
                }
                if (now >= max3) {
                    id3 = i;
                    max3 = now;
                }
            }
            List<SimpleVenue> simpleVenues = new ArrayList<>();
            simpleVenues.add(new SimpleVenue(id1, max1));
            simpleVenues.add(new SimpleVenue(id2, max2));
            simpleVenues.add(new SimpleVenue(id3, max3));

            //计算总数量
            int totalNumber = (int) t.getTotal();


            //recommendation
            //author
            andmap = new HashMap<>();
            andmap.put("rname",normalSearch);
            List<JSONObject> author = new ArrayList<>();
            t = esUtileService.conditionSearch("researcher",1,20,"",null,null,null,andmap);
            int nums = 0;
            for (JSONObject i:t.getList()){
                nums++;
                if (nums>3){
                    break;
                }
                author.add(i);
            }

            //institute
            andmap = new HashMap<>();
            andmap.put("iname",normalSearch);
            List<JSONObject> institute = new ArrayList<>();
            t = esUtileService.conditionSearch("institutions",1,20,"",null,null,null,andmap);
            nums = 0;
            for (JSONObject i:t.getList()){
                nums++;
                if (nums>3){
                    break;
                }
                institute.add(i);
            }

            //整合
            SearchResultRet searchResultRet = new SearchResultRet(
                    result,
                    simpleAuthors,
                    simpleVenues,
                    totalNumber,
                    new Recommendation(author,institute)
            );

            return Response.success("搜索结果如下:", searchResultRet);
        }catch (Exception e){
            return Response.fail("网络错误!");
        }
    }

    @Override
    public Response<Object> advancedSearch(
            String user_id,
            List<JSONObject> advancedSearch,
            Timestamp from,
            Timestamp to,
            String filterAuthors,
            String filterPublicationTypes,
            String sort,
            int page) throws IOException {

        try{

            int type; String category; String content;

            //处理搜索条件
            Map<String,Object> andmap = new HashMap<>();
            Map<String,Object> ormap = new HashMap<>();
            Map<String,Object> notmap = new HashMap<>();
            Map<String,Object> tempMap = new HashMap<>();

            for (JSONObject i:advancedSearch){
                type = i.getInteger("type");
                category = i.getString("category");
                content = i.getString("content");

                if (category.equals("main")){
                    switch (type){
                        case 1:
                            andmap.put("pname", content);
                            andmap.put("pabstract", content);
                            break;
                        case 2:
                            ormap.put("pname", content);
                            ormap.put("pabstract", content);
                            break;
                        case 3:
                            notmap.put("pname", content);
                            notmap.put("pabstract", content);
                            break;
                    }
                    continue;
                }

                if (category.equals("Pname")){
                    switch (type){
                        case 1:
                            andmap.put("pname", content);
                            break;
                        case 2:
                            ormap.put("pname", content);
                            break;
                        case 3:
                            notmap.put("pname", content);
                            break;
                    }
                    continue;
                }

                if (category.equals("Pabstract")){
                    switch (type){
                        case 1:
                            andmap.put("pabstract", content);
                            break;
                        case 2:
                            ormap.put("pabstract", content);
                            break;
                        case 3:
                            notmap.put("pabstract", content);
                            break;
                    }
                    continue;
                }

                if (category.equals("Pconcepts")){
                    switch (type){
                        case 1:
                            andmap.put("pconcepts", content);
                            break;
                        case 2:
                            ormap.put("pconcepts", content);
                            break;
                        case 3:
                            notmap.put("pconcepts", content);
                            break;
                    }
                    continue;
                }

                if (category.equals("Pauthor")){
                    switch (type){
                        case 1:
                            andmap.put("pauthor", content);
                            break;
                        case 2:
                            ormap.put("pauthor", content);
                            break;
                        case 3:
                            notmap.put("pauthor", content);
                            break;
                    }
                    continue;
                }

                if (category.equals("Cname")){
                    tempMap = new HashMap<>();
                    tempMap.put("Cname",content);
                    PageResult<JSONObject> t = esUtileService.conditionSearch("concept",100,20,"",tempMap,null,null,null);
                    content = t.getList().get(0).getString("CID");
                    switch (type){
                        case 1:
                            andmap.put("pconcepts", content);
                            break;
                        case 2:
                            ormap.put("pconcepts", content);
                            break;
                        case 3:
                            notmap.put("pconcepts", content);
                            break;
                    }
                    continue;
                }

                if (category.equals("Iname")){
                    tempMap = new HashMap<>();
                    tempMap.put("Rinstitute",content);
                    PageResult<JSONObject> t = esUtileService.conditionSearch("researcher",100,20,"",tempMap,null,null,null);
                    content = t.getList().get(0).getString("rID");
                    /*for (JSONObject j:t.getList()){
                        switch (type){
                            case 1:
                                //andmap.put("pauthor", content);
                                break;
                            case 2:
                                ormap.put("pauthor", j.getString("rID"));
                                break;
                            case 3:
                                notmap.put("pauthor", j.getString("rID"));
                                break;
                        }
                    }*/
                    switch (type){
                        case 1:
                            andmap.put("pauthor", content);
                            break;
                        case 2:
                            ormap.put("pauthor", content);
                            break;
                        case 3:
                            notmap.put("pauthor", content);
                            break;
                    }
                    continue;
                }

                if (category.equals("sourse")){
                    switch (type){
                        case 1:
                            andmap.put("p_Vname", content);
                            break;
                        case 2:
                            ormap.put("p_Vname", content);
                            break;
                        case 3:
                            notmap.put("p_Vname", content);
                            break;
                    }
                    continue;
                }

                if (category.equals("DOI")){
                    switch (type){
                        case 1:
                            andmap.put("dOI", content);
                            break;
                        case 2:
                            ormap.put("dOI", content);
                            break;
                        case 3:
                            notmap.put("dOI", content);
                            break;
                    }
                    continue;
                }

                if (category.equals("PsystemTags")){
                    switch (type){
                        case 1:
                            andmap.put("psystemTags", content);
                            break;
                        case 2:
                            ormap.put("psystemTags", content);
                            break;
                        case 3:
                            notmap.put("psystemTags", content);
                            break;
                        }
                    continue;
                }
            }
            //处理筛选条件
            boolean flag = false;
            if (filterAuthors != null) {
                List<String> tmp = new ArrayList<>();
                tmp.add(filterAuthors);
                andmap.put("pauthor", tmp);
                flag = true;
            }
            if (!flag) {
                andmap = null;
            }

            //搜索
            PageResult<JSONObject> t = esUtileService.defaultSearch("works", 100, 20, "", andmap, ormap, notmap, null, null, null, from, to,sort);

            //初始化最终结果
            List<JSONObject> result = new ArrayList<>();
            JSONObject result_i;

            //统计存在学者发表的论文数，以及出版物,同时加入学者姓名
            List<String> now_authors = new ArrayList<>();
            List<CoAuthor> coAuthors = new ArrayList<>();
            Object q;
            //学者
            Map<String, Integer> R_map = new HashMap<>();
            Map<String, String> R_map_name = new HashMap<>();
            //出版物
            Map<String, Integer> V_map = new HashMap<>();
            //临时变量
            int qs;
            JSONObject p;
            for (JSONObject i : t.getList()) {
                //出版类型统计
                String v = i.getString("p_VID");
                p = esUtileService.queryDocById("venue", v);
                v = p.getString("vtype");
                //不同的出版类型忽略
                if (filterPublicationTypes != null) {
                    if (!v.equals(filterPublicationTypes)) {
                        continue;
                    }
                }
                if (V_map.containsKey(v)) {
                    qs = V_map.get(v);
                    V_map.put(v, qs + 1);
                } else {
                    V_map.put(v, 1);
                }
                //学者统计
                coAuthors = new ArrayList<>();
                q = i.get("pauthor");
                now_authors = castList(q, String.class);
                for (String nowAuthor : now_authors) {
                    p = esUtileService.queryDocById("researcher", nowAuthor);
                    if (p != null) {
                        coAuthors.add(new CoAuthor(
                                p.getString("rinstitute"),
                                nowAuthor,
                                p.getString("rname"),
                                p.getString("ravatar"),
                                p.getString("r_IID")
                        ));
                        if (R_map.containsKey(nowAuthor)) {
                            qs = R_map.get(nowAuthor);
                            R_map.put(nowAuthor, qs + 1);
                        } else {
                            R_map.put(nowAuthor, 1);
                            R_map_name.put(nowAuthor, p.getString("rname"));
                        }
                    }
                }
                result_i = i;
                result_i.put("PAuthor", coAuthors);
                result.add(result_i);

            }
            //计算前三的学者
            int max1 = 0, max2 = 0, max3 = 0, now;
            String id1 = "", id2 = "", id3 = "";
            for (String i : R_map.keySet()) {
                now = R_map.get(i);
                if (now >= max1) {
                    id3 = id2;
                    max3 = max2;
                    id2 = id1;
                    max2 = max1;
                    id1 = i;
                    max1 = now;
                    continue;
                }
                if (now >= max2) {
                    id3 = id2;
                    max3 = max2;
                    id2 = i;
                    max2 = now;
                    continue;
                }
                if (now >= max3) {
                    id3 = i;
                    max3 = now;
                    continue;
                }
            }
            List<SimpleAuthor> simpleAuthors = new ArrayList<>();
            simpleAuthors.add(new SimpleAuthor(id1, R_map_name.get(id1), max1));
            simpleAuthors.add(new SimpleAuthor(id2, R_map_name.get(id2), max2));
            simpleAuthors.add(new SimpleAuthor(id3, R_map_name.get(id3), max3));

            //统计前三的出版物
            max1 = 0;
            max2 = 0;
            max3 = 0;
            id1 = "";
            id2 = "";
            id3 = "";
            for (String i : V_map.keySet()) {
                now = V_map.get(i);
                if (now >= max1) {
                    id3 = id2;
                    max3 = max2;
                    id2 = id1;
                    max2 = max1;
                    id1 = i;
                    max1 = now;
                    continue;
                }
                if (now >= max2) {
                    id3 = id2;
                    max3 = max2;
                    id2 = i;
                    max2 = now;
                    continue;
                }
                if (now >= max3) {
                    id3 = i;
                    max3 = now;
                    continue;
                }
            }
            List<SimpleVenue> simpleVenues = new ArrayList<>();
            simpleVenues.add(new SimpleVenue(id1, max1));
            simpleVenues.add(new SimpleVenue(id2, max2));
            simpleVenues.add(new SimpleVenue(id3, max3));

            //计算总数量
            int totalNumber = (int) t.getTotal();

            //整合
            SearchResultRet searchResultRet = new SearchResultRet(
                    result,
                    simpleAuthors,
                    simpleVenues,
                    totalNumber,
                    null
            );

            return Response.success("搜索结果如下:", t);
        }catch (Exception e){
            return  Response.fail("网络错误!");
        }
    }
}
