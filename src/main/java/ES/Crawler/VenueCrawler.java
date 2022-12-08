package ES.Crawler;

import ES.Common.AlexUtils;
import ES.Common.HttpUtils;
import ES.Document.ConceptDoc;
import ES.Document.VenueDoc;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VenueCrawler {


    public static ArrayList<VenueDoc> getVenuesByURL(String url) {
        ArrayList<VenueDoc> ret = new ArrayList<>();
        try {
            String response = HttpUtils.handleRequestURL(url);
            JSONObject responseJSON = new JSONObject(response);
            JSONArray results = responseJSON.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                ret.add(parseOpenAlexVenueInfo(results.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }



    public static VenueDoc parseOpenAlexVenueInfo(JSONObject venueJSON){
        VenueDoc ret = new VenueDoc();
        JSONArray tmp;
        try {
//          System.out.println("Origin JSON:");
//          System.out.println(venueJSON.toString());
            // 出版物ID
            ret.setVID(AlexUtils.getRawID(venueJSON.getString("id")));
            // 出版物类型
            ret.setVtype(venueJSON.getString("type"));
            // 缩写名称
            ArrayList<String> Valtnames = new ArrayList<>();
            tmp = venueJSON.getJSONArray("alternate_titles");
            for (int i = 0; i < tmp.length(); i++) {
                Valtnames.add(tmp.getString(i));
            }
            ret.setValtnames(Valtnames);
            // 全名
            ret.setVfullname(venueJSON.getString("display_name"));
            // 出版物所属领域
            // 取至多前五个
            ArrayList<ConceptDoc> concepts = new ArrayList<>();
            ArrayList<Double> conceptScores = new ArrayList<>();
            ArrayList<String> conceptIDs = new ArrayList<>();
            JSONArray originConcepts = venueJSON.getJSONArray("x_concepts");
            int conceptLength = Math.min(originConcepts.length(), 10);
            for (int i = 0; i < conceptLength; i++){
                JSONObject obj = originConcepts.getJSONObject(i);
                String[] CIDs = obj.getString("id").split("/");
                conceptIDs.add(CIDs[CIDs.length - 1]);
                conceptScores.add(obj.getDouble("score"));
            }
            // 关联概念ID
            ret.setVconceptIDs(conceptIDs);
            // 关联概念对应的相似度
            ret.setVconceptscores(conceptScores);
            // 已出版work数量
            ret.setVworksCount(venueJSON.getInt("works_count"));
            // 出版物被引数量
            ret.setVcitecount(venueJSON.getInt("cited_by_count"));
            // 出版物官方网站
            String url = "";
            if (venueJSON.get("homepage_url") instanceof String) {
                url = venueJSON.getString("homepage_url");
            } else {
                url = "none";
            }
            ret.setVhomepage(url);

            // 出版物按年统计的work数量，按年统计的被引数量
            int[] targetYears = {2022, 2021, 2020, 2019, 2018};
            int[] worksByYear = {0, 0, 0, 0, 0};
            int[] citesByYear = {0, 0, 0, 0, 0};
            JSONArray dataByYear = venueJSON.getJSONArray("counts_by_year");
            for (int i = 0; i < Math.min(5, dataByYear.length()); i++){
                JSONObject obj = dataByYear.getJSONObject(i);
                int yr = obj.getInt("year");
                for (int j = 0; j < 5; j++){
                    if (targetYears[j] == yr){
                        worksByYear[j] = obj.getInt("works_count");
                        citesByYear[j] = obj.getInt("cited_by_count");
                        break;
                    }
                }
            }

            ArrayList<Integer> worksByYearList = IntStream.of(worksByYear)
                    .boxed()
                    .collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Integer> citesByYearList = IntStream.of(citesByYear)
                            .boxed()
                                    .collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Integer> VworksAccumulate = new ArrayList<>();
            ArrayList<Integer> VcitesAccumulate = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                if(i == 0) {
                    VworksAccumulate.add(0, worksByYear[0]);
                    VcitesAccumulate.add(0, citesByYear[0]);
                }else{
                    VworksAccumulate.add(i, worksByYear[i] + VworksAccumulate.get(i - 1));
                    VcitesAccumulate.add(i, citesByYear[i] + VcitesAccumulate.get(i - 1));
                }
            }
            ret.setVworksyear(worksByYearList);
            ret.setVworksAccumulate(VworksAccumulate);
            ret.setVcitesyear(citesByYearList);
            ret.setVcitesAccumulate(VcitesAccumulate);
//        System.out.println(retJSON);

        } catch (Exception e) {
            e.printStackTrace();
            ret.setVfullname("ERROR");
        }
        return ret;
    }
}
