package ES.storage;

import ES.Common.EsUtileService;
import ES.Crawler.InstitutionCrawler;
import ES.Crawler.WorkCrawler;
import ES.Document.InstitutionDoc;
import ES.Document.WorkDoc;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public void addDoc(String indexName, InstitutionDoc institutionDoc)
    {
        esUtileService.addDoc(indexName, institutionDoc);
    }
}
