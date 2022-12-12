package ES.storage;

import ES.Common.HttpUtils;
import ES.Common.WebITS;
import ES.Crawler.ConceptCrawler;
import ES.Crawler.ResearcherCrawler;
import ES.Document.ConceptDoc;
import ES.Document.ResearcherDoc;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;
import java.util.ArrayList;

public class FinishedCrawlers {
    public void getCoAuthors() {
        int targetLevel = 2;
        ConceptStorage conceptStorage = new ConceptStorage();
        JSONArray currentConcepts = conceptStorage.searchConceptByLevelAndAncestor("C33923547", 1);
        for (int i = 0; i < currentConcepts.size(); i++) {
            JSONObject currentConcept = currentConcepts.getJSONObject(i);
            String Cname = currentConcept.getString("cname");
            String CID = currentConcept.getString("cID");
            if (Cname.equals("Computer science")) {
                continue;
            }
            System.out.printf("Ready to crawl level %d concepts whose ancestor is %s", targetLevel, Cname);

            int page = 0;
            int count = 0;
            int tries = 0;
            int totalCrawledConcepts = 25;
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(0, new BasicNameValuePair("filter", "level:" + targetLevel + ",ancestors.id:" + CID));
            while(count < totalCrawledConcepts) {
                tries++;
                page++;
                nameValuePairs.add(1, new BasicNameValuePair("page", Integer.toString(page)));
                String requestString = "";
                try {
                    URI uri = new URIBuilder("https://api.openalex.org/concepts")
                            .addParameters(nameValuePairs)
                            .build();
                    requestString = uri.toString();
                } catch (Exception e) {

                }
                nameValuePairs.remove(1);
                System.out.println("Concept Request: " + requestString);

                String response = HttpUtils.handleRequestURL(requestString);
                JSONObject responseJSON = JSONObject.parseObject(response);

                totalCrawledConcepts = responseJSON.getJSONObject("meta").getInteger("count");
                JSONArray arr = responseJSON.getJSONArray("results");
                for (int j = 0; j < arr.size(); j++) {
                    JSONObject object = arr.getJSONObject(j);
                    // System.out.println(object);
                    ConceptDoc conceptDoc = ConceptCrawler.parseOpenAlexConceptInfo(object);
                    conceptStorage.addDoc("concept", conceptDoc);
                    count++;
                }
                System.out.printf("Crawl %s's children status %d / %d\n", Cname, count, totalCrawledConcepts);
//                if (tries >= 1) {
//                    break;
//                }
            }
//            if (tries >= 1) {
//                break;
//            }
        }

    }

    public JSONObject testTranslate(String originText, String originLanguage, String targetLanguage) {
        JSONObject ret = new JSONObject();
        try {
            String result = WebITS.translate(originText, originLanguage, targetLanguage);
            System.out.println(result);
            ret.put("result", result);
        } catch (Exception e) {

        }
        return ret;
    }

    public void crawlFamousResearchers() {
        // C41008148
        ConceptStorage conceptStorage = new ConceptStorage();
        ResearcherStorage researcherStorage = new ResearcherStorage();
        JSONArray currentConcepts = conceptStorage.searchConceptByLevelAndAncestor("C41008148", 1);
        for (int i = 0; i < currentConcepts.size(); i++) {
            JSONObject currentConcept = currentConcepts.getJSONObject(i);
            String Cname = currentConcept.getString("cname");
            String CID = currentConcept.getString("cID");
            int page = 0;
            int count = 0;
            int tries = 0;
            int totalCrawledResearchers = 75;
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(0, new BasicNameValuePair("filter",
                    "last_known_institution.continent:north_america,cited_by_count:>15000,x_concepts.id:" + CID));
            while(count < totalCrawledResearchers) {
                tries++;
                page++;
                nameValuePairs.add(1, new BasicNameValuePair("page", Integer.toString(page)));
                String requestString = "";
                try {
                    URI uri = new URIBuilder("https://api.openalex.org/authors")
                            .addParameters(nameValuePairs)
                            .build();
                    requestString = uri.toString();
                } catch (Exception e) {

                }
                nameValuePairs.remove(1);
                System.out.printf("[%s]Researcher Request: %s" , Cname, requestString);

                String response = HttpUtils.handleRequestURL(requestString);
                JSONObject responseJSON = JSONObject.parseObject(response);

                totalCrawledResearchers = responseJSON.getJSONObject("meta").getInteger("count");
                JSONArray arr = responseJSON.getJSONArray("results");
                for (int j = 0; j < arr.size(); j++) {
                    JSONObject object = arr.getJSONObject(j);
                    // System.out.println(object);
                    ResearcherDoc researcherDoc = ResearcherCrawler.parseOpenAlexResearcherInfo(object);
                    researcherStorage.addDoc("researcher", researcherDoc);
                    count++;
                }
                System.out.printf("Crawl status %d / %d\n", count, totalCrawledResearchers);
//                if (tries >= 1) {
//                    break;
//                }
            }
//            if (tries >= 1) {
//                break;
//            }
        }
    }
}
