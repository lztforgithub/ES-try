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

import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

@Component
@RestController
public class WorkStorage {

    private int layer = 0;

    EsUtileService esUtileService = new EsUtileService();

    @RequestMapping(value = "/addIndex", method = RequestMethod.PUT)
    public void addIndex(String indexName)
    {
        esUtileService.createIndex(indexName.toLowerCase());
    }


    @RequestMapping(value="/storeWork", method = RequestMethod.PUT)
    public void storeWork(String url)
    {
        if(layer==0)
        {
            WorkCrawler workCrawler = new WorkCrawler(url);
            WorkStorage workStorage = new WorkStorage();
            WorkDoc workDoc = workCrawler.run();
            workStorage.addDoc("works", workDoc);
            System.out.println("store "+url+" doc done.");
        }
        else
        {
            WorkCrawler workCrawler = new WorkCrawler(url, 1);
            WorkStorage workStorage = new WorkStorage();
            WorkDoc workDoc = workCrawler.run();
            workStorage.addDoc("works", workDoc);
            System.out.println("store "+url+" doc done.");
        }
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

    public WorkStorage(int layer) {
        this.layer = layer;
    }

    public WorkStorage() {}

    public static void main(String[] args) throws IOException {
        FileReader fileReader = new FileReader("C:\\Users\\1\\IdeaProjects\\1129\\ES-try\\works_urls.txt");
        String urls_string = "";
        int c = 0;
        while((c=fileReader.read())!=-1)
        {
            urls_string += (char)c;
        }
        String[] urls = urls_string.split("\n");
        for(String url:urls)
        {
            WorkStorage workStorage = new WorkStorage();
            workStorage.storeWork(url);
        }
        System.out.println("----done----");
        fileReader.close();
    }
}
