package ES.storage;

import ES.Common.AlexUtils;
import ES.Common.CrawlerUtils;
import ES.Common.PageResult;
import ES.Common.Response;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Component
@RestController
public class FrogRecommendService {


    @RequestMapping(value = "/frogRecommendPapers", method = RequestMethod.GET)
    public Response<JSONObject> frogRecommendPapers(){

        JSONObject ret = new JSONObject();
        JSONArray paperResults = new JSONArray();

        ConceptStorage conceptStorage = new ConceptStorage();
        // 爬取所有一级概念
        JSONArray arr = conceptStorage.searchConceptByLevelAndAncestor("C41008148", 1);
        System.out.println("Recommend paper concepts:" + arr.size());
        // 随机选取一级概念
        int choose = (int) (Math.random() * 20);

        while (arr.getJSONObject(choose).getString("cname").equals("Data science")
                || arr.getJSONObject(choose).getString("cname").equals("Library science")){
            choose = (int) (Math.random() * 20);
        }

        JSONObject selectedConcept = arr.getJSONObject(choose);
        String Cname = selectedConcept.getString("cname");
        HashMap<String, Object> andMap = new HashMap<>();
        andMap.put("pconcepts", Cname);

        System.out.println("Select: " + Cname);
        ret.put("cName", Cname);

        // 默认查询进5年
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = dateFormat.parse("10/10/2016");
            long startTime = date.getTime();
            Timestamp timestamp1 = new Timestamp(startTime);
            date = dateFormat.parse("11/11/2022");
            long endTime = date.getTime();
            Timestamp timestamp2 = new Timestamp(endTime);
            // 查找概念带Cname的论文
            int counter = 0;
            for (int j = 1; j <= 10; j++) {
                PageResult<JSONObject> pageResult = conceptStorage.esUtileService.defaultSearch("works", j, 20, "",
                        andMap, null, null, null,null, null, timestamp1, timestamp2, "pcite");
                for (JSONObject work : pageResult) {
                    if (CrawlerUtils.checkWorkConceptRelevance(work, Cname, 5)) {

                        String Pname = work.getString("pname");
                        String PID = work.getString("pID");
                        String P_Vname = work.getString("p_Vname");
                        if (P_Vname == null) {
                            continue;
                        }

                        counter++;
                        System.out.printf("[%s]%s work is relevant. %d\n", PID, Pname, counter);
                        JSONObject object = new JSONObject();
                        object.put("pName", Pname);
                        object.put("pVName", P_Vname);
                        object.put("pID", PID);
                        paperResults.add(object);
                        if (counter >= 5) {
                            break;
                        }
                    }
                }
                if(counter >= 5) {
                    break;
                }
            }
            // System.out.println(pageResult.getList().size());
            ret.put("paperResults", paperResults);

            return Response.success("成功获取热门论文！", ret);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail("获取热门论文失败。");
        }

    }


    @RequestMapping(value = "/frogRecommendJournals", method = RequestMethod.GET)
    public Response<JSONObject> frogRecommendJournals() {
        JSONObject object = parseRecommendVenues("journal", 10);
        if (object.getInteger("status") != 1) {
            return Response.fail("获取热门期刊失败。");
        }else {
            return Response.success("获取热门期刊成功！", object);
        }
    }

    @RequestMapping(value = "/frogRecommendConferences", method = RequestMethod.GET)
    public Response<JSONObject> frogRecommendConferences() {

        JSONObject object = parseRecommendVenues("conference", 10);
        if (object.getInteger("status") != 1) {
            return Response.fail("获取热门期刊失败。");
        }else {
            return Response.success("获取热门期刊成功！", object);
        }
    }


    public JSONObject parseRecommendVenues(String type, int lim) {
        JSONObject ret = new JSONObject();
        JSONArray journalResults = new JSONArray();

        ConceptStorage conceptStorage = new ConceptStorage();
        // 爬取所有一级概念
        JSONArray arr = conceptStorage.searchConceptByLevelAndAncestor("C41008148", 1);
//        System.out.println("Recommend paper concepts:" + arr.size());
        // 随机选取一级概念
        int choose = (int) (Math.random() * lim);

        while (arr.getJSONObject(choose).getString("cname").equals("Data science")
        || arr.getJSONObject(choose).getString("cname").equals("Library science")){
            choose = (int) (Math.random() * lim);
        }

        JSONObject selectedConcept = arr.getJSONObject(choose);
        String Cname = selectedConcept.getString("cname");
        String CID = selectedConcept.getString("cID");
        HashMap<String, Object> andMap = new HashMap<>();
        andMap.put("vconceptIDs", CID);
        andMap.put("vtype", type);


        int counter = 0;

        // 搜索期刊
        for (int j = 1; j <= 6; j++) {
            try {
                PageResult<JSONObject> pageResult = conceptStorage.esUtileService.conditionSearchWithSort("venue", j, 25, "",
                        andMap, null, null, null, "vciteThree");
//                System.out.println("Size: " + pageResult.getList().size());
                for (JSONObject currentVenue : pageResult) {
                    int Vcite1 = currentVenue.getInteger("vciteThree");
//                    System.out.println(Vcite1);
                    if (CrawlerUtils.checkVenueConceptScore(currentVenue, CID, 3)) {
                        counter++;

                        JSONObject object = new JSONObject();
                        String VID = currentVenue.getString("vID");
                        String Vfullname = currentVenue.getString("vfullname");
                        String Vabbrname = AlexUtils.generateAbbr(Vfullname);
                        String ValexAbbrname = "testtesttest";

                        JSONArray array = currentVenue.getJSONArray("valtnames");
                        if (array != null) {
                            for (int k = 0; k < array.size(); k++) {
                                String str = array.getString(k);
                                if (str.length() < 10 && str.length() > 1) {
                                    ValexAbbrname = (ValexAbbrname.length() > str.length() ? str : ValexAbbrname);
                                }
                            }
                        }
                        if (!ValexAbbrname.equals("testtesttest")) {
//                            System.out.println("Change name" + Vfullname);
                            Vabbrname = ValexAbbrname;
                        }

                        int Vcite = currentVenue.getInteger("vciteThree");

//                        System.out.printf("[%s]%s is relevant, %d\n", currentVenue.getString("vID"), Vfullname, counter);

                        object.put("vName", Vfullname);
                        object.put("vAbbrName", Vabbrname);
                        object.put("vCite", Vcite);
                        object.put("vID", VID);

                        journalResults.add(object);
                    }
                    if (counter >= 5) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ret.put("status", 0);
                return ret;
            }
            if (counter >= 5) {
                break;
            }
        }
        ret.put("venueResults", journalResults);
        ret.put("cName", Cname);
        ret.put("status", 1);
        return ret;
    }


}
