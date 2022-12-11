package ES.storage;

import ES.Common.AlexUtils;
import ES.Common.EsUtileService;
import ES.Common.HttpUtils;
import ES.Common.PageResult;
import ES.Crawler.ConceptCrawler;
import ES.Crawler.InstitutionCrawler;
import ES.Crawler.ResearcherCrawler;
import ES.Crawler.WorkCrawler;
import ES.Document.ConceptDoc;
import ES.Document.InstitutionDoc;
import ES.Document.ResearcherDoc;
import ES.Document.WorkDoc;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

@Component
@RestController
public class InstitutionStorage {
    EsUtileService esUtileService = new EsUtileService();

    @RequestMapping(value="/storeInstitution", method = RequestMethod.PUT)
    public void storeInstitution(String url)
    {
        InstitutionCrawler institutionCrawler = new InstitutionCrawler(url);
        InstitutionStorage institutionStorage = new InstitutionStorage();
        institutionStorage.addDoc("institutions", institutionCrawler.run());
    }

    /**
     * 爬取institutions api中所有的项目。
     * @param url openalex institutions api链接
     */
    @RequestMapping(value = "/storeInstitutions", method = RequestMethod.PUT)
    public void storeInstitutions(String url) {
        InstitutionCrawler institutionCrawler = new InstitutionCrawler(url);
        InstitutionStorage institutionStorage = new InstitutionStorage();
        institutionStorage.addDoc(institutionCrawler.crawlInstitutions(url));
    }

    public void addDoc(String indexName, InstitutionDoc institutionDoc)
    {
        esUtileService.addDoc(indexName, institutionDoc);
    }

    public void addDoc(ArrayList<InstitutionDoc> institutionDocs) {
        for (InstitutionDoc institutionDoc : institutionDocs) {
            esUtileService.addDoc("institutions", institutionDoc);
        }
    }


    @RequestMapping(value = "/searchInstitutionByName", method = RequestMethod.GET)
    public JSONObject searchInstitutionByName(String name) {
        HashMap<String, Object> andMap = new HashMap<>();
        JSONArray arr = new JSONArray();
        JSONObject ret = new JSONObject();
        ret.put("count", 0);
        int count = 0;
        andMap.put("iname", name);
        try {
            PageResult<JSONObject> pageResult = esUtileService.conditionSearch("institutions", 1, 10,  "",
                    andMap, null, null, null);
            for(JSONObject object : pageResult) {
                arr.add(object);
                count++;
            }
            ret.put("results", arr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ret.put("count", count);
        return ret;
    }

    @RequestMapping(value = "/finalCrawler", method = RequestMethod.GET)
    public JSONObject crawlLargeInstitutionsAndTopAuthors(String continent){
        int page = 1;
        int totalInstitutionCount = 25;
        int count = 0;
        int tries = 0;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        ArrayList<InstitutionDoc> institutionDocs = new ArrayList<>();
        nameValuePairs.add(0, new BasicNameValuePair("filter", "cited_by_count:>370000,continent:" + continent));
        nameValuePairs.add(1, new BasicNameValuePair("sort", "cited_by_count:desc"));
        while (count < totalInstitutionCount) {
            tries ++;
            nameValuePairs.add(2, new BasicNameValuePair("page", Integer.toString(page)));
            String requestString = "";
            try {
                URI uri = new URIBuilder("https://api.openalex.org/institutions")
                        .addParameters(nameValuePairs)
                        .build();
                requestString = uri.toString();
            } catch (Exception e) {

            }
            nameValuePairs.remove(2);
            System.out.println("Request URL:" + requestString);
            String response = HttpUtils.handleRequestURL(requestString);
            try {
                JSONObject res = JSONObject.parseObject(response);
                totalInstitutionCount = res.getJSONObject("meta").getInteger("count");
                JSONArray arr = res.getJSONArray("results");
                InstitutionCrawler institutionCrawler = new InstitutionCrawler("none");
                for (int i = 0; i < arr.size(); i++) {
                    InstitutionDoc institutionDoc = institutionCrawler.json2Doc(arr.getJSONObject(i).toJSONString());
                    institutionDoc.setIresearchers(crawlTopTwentyResearchers(institutionDoc.getIID()));
                    addDoc("institutions", institutionDoc);
                    institutionDocs.add(institutionDoc);
                    count++;
//                    if(count >= 402) {
//                        break;
//                    }
                }
                System.out.printf("Page %d crawl success, current institution progress %d / %d\n", page, count, totalInstitutionCount);
                page++;
            } catch (Exception e) {

            }
            if (tries >= 40) {
                break;
            }
        }
        JSONObject ret = new JSONObject();
        ret.put("instituteCount", count);
        ret.put("totalInstitutionCount", totalInstitutionCount);
        return ret;
    }

    public ArrayList<String> crawlTopTwentyResearchers(String IID) {
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(0, new BasicNameValuePair("filter", "last_known_institution.id:" + IID));
        nameValuePairs.add(1, new BasicNameValuePair("sort", "cited_by_count:desc"));
        String requestString = "";
        try {
            URI uri = new URIBuilder("https://api.openalex.org/authors")
                    .addParameters(nameValuePairs)
                    .build();
            requestString = uri.toString();
        } catch (Exception e) {

        }
        System.out.println("    Author request URL: " + requestString);
        String response = HttpUtils.handleRequestURL(requestString);
        JSONArray arr = JSONObject.parseObject(response).getJSONArray("results");
        for (int i = 0; i < 12; i++) {
            ResearcherDoc researcherDoc = ResearcherCrawler.parseOpenAlexResearcherInfo(arr.getJSONObject(i));
            esUtileService.addDoc("researcher", researcherDoc);
            ret.add(researcherDoc.getRID());
        }
        System.out.println(ret);
        return ret;
    }

    @RequestMapping(value = "/gs", method = RequestMethod.GET)
    public void getCoAuthors() {
        int targetLevel = 4;
        ConceptStorage conceptStorage = new ConceptStorage();
        JSONArray currentConcepts = conceptStorage.searchConceptByLevelAndAncestor("C41008148", 1);
        for (int i = 0; i < currentConcepts.size(); i++) {
            JSONObject currentConcept = currentConcepts.getJSONObject(i);
            String Cname = currentConcept.getString("cname");
            String CID = currentConcept.getString("cID");
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

}
