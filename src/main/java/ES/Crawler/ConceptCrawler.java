package ES.Crawler;

import ES.Common.AlexUtils;
import ES.Common.HttpUtils;
import ES.Document.ConceptDoc;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;
import java.util.ArrayList;

public class ConceptCrawler {

    public static ArrayList<ConceptDoc> csConcepts = new ArrayList<>();

    public static ArrayList<ConceptDoc> getLevelZeroConcepts(){
        ArrayList<ConceptDoc> arr = new ArrayList<>();
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
                JSONObject obj = JSONObject.parseObject(result);
                JSONArray concepts = obj.getJSONArray("results");
                int count = concepts.size();
                for(int i = 0; i < count; i++){
                    obj = concepts.getJSONObject(i);
                    arr.add(parseOpenAlexConceptInfo(obj));
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
     * @return 一个ArrayList，内含所有符合条件的概念，存储为ConceptDoc。
     */
    public static ArrayList<ConceptDoc> getConceptsByAncestor(int level, String ancestorID){
        int totalConceptCount = 25;
        int currentConceptCount = 0;
        int page = 0;
        String alexURI = "https://api.openalex.org/concepts";
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();

        nameValuePairs.add(0, new BasicNameValuePair("filter", "level:1,ancestors.id:" + ancestorID));
        nameValuePairs.add(1, new BasicNameValuePair("page", Integer.toString(0)));
        ArrayList<ConceptDoc> ret = new ArrayList<>();

        try {
            while (currentConceptCount < totalConceptCount) {
                page++; // 查询新的一页
                nameValuePairs.set(1, new BasicNameValuePair("page", Integer.toString(page)));
                String responseString = HttpUtils.handleRequestWithParams(alexURI, nameValuePairs);
                // 检查是否出错
                if (responseString.equals("ERR_GET") || responseString.equals("ERR_CLIENT")) {
                    ConceptDoc temp = new ConceptDoc();
                    temp.setCID(responseString);
                    ret.add(temp);
                    return ret; // 返回错误信息
                }
                // 处理返回信息
                JSONObject conceptJSON = JSONObject.parseObject(responseString);
                // 如果返回结果异常，则抛出错误信息
                try {
                    totalConceptCount = conceptJSON.getJSONObject("meta").getInteger("count");
                } catch (Exception e){
                    System.out.println("Error message from OpenAlex server:");
                    System.out.println(conceptJSON);
                    System.out.println("Error message from local JVM:");
                    e.printStackTrace();
                    return ret;
                }

                JSONArray receivedConcepts = conceptJSON.getJSONArray("results");
                for (int i = 0; i < receivedConcepts.size(); i++) {
                    ConceptDoc parsedConcept = parseOpenAlexConceptInfo(receivedConcepts.getJSONObject(i)) ;
                    ret.add(parsedConcept);
                    currentConceptCount++;
                    System.out.printf("Progress: %d / %d, current CID %s\n", currentConceptCount, totalConceptCount,
                            parsedConcept.getCID());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static ArrayList<ConceptDoc> getConceptsByAPIParams(ArrayList<NameValuePair> nameValuePairs) {
        ArrayList<ConceptDoc> ret = new ArrayList<>();
        try {
            String uri = "https://api.openalex.org/concepts";
            String response = HttpUtils.handleRequestWithParams(uri, nameValuePairs);
            JSONObject responseJSON = JSONObject.parseObject(response);
            JSONArray results = responseJSON.getJSONArray("results");
            for (int i = 0; i < results.size(); i++) {
                ret.add(parseOpenAlexConceptInfo(results.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static ArrayList<ConceptDoc> getConceptsByURL(String url) {
        ArrayList<ConceptDoc> ret = new ArrayList<>();
        try {
            String response = HttpUtils.handleRequestURL(url);
            JSONObject responseJSON = JSONObject.parseObject(response);
            JSONArray results = responseJSON.getJSONArray("results");
            for (int i = 0; i < results.size(); i++) {
                ret.add(parseOpenAlexConceptInfo(results.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static ConceptDoc parseOpenAlexConceptInfo(JSONObject concept) {
        ConceptDoc ret = new ConceptDoc();
        try {
            ret.setCID(AlexUtils.getRawID(concept.getString("id")));
            ret.setCname(concept.getString("display_name"));
            try {
                ret.setCnameCN(concept.getJSONObject("international").getJSONObject("display_name").getString("zh-hans"));
            } catch (Exception e) {
                ret.setCnameCN("none");
                System.out.printf("Concept %s has no Chinese name.\n", concept.getString("display_name"));
            }

            ret.setClevel(concept.getInteger("level"));
            if (concept.getInteger("level") > 0) {
                JSONArray ancestors = concept.getJSONArray("ancestors");
                ArrayList<String> ancestorIDs = new ArrayList<>();
                for (int i = 0; i < ancestors.size(); i++) {
                    ancestorIDs.add(AlexUtils.getRawID(ancestors.getJSONObject(i).getString("id")));
                }
                ret.setCancestorID(ancestorIDs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 爬取该概念的所有父级概念。
     * @return 经过处理的ConceptDoc。中间如果有出错，则返回的Cname为ERROR。
     */
    public ConceptDoc getAncestors(ConceptDoc conceptDoc){
        assert conceptDoc.getCID().length() > 1;
        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("filter", "ids.openalex:" + conceptDoc.getCID()));
            String response = HttpUtils.handleRequestWithParams("https://api.openalex.org/concepts", nameValuePairs);
            JSONObject responseJSON = JSONObject.parseObject(response);
            // 拿取第一个记录
            JSONObject entry = responseJSON.getJSONArray("results").getJSONObject(0);
            JSONArray ancestors = entry.getJSONArray("ancestors");
            ArrayList<String> temp = new ArrayList<>();
            for (int i = 0; i < ancestors.size(); i++) {
                temp.add(AlexUtils.getRawID(ancestors.getJSONObject(i).getString("id")));
            }
            conceptDoc.setCancestorID(temp);
        } catch (Exception e) {
            e.printStackTrace();
            conceptDoc.setCname("ERROR");
        }
        return conceptDoc;
    }

    public static void main(String[] args) {
        ArrayList<ConceptDoc> levelZeroConcepts = getLevelZeroConcepts();
        try {
            for (int i = 0; i < levelZeroConcepts.size(); i++) {
                ConceptDoc levelZeroConcept = levelZeroConcepts.get(i);
                // System.out.println("Get level 0 concept " + levelZeroConcept.getString("Cname"));
                if (levelZeroConcept.getCname().equals("Computer science")) {
                    ArrayList<ConceptDoc> arr = getConceptsByAncestor(1,
                            levelZeroConcept.getCID());
                    for (ConceptDoc conceptDoc : arr) {
                        System.out.printf("Crawled level 1 concept %s\n", conceptDoc.getCname());
                        csConcepts.add(conceptDoc);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
