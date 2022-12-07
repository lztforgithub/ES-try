package ES.Crawler;

import ES.Common.AlexUtils;
import ES.Common.HttpUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

public class ConceptCrawler {

    public static JSONArray csConcepts = new JSONArray();

    public static JSONArray getLevelZeroConcepts(){
        JSONArray arr = new JSONArray();
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("https://api.openalex.org/concepts");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("filter", "level:0"));
            URI uri = new URIBuilder(httpGet.getUri())
                    .addParameters(nameValuePairs)
                    .build();
            httpGet.setUri(uri);
            try (CloseableHttpResponse response = httpclient.execute(httpGet)){
                String result = HttpUtils.handleResponse(response);
                JSONObject obj = new JSONObject(result);
                JSONArray concepts = obj.getJSONArray("results");
                int count = concepts.length();
                for(int i = 0; i < count; i++){
                    obj = concepts.getJSONObject(i);
                    JSONObject newConcept = new JSONObject();
                    newConcept.put("CID", AlexUtils.getRawID(obj.getString("id")));
                    newConcept.put("Cname", obj.getString("display_name"));
                    String chineseName = "none";
                    try {
                        chineseName = obj.getJSONObject("international")
                                .getJSONObject("display_name")
                                .getString("zh-hans");
                    } catch (Exception e){
                        System.out.printf("Concept %s has no Chinese Name provided.\n",
                                obj.getString("display_name"));
                    }
                    newConcept.put("CnameCN", chineseName);
                    newConcept.put("Clevel", obj.getInt("level"));
                    newConcept.put("CparentID", "none");
                    arr.put(newConcept);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arr;
    }

    /**
     * 抓取第level级的，父级概念含ancestorID的所有概念。
     * @param level 概念等级。
     * @param ancestorID 所属的父级概念ID。
     * @return 一个JSONArray，内含所有符合条件的概念。
     */
    public static JSONArray getConceptsByAncestor(int level, String ancestorID){
        int totalConceptCount = 25;
        int currentConceptCount = 0;
        int page = 0;
        String alexURI = "https://api.openalex.org/concepts";
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();

        nameValuePairs.add(0, new BasicNameValuePair("filter", "level:1,ancestors.id:" + ancestorID));
        nameValuePairs.add(1, new BasicNameValuePair("page", Integer.toString(0)));
        JSONArray ret = new JSONArray();

        try {
            while (currentConceptCount < totalConceptCount) {
                page++; // 查询新的一页
                nameValuePairs.set(1, new BasicNameValuePair("page", Integer.toString(page)));
                String responseString = HttpUtils.handleRequestWithParams(alexURI, nameValuePairs);
                // 检查是否出错
                if (responseString.equals("ERR_GET") || responseString.equals("ERR_CLIENT")) {
                    JSONObject obj = new JSONObject();
                    obj.put("error", responseString);
                    ret.put(obj);
                    return ret; // 返回错误代码
                }
                // 处理返回信息
                JSONObject conceptJSON = new JSONObject(responseString);
                // 如果返回结果异常，则抛出错误信息
                try {
                    totalConceptCount = conceptJSON.getJSONObject("meta").getInt("count");
                } catch (Exception e){
                    System.out.println("Error message from OpenAlex server:");
                    System.out.println(conceptJSON);
                    System.out.println("Error message from local JVM:");
                    e.printStackTrace();
                    return ret;
                }

                JSONArray receivedConcepts = conceptJSON.getJSONArray("results");
                for (int i = 0; i < receivedConcepts.length(); i++) {
                    JSONObject parsedConcept = parseOpenAlexConceptInfo(receivedConcepts.getJSONObject(i)) ;
                    ret.put(parsedConcept);
                    currentConceptCount++;
                    System.out.printf("Progress: %d / %d, current CID %s\n", currentConceptCount, totalConceptCount,
                            parsedConcept.getString("CID"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static JSONObject parseOpenAlexConceptInfo(JSONObject concept) {
        JSONObject ret = new JSONObject();
        try {
            ret.put("CID", AlexUtils.getRawID(concept.getString("id")));
            ret.put("Cname", concept.getString("display_name"));
            try {
                ret.put("CnameCN", concept.getJSONObject("international").getJSONObject("display_name").getString("zh-hans"));
            } catch (Exception e) {
                ret.put("CnameCN", "none");
                System.out.printf("Concept %s has no Chinese name.\n", concept.getString("display_name"));
            }

            ret.put("Clevel", concept.getInt("level"));
            JSONArray ancestors = concept.getJSONArray("ancestors");
            ArrayList<String> ancestorIDs = new ArrayList<>();
            for (int i = 0; i < ancestors.length(); i++) {
                ancestorIDs.add(AlexUtils.getRawID(ancestors.getJSONObject(i).getString("id")));
            }
            ret.put("CancestorID", ancestorIDs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void main(String[] args) {
        JSONArray levelZeroConcepts = getLevelZeroConcepts();
        try {
            for (int i = 0; i < levelZeroConcepts.length(); i++) {
                JSONObject levelZeroConcept = levelZeroConcepts.getJSONObject(i);
                // System.out.println("Get level 0 concept " + levelZeroConcept.getString("Cname"));
                if (levelZeroConcept.getString("Cname").equals("Computer science")) {
                    JSONArray arr = getConceptsByAncestor(1,
                            levelZeroConcept.getString("CID"));
                    for (int j = 0; j < arr.length(); j++) {
                        System.out.printf("Crawled level 1 concept %s\n", arr.getJSONObject(j).getString("Cname"));
                        csConcepts.put(arr.getJSONObject(j));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
