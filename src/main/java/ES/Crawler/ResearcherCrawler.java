package ES.Crawler;

import ES.Common.AlexUtils;
import ES.Common.HttpUtils;
import ES.Document.ResearcherDoc;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hc.core5.http.NameValuePair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ResearcherCrawler {

    public static ArrayList<ResearcherDoc> getResearchersByURL(String url) {
        System.out.println("getResearcher Request url:" + url);
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
        System.out.println(researcherJSON);
        try {
            ret.setRID(researcherJSON.getString("id"));
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
            for (int i = 0; i < Math.min(5, countsByYear.size()); i++) {
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
                //TODO 检查Concept是否已经在ElasticSearch中
            }
            ret.setRconcepts(conceptIDs);

            // 学者文章API
            ret.setRworks_api_url(researcherJSON.getString("works_api_url"));

            //TODO 学者共著信息懒加载
            ArrayList<String> coauthors = new ArrayList<>();
            coauthors.add(0, "none");
            ret.setRcoauthor(coauthors);
            ArrayList<String> coauthorsInstitute = new ArrayList<>();
            coauthorsInstitute.add(0, "none");
            ret.setRcoauthorInstitute(coauthorsInstitute);

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
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
