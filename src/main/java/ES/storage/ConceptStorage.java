package ES.storage;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Crawler.ConceptCrawler;
import ES.Document.ConceptDoc;
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
import java.util.List;

@Component
@RestController
public class ConceptStorage {
    EsUtileService esUtileService = new EsUtileService();

    public void addIndex(String indexName)
    {
        esUtileService.createIndex(indexName.toLowerCase());
    }

    public void addDoc(String indexName, ConceptDoc conceptDoc)
    {
        esUtileService.addDoc(indexName, conceptDoc);
    }

    @RequestMapping(value = "/storeConcepts", method = RequestMethod.PUT)
    public void storeConceptByURL(String url) {
        ArrayList<ConceptDoc> conceptDocs = ConceptCrawler.getConceptsByURL(url);
        System.out.println("Total concepts found:" + conceptDocs.size());
        for (ConceptDoc conceptDoc : conceptDocs) {
            addDoc("concept", conceptDoc);
        }
    }

    @RequestMapping(value = "/searchConceptById", method = RequestMethod.GET)
    public JSONObject searchConceptById(String ID) {
        // 清除不需要的部分
        esUtileService.deleteDuplicateDoc("concept", "cID", ID);
        return esUtileService.queryDocById("concept", ID);
    }

    @RequestMapping(value = "/conditionSearchConcept", method = RequestMethod.GET)
    public void searchConceptByLevelAndName(String name, int level) {
        HashMap<String, Object> andMap = new HashMap<>();
        andMap.put("clevel", level);
        HashMap<String, Object> dimAndMap = new HashMap<>();
        dimAndMap.put("cname", name);
        try {
            PageResult<JSONObject> pageResult = esUtileService.conditionSearch("concept", 1, 20, "",
                    andMap, null, dimAndMap, null);
            for (JSONObject object : pageResult) {
                System.out.println(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 爬取ES数据库中，自身等级为level且祖先ID含ancestorID的所有概念。
     * @param ancestorID
     * @param level
     * @return
     */
    @RequestMapping(value = "/gs2", method = RequestMethod.GET)
    public JSONArray searchConceptByLevelAndAncestor(String ancestorID, int level) {
        JSONArray ret = new JSONArray();

        HashMap<String, Object> andMap = new HashMap<>();
        if (level != 0){
            andMap.put("cancestorID", ancestorID);
        }
        andMap.put("clevel", level);

        try {
            PageResult<JSONObject> pageResult = esUtileService.conditionSearch("concept", 1, 35, "",
                    andMap, null, null, null);
            for (JSONObject object : pageResult) {
                // System.out.println(object);
                ret.add(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }
}
