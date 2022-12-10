package ES.storage;


import ES.Common.EsUtileService;
import ES.Crawler.ConceptCrawler;
import ES.Crawler.VenueCrawler;
import ES.Document.ConceptDoc;
import ES.Document.VenueDoc;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@Component
@RestController
public class VenueStorage {
    EsUtileService esUtileService = new EsUtileService();

    public void addIndex(String indexName)
    {
        esUtileService.createIndex(indexName.toLowerCase());
    }

    public void addDoc(String indexName, VenueDoc venueDoc)
    {
        esUtileService.addDoc(indexName, venueDoc);
    }

    @RequestMapping(value = "/storeFirstPageVenues", method = RequestMethod.PUT)
    public void storeFirstPageVenuesByURL(String url) {
        ArrayList<VenueDoc> venueDocs = VenueCrawler.getVenuesByURL(url);
        System.out.println("Total venues found:" + venueDocs.size());
        for (VenueDoc venueDoc : venueDocs) {
            addDoc("venue", venueDoc);
        }
    }


    @RequestMapping(value = "/storeAllPageVenues", method = RequestMethod.PUT)
    public void storeVenuesByURL(String url, int num) {
        int totalPage = VenueCrawler.getTotalPageCount(url);
        int tries = 0;
        for (int i = 1; i <= totalPage; i++) {
            String requestUrl = url + "&page=" + i;
            ArrayList<VenueDoc> venueDocs = VenueCrawler.getVenuesByURL(requestUrl);
            for (VenueDoc venueDoc : venueDocs){
                addDoc("venue", venueDoc);
            }
        }
    }

    @RequestMapping(value = "/searchVenuesById", method = RequestMethod.GET)
    public JSONObject searchConceptById( String ID) {
        return esUtileService.queryDocById("venue", ID);
    }
}
