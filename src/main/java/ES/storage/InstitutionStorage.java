package ES.storage;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Crawler.InstitutionCrawler;
import ES.Crawler.WorkCrawler;
import ES.Document.InstitutionDoc;
import ES.Document.WorkDoc;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
