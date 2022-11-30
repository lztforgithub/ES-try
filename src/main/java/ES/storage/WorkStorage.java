package ES.storage;

import ES.Common.EsUtileService;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class WorkStorage {

    EsUtileService esUtileService = new EsUtileService();

    @PostMapping("/test")
    public int addIndex()
    {
        esUtileService.createIndex("indexName");
        return 1;

    }

    public void addDoc(String index)
    {

    }
}
