package ES.storage;

import ES.Common.EsUtileService;
import ES.Crawler.WorkCrawler;
import ES.Document.WorkDoc;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Component
@RestController
public class WorkStorage {

    EsUtileService esUtileService = new EsUtileService();

    @RequestMapping(value = "/addIndex", method = RequestMethod.PUT)
    public void addIndex(String indexName)
    {
        esUtileService.createIndex(indexName.toLowerCase());
    }


    @RequestMapping(value="/storeWork", method = RequestMethod.PUT)
    public void storeWork(String url)
    {
        WorkCrawler workCrawler = new WorkCrawler(url);
        WorkStorage workStorage = new WorkStorage();
        workStorage.addDoc("works", workCrawler.run());
    }
    public void addDoc(String indexName, WorkDoc workDoc)
    {
        esUtileService.addDoc(indexName, workDoc);
    }


    @RequestMapping(value="/findDocByID", method = RequestMethod.GET)
    public JSONObject findDocByID(String indexName, String ID)
    {
        return esUtileService.queryDocById(indexName, ID);
    }

    public static void main(String[] args) {
        String url = "https://api.openalex.org/works/W1775749144";
        WorkStorage workStorage = new WorkStorage();
        workStorage.storeWork(url);
        System.out.println("----done----");
    }
}
