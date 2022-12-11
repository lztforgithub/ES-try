package ES.Crawler;

import ES.Common.AlexUtils;
import ES.Common.HttpUtils;
import ES.Common.PageResult;
import ES.Document.ResearcherDoc;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.min;
import static java.util.Map.Entry.comparingByValue;

public class ResearcherCrawler {

    public static ArrayList<ResearcherDoc> getResearchersByURL(String url) {
       //  System.out.println("getResearcher Request url:" + url);
        ArrayList<ResearcherDoc> ret = new ArrayList<>();
        String response = HttpUtils.handleRequestURL(url);
        try {
            JSONObject responseJSON = JSONObject.parseObject(response);
            JSONArray arr = responseJSON.getJSONArray("results");
            for (int i = 0; i < arr.size(); i++) {
                ret.add(parseOpenAlexResearcherInfo(arr.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static ResearcherDoc parseOpenAlexResearcherInfo(JSONObject researcherJSON) {
        ResearcherDoc ret = new ResearcherDoc();
        // System.out.println(researcherJSON);
        try {
            ret.setRID(AlexUtils.getRawID(researcherJSON.getString("id")));
            // 对应入驻用户ID
            ret.setR_UID("none");
            // 学者名称
            ret.setRname(researcherJSON.getString("display_name"));
            // 学者别名
            ArrayList<String> nameAlternatives = new ArrayList<>();
            JSONArray arr = researcherJSON.getJSONArray("display_name_alternatives");
            for (int i = 0; i < arr.size(); i++) {
                nameAlternatives.add(arr.getString(i));
            }
            ret.setRnamealternative(nameAlternatives);
            // 学者引用总量
            ret.setRcitescount(researcherJSON.getInteger("cited_by_count"));
            // 学者论文总量
            ret.setRworkscount(researcherJSON.getInteger("works_count"));

            // 学者按年统计的work数量，按年统计的被引数量
            int[] targetYears = {2022, 2021, 2020, 2019, 2018};
            int[] worksByYear = {0, 0, 0, 0, 0};
            int[] citesByYear = {0, 0, 0, 0, 0};

            JSONArray countsByYear = researcherJSON.getJSONArray("counts_by_year");
            for (int i = 0; i < min(5, countsByYear.size()); i++) {
                JSONObject obj = countsByYear.getJSONObject(i);
                int yr = obj.getInteger("year");
                for (int j = 0; j < 5; j++) {
                    if (targetYears[j] == yr) {
                        worksByYear[j] = obj.getInteger("works_count");
                        citesByYear[j] = obj.getInteger("cited_by_count");
                        break;
                    }
                }
            }

            int[] worksAccumulate = {worksByYear[0], 0, 0, 0, 0};
            int[] citesAccumulate = {citesByYear[0], 0, 0, 0, 0};
            for (int i = 1; i < 5; i++) {
                worksAccumulate[i] = worksAccumulate[i - 1] + worksByYear[i];
                citesAccumulate[i] = citesAccumulate[i - 1] + citesByYear[i];
            }

            ArrayList<Integer> worksByYearList = IntStream.of(worksByYear).boxed().collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Integer> citesByYearList = IntStream.of(citesByYear).boxed().collect(Collectors.toCollection(ArrayList::new));
//            ArrayList<Integer> worksAccumulateList = IntStream.of(worksAccumulate).boxed().collect(Collectors.toCollection(ArrayList::new));
//            ArrayList<Integer> citesAccumulateList = IntStream.of(citesAccumulate).boxed().collect(Collectors.toCollection(ArrayList::new));
            // 按年累计的近五年work数量
//            ret.setRwork("VworksAccumulate", worksAccumulate);
            //按年累计的近五年cite数量
//            ret.put("VcitesAccumulate", citesAccumulate);
            // 按年统计的cite数量
            ret.setRcitesyear(citesByYearList);
            // 按年统计的works数量
            ret.setRworksyear(worksByYearList);

            // 学者领域
            ArrayList<String> conceptIDs = new ArrayList<>();
            JSONArray conceptsJSON = researcherJSON.getJSONArray("x_concepts");
            for(int i = 0; i < 5; i++){
                JSONObject obj = conceptsJSON.getJSONObject(i);
                conceptIDs.add(AlexUtils.getRawID(obj.getString("id")));
            }
            ret.setRconcepts(conceptIDs);

            // 学者文章API
            ret.setRworks_api_url(researcherJSON.getString("works_api_url"));



            // 学者所属机构ID
            String R_IID = researcherJSON.getJSONObject("last_known_institution").getString("id");
            ret.setR_IID(AlexUtils.getRawID(R_IID));
            // 学者所属机构名称
            ret.setRinstitute(researcherJSON.getJSONObject("last_known_institution").getString("display_name"));
            // 联系方式（邮箱）
            ret.setRcontact("none");
            // 个人主页
            ret.setRpersonalPage("none");
            // 门户介绍
            ret.setRgateinfo("none");
            // 自选文献
            // 自动抓取学者cite最多的五篇文章
            String requestTopCite = researcherJSON.getString("works_api_url") + "&sort=cited_by_count:desc";
            WorkCrawler workCrawler = new WorkCrawler(requestTopCite);
            ArrayList<String> gatepubs = workCrawler.getFirstFiveResults(requestTopCite);
            ret.setRgatepubs(gatepubs);

            ArrayList<String> coauthors = new ArrayList<>();
            ArrayList<String> coauthorsInstitute = new ArrayList<>();

            JSONArray res = ResearcherCrawler.getCoAuthors(ret.getRID(), ret.getRname(), ret.getRgatepubs());

            for (int i = 0; i < res.size(); i++) {
                coauthors.add(res.getJSONObject(i).getString("name"));
                coauthorsInstitute.add(res.getJSONObject(i).getString("institutionName"));
            }

            ret.setRcoauthor(coauthors);
            ret.setRcoauthorInstitute(coauthorsInstitute);
            System.out.println("   Coauthors: " + coauthors);
            System.out.println("   CoInstitute: " + coauthorsInstitute);


            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static JSONArray getCoAuthors(String RID, String Rname, ArrayList<String> firstFivePapers) {
        JSONArray ret = new JSONArray();
        String baseURL = "https://api.openalex.org/works?filter=openalex:";
        HashMap<String, Integer> counter = new HashMap<>();
        HashMap<String, String> information = new HashMap<>();
        int crawlCounter = 0;
        for (String topPaperID : firstFivePapers) {
            // 优化，减少40%时间
            crawlCounter++;
            if(crawlCounter > 3) {
                break;
            }
            String request = baseURL + topPaperID;
            String response = HttpUtils.handleRequestURL(request);
            JSONObject object = JSONObject.parseObject(response);
            object = object.getJSONArray("results").getJSONObject(0);
            JSONArray arr = object.getJSONArray("authorships");
            for (int i = 0; i < min(arr.size(), 5); i++) {
                JSONObject obj = arr.getJSONObject(i);
                // System.out.println(obj);
                String tempID = obj.getJSONObject("author").getString("display_name");
                String tempRID = AlexUtils.getRawID(obj.getJSONObject("author").getString("id"));
                if (tempID.equals(Rname)) {
                    continue;
                }
                if (counter.containsKey(tempID)) {
                    counter.put(tempID, counter.get(tempID) - 1);
                } else {
                    counter.put(tempID, -1);
                    String institutionName = "";
                    if (obj.getJSONArray("institutions").size() == 0) {
                        institutionName = "none|" + tempRID;
                    } else {
                        institutionName = obj
                                .getJSONArray("institutions").getJSONObject(0).getString("display_name");
                        institutionName = institutionName + "|" + tempRID;
                    }
                    information.put(tempID, institutionName);

                }
            }
            // System.out.println(counter);
        }
        ArrayList<Integer> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counter.entrySet()) {
            list.add(entry.getValue());
        }
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        Collections.sort(list);
        for (int num : list) {
            for (Map.Entry<String, Integer> entry : counter.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }

        int tmptmp = 0;
        for (String str : sortedMap.keySet()) {
            // System.out.println(str);
            JSONObject object1 = new JSONObject();
            object1.put("name", str);
            object1.put("institutionName", information.get(str));
            ret.add(object1);
            tmptmp++;
            if (tmptmp >= 5) {
                break;
            }
        }
        return ret;
    }

}
