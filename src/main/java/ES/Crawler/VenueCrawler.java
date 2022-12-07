package ES.Crawler;

import ES.Document.ConceptDoc;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class VenueCrawler {



    public static JSONObject parseOpenAlexVenueInfo(JSONObject venueJSON){
        JSONObject retJSON = new JSONObject();
        try {
//          System.out.println("Origin JSON:");
//          System.out.println(venueJSON.toString());
            String[] VIDs = venueJSON.getString("id").split("/");
            // 出版物ID
            retJSON.put("VID", VIDs[VIDs.length - 1]);
            // 出版物类型
            retJSON.put("Vtype", venueJSON.getString("type"));
            // 缩写名称
            retJSON.put("Valtnames", venueJSON.getJSONArray("alternate_titles"));
            // 全名
            retJSON.put("Vfullname", venueJSON.getString("display_name"));
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
            retJSON.put("VconceptIDs", conceptIDs);
            // 关联概念对应的相似度
            retJSON.put("Vconceptscores", conceptScores);
            // 已出版work数量
            retJSON.put("Vworkscount", venueJSON.getInt("works_count"));
            // 出版物被引数量
            retJSON.put("Vcitecont", venueJSON.getInt("cited_by_count"));
            // 出版物官方网站
            String url = "";
            if (venueJSON.get("homepage_url") instanceof String) {
                url = venueJSON.getString("homepage_url");
            } else {
                url = "none";
            }
            retJSON.put("Vhomepage", url);

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
            retJSON.put("Vworksyear", worksByYear);
            retJSON.put("Vcitesyear", citesByYear);

//        System.out.println(retJSON);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retJSON;
    }
}
