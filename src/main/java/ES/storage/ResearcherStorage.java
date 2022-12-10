package ES.storage;

import ES.Common.AlexUtils;
import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Crawler.ConceptCrawler;
import ES.Crawler.ResearcherCrawler;
import ES.Document.ConceptDoc;
import ES.Document.ResearcherDoc;
import ES.Service.ServiceImpl.VenueServiceImpl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RestController
public class ResearcherStorage {
    EsUtileService esUtileService = new EsUtileService();

    public void addIndex(String indexName)
    {
        esUtileService.createIndex(indexName.toLowerCase());
    }

    public void addDoc(String indexName, ResearcherDoc researcherDoc)
    {
        esUtileService.addDoc(indexName, researcherDoc);
    }

    @RequestMapping(value = "/storeResearchers", method = RequestMethod.PUT)
    public void storeResearcherByURL(String url) {
        ArrayList<ResearcherDoc> researcherDocs = ResearcherCrawler.getResearchersByURL(url);
        System.out.println("Total researchers found:" + researcherDocs.size());
        for (ResearcherDoc researcherDoc : researcherDocs) {
            addDoc("researcher", researcherDoc);
        }
    }

    @RequestMapping(value = "/searchRID", method = RequestMethod.GET)
    public JSONObject searchResearcherById(String ID) {
        // 清除不需要的部分
        esUtileService.deleteDuplicateDoc("researcher", "rID", ID);
        return esUtileService.queryDocById("researcher", ID);
    }

    @RequestMapping(value = "/searchRname", method = RequestMethod.GET)
    public JSONObject searchResearcherByName(String name) {
        HashMap<String, Object> dimAndMap = new HashMap<>();
        dimAndMap.put("rname", name);
        JSONArray arr = new JSONArray();
        JSONObject ret = new JSONObject();
        ret.put("count", 0);
        int count = 0;
        try {
            PageResult<JSONObject> pageResult = esUtileService.conditionSearch("researcher", 1, 10, "",
                    null, null, dimAndMap, null);
            for (JSONObject object : pageResult) {
//                System.out.println(object);
                arr.add(object);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ret.put("results", arr);
        ret.put("count", count);
        System.out.println(ret.getInteger("count"));
        return ret;
    }

    @RequestMapping(value = "/searchRnameRinstitute", method = RequestMethod.GET)
    public JSONObject searchResearcherByNameAndInstitution(String name, String institutionName) {
        // System.out.println("Params:" + name + " " + institutionName);
        JSONObject ret = new JSONObject();
        HashMap<String, Object> dimAndMap = new HashMap<>();
        dimAndMap.put("rname", name);
        dimAndMap.put("rinstitute", institutionName);
        JSONArray arr = new JSONArray();
        ret.put("count", 0);
        int count = 0;
        try {
            PageResult<JSONObject> pageResult = esUtileService.conditionSearch("researcher", 1, 10, "",
                    dimAndMap, null, null, null);
            for (JSONObject object : pageResult) {
//                System.out.println(object);
                arr.add(object);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ret.put("results", arr);
        ret.put("count", count);
        System.out.println(ret.getInteger("count"));
        return ret;
    }


    @RequestMapping(value = "/storeAndGetResearchers", method = RequestMethod.GET)
    public JSONArray storeAndGetResearchers(String name, String institutionName) {
        JSONObject result = new JSONObject();
        JSONArray ret = new JSONArray();
        result.put("status", -1);
        String requestURL = "https://api.openalex.org/authors?";
        requestURL += "filter=display_name.search:" + name.replace(' ', '+');
        // 查institution ID
        String institutionRequestURL = "https://api.openalex.org/institutions?filter=display_name.search:" + institutionName.replace(' ', '+');
//        System.out.println(institutionRequestURL);
        InstitutionStorage institutionStorage = new InstitutionStorage();
        // 爬取一遍institution
        institutionStorage.storeInstitutions(institutionRequestURL);
        // 搜索institution
        JSONObject temp = institutionStorage.searchInstitutionByName(institutionName);
        if (temp.getInteger("count") == 0) {
            // 出错
            return ret;
        }
        JSONArray arr = temp.getJSONArray("results");
        // 默认拉取第一个
        String targetInstitutionID = arr.getJSONObject(0).getString("iID");
        requestURL += ",last_known_institution.id:" + targetInstitutionID;
//        System.out.println("Request string:");
//        System.out.println(requestURL);
        ResearcherStorage researcherStorage = new ResearcherStorage();
        // 爬取并存储学者
        researcherStorage.storeResearcherByURL(requestURL);
        // 在已经存储的学者中按照名字和机构查找
        result = researcherStorage.searchResearcherByNameAndInstitution(name, institutionName);

        // System.out.println("Find " + result.getJSONArray("results").size() + " entries.");
        // 出错
        if (result.getInteger("count") == 0) {
            return ret;
        }
        ret = result.getJSONArray("results");
//        System.out.println(ret.getJSONObject(0).getString("rname"));
        return ret;
    }


    public static void main(String[] args) {

    }
}
