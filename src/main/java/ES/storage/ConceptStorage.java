package ES.storage;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Crawler.ConceptCrawler;
import ES.Document.ConceptDoc;
import ES.Document.WorkDoc;
import com.alibaba.fastjson.JSON;
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

    public void searchConceptByLevel(int level) {
        HashMap<String, Object> andMap = new HashMap<>();
        andMap.put("Clevel", level);
//        PageResult<List<JSON>> pageResult = esUtileService.conditionSearch("concept",
//                1, 10, "", andMap)
    }
}
