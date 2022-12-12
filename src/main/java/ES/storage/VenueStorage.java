package ES.storage;


import ES.Common.CrawlerUtils;
import ES.Common.EsUtileService;
import ES.Common.HttpUtils;
import ES.Crawler.ConceptCrawler;
import ES.Crawler.VenueCrawler;
import ES.Crawler.WorkCrawler;
import ES.Document.ConceptDoc;
import ES.Document.VenueDoc;
import ES.Document.WorkDoc;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Basic;
import java.net.URI;
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
    public ArrayList<VenueDoc> storeVenuesByURL(String url, int num) {
        ArrayList<VenueDoc> ret = new ArrayList<>();
        int totalPage = VenueCrawler.getTotalPageCount(url);
        int count = 0;
        for (int i = 1; i <= totalPage; i++) {
            String requestUrl = url + "&page=" + i;
//            System.out.printf("Request page %d with url %s\n", i, requestUrl);
            ArrayList<VenueDoc> venueDocs = VenueCrawler.getVenuesByURL(requestUrl);
            for (VenueDoc venueDoc : venueDocs){
                addDoc("venue", venueDoc);
                ret.add(venueDoc);
                count++;
                if(count >= num) {
//                    System.out.printf("Reached %d limit.\n", num);
                    return ret;
                }
            }
        }
        return ret;
    }

    @RequestMapping(value = "/searchVenuesById", method = RequestMethod.GET)
    public JSONObject searchConceptById( String ID) {
        return esUtileService.queryDocById("venue", ID);
    }

    @RequestMapping(value = "/gs", method = RequestMethod.GET)
    public void crawlVenues(String ancestorID, int level) {
        ConceptStorage conceptStorage = new ConceptStorage();
        JSONArray currentConcepts = conceptStorage.searchConceptByLevelAndAncestor(ancestorID, level);
        int tries = 0;
        for (int i = 0; i < currentConcepts.size(); i++) {
            tries++;
            JSONObject currentConcept = currentConcepts.getJSONObject(i);
            String CID = currentConcept.getString("cID");
            String Cname = currentConcept.getString("cname");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("filter", "type:conference,x_concepts.id:" + CID));
            nameValuePairs.add(new BasicNameValuePair("sort", "cited_by_count:desc"));
            String requestString = "";
            try {
                URI uri = new URIBuilder("https://api.openalex.org/venues")
                        .addParameters(nameValuePairs)
                        .build();
                requestString = uri.toString();
            } catch (Exception e) {

            }
            int num = 0;
            if (i < 10) {
                num = 8;
            } else {
                num = 5;
            }
            System.out.printf("Crawling %d venues by concept: [%s]%s\n", num, CID, Cname);
            ArrayList<VenueDoc> venueDocs = storeVenuesByURL(requestString, num);
            // 接着爬取论文
            // 2017年以来，排名前20的文章
            for (VenueDoc venueDoc : venueDocs) {
                String Vfullname = venueDoc.getVfullname();
                String VID = venueDoc.getVID();
                System.out.printf("    Crawling top 20 papers of [%s]%s\n", VID, Vfullname);

                nameValuePairs.clear();
                nameValuePairs.add(new BasicNameValuePair("filter", "publication_year:>2017,host_venue.id:" + VID));
                nameValuePairs.add(new BasicNameValuePair("sort", "cited_by_count:desc"));

                String requestURL = HttpUtils.buildURL(nameValuePairs, "https://api.openalex.org/works");
                System.out.printf("    Request URL: %s\n", requestURL);
                String response = HttpUtils.handleRequestURL(requestURL);

                JSONObject responseJSON = JSONObject.parseObject(response);
                JSONArray arr = responseJSON.getJSONArray("results");
                WorkCrawler workCrawler = new WorkCrawler("none");
                for (int j = 0; j < arr.size(); j++) {
                    JSONObject object = arr.getJSONObject(j);
                    WorkDoc workDoc = workCrawler.json2Doc(object.toJSONString());

                    System.out.printf("    Get new work: [%s]%s\n", workDoc.getPID(), workDoc.getPname());

                    // 爬取相关文献
                    CrawlerUtils.crawlRelatedDocs(workDoc);
                    // 爬取引用文献
                    CrawlerUtils.crawlReferencedDocs(workDoc);
                    // 爬取作者
                    CrawlerUtils.crawlDocsResearchers(workDoc);

//                    if (tries >= 1) {
//                        break;
//                    }
                    // 存储自己！
                    esUtileService.addDoc("works", workDoc);

                }
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
