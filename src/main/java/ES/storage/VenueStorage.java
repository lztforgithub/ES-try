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
import com.alibaba.fastjson.JSON;
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
import java.lang.reflect.Array;
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
    public void crawlVenues(String ancestorID, int level, int number, int mode) {
        ConceptStorage conceptStorage = new ConceptStorage();
        JSONArray currentConcepts = conceptStorage.searchConceptByLevelAndAncestor(ancestorID, level);
        int tries = 0;
        for (int i = 0; i < currentConcepts.size(); i++) {
            tries++;

            if (number > (i + 1)) {
                continue;
            }

            JSONObject currentConcept = currentConcepts.getJSONObject(i);
            String CID = currentConcept.getString("cID");
            String Cname = currentConcept.getString("cname");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("filter", "type:journal,x_concepts.id:" + CID));
            nameValuePairs.add(new BasicNameValuePair("sort", "cited_by_count:desc"));
            String requestString = "";
            try {
                URI uri = new URIBuilder("https://api.openalex.org/venues")
                        .addParameters(nameValuePairs)
                        .build();
                requestString = uri.toString();
            } catch (Exception e) {

            }

            int num = 5;

            System.out.println(requestString);

            System.out.printf("Crawling %d venues by concept: [%s]%s\n", 75, CID, Cname);
            ArrayList<VenueDoc> venueDocs = storeVenuesByURL(requestString, 75);
            // 接着爬取论文
            // 2017年以来，排名前20的文章

            int crawledVenuesCount = 0;
            int broken = 5;
            if(mode == 1)
                continue;

            for (VenueDoc venueDoc : venueDocs) {

                // 先检查相关性
                // 一级概念相关性排名在5及以内

                if(!CrawlerUtils.checkVenueConceptScore(venueDoc, CID, 3)) {
                    continue;
                }


                // 确认有关系了再开始爬
                crawledVenuesCount++;

//                // 检查这个Venue是否已经爬取过
//                if (CrawlerUtils.checkDocExist("venue", venueDoc.getVID()) == 1) {
//                    // 已经爬取过了
//                    System.out.printf("    Duplicated venue [%s]%s\n", venueDoc.getVID(), venueDoc.getVfullname());
//                    continue;
//                }

                // 跳过crawledVenuesCount = 1之前的部分
                if (crawledVenuesCount <= 0) {
                    continue;
                }


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
                int venueWorksCount = 0;
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
                    if (CrawlerUtils.checkDocExist("works", workDoc.getPID()) == 0) {
                        System.out.printf("    Store new work: [%s]%s, progress %d / 20\n", workDoc.getPID(), workDoc.getPname(), j + 1);
                        esUtileService.addDoc("works", workDoc);
                    }
                    venueWorksCount++;
                    if (venueWorksCount >= 10) {
                        break;
                    }
                }
                System.out.printf("    Finished crawling top 20 papers of [%s]%s, progress %d / %d\n", VID, Vfullname, crawledVenuesCount, num);
//                if (tries >= 1) {
//                    break;
//                }
                if (crawledVenuesCount >= num) {
                    break;
                }
            }
            if (tries >= 8) {
                break;
            }
        }
    }


    @RequestMapping(value = "/crawlWorksByResearcherID", method = RequestMethod.GET)
    public void crawlWorksByResearcherID(String RID) {
        String baseURL = "https://api.openalex.org/works?filter=authorships.author.id:" + RID;
        int page = 0;
        int count = 0;
        int tries = 0;
        int totalCrawledWorks = 75;
        WorkCrawler workCrawler = new WorkCrawler("none");

        while(count < totalCrawledWorks) {
            page++;
            String requestURL = baseURL + "&page=" + page;
            System.out.println(requestURL);
            String response = HttpUtils.handleRequestURL(requestURL);
            JSONObject responseJSON = JSONObject.parseObject(response);
//            System.out.println(responseJSON);
            totalCrawledWorks = responseJSON.getJSONObject("meta").getInteger("count");
            JSONArray arr = responseJSON.getJSONArray("results");
            for (int i = 0; i < arr.size(); i++) {
                JSONObject object = arr.getJSONObject(i);
                WorkDoc workDoc = workCrawler.json2Doc(object.toJSONString());

                System.out.printf("    Get new work: [%s]%s\n", workDoc.getPID(), workDoc.getPname());
                CrawlerUtils.parseWorkDocForCompleteInformation(workDoc);
                count++;
            }
        }
    }

    @RequestMapping(value = "/crawlWorksByCSConcept", method = RequestMethod.GET)
    public void crawlWorksByCSConcept(){
        ConceptStorage conceptStorage = new ConceptStorage();
        WorkCrawler workCrawler = new WorkCrawler("none");

        // 爬取所有一级概念
        JSONArray arr = conceptStorage.searchConceptByLevelAndAncestor("C41008148", 1);
//        System.out.println("Recommend paper concepts:" + arr.size());

        for (int i = 0; i < arr.size(); i++) {
            JSONObject currentConcept = arr.getJSONObject(i);
            String CID = currentConcept.getString("cID");
            String Cname = currentConcept.getString("cname");
            System.out.println("Crawling works from: " + Cname);
            String baseURL = "https://api.openalex.org/works";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("filter", "concepts.id:" + CID + ",publication_year:>2015"));
            nameValuePairs.add(new BasicNameValuePair("sort", "cited_by_count:desc"));
            baseURL = HttpUtils.buildURL(nameValuePairs, baseURL);
            int page = 0;
            int count = 0;
            int tries = 0;
            int total_page = 0;
            int totalCrawlWorks = 10;

            while (count < Math.min(10, totalCrawlWorks)) {
                page++;
                String requestURL = baseURL + "&page=" + page;
                String response = HttpUtils.handleRequestURL(requestURL);
                System.out.println(requestURL);
                JSONObject responseJSON = JSONObject.parseObject(response);
//            System.out.println(responseJSON);
                totalCrawlWorks = responseJSON.getJSONObject("meta").getInteger("count");
                total_page = totalCrawlWorks / page;
                JSONArray arr1 = responseJSON.getJSONArray("results");
                for (int j = 0; j < arr1.size(); j++) {
                    JSONObject object = arr1.getJSONObject(j);
                    WorkDoc workDoc = workCrawler.json2Doc(object.toJSONString());
                    if (!CrawlerUtils.checkWorkConceptRelevance(workDoc, Cname, 3)) continue;
                    System.out.printf("    Get new work: [%s]%s\n", workDoc.getPID(), workDoc.getPname());
                    if(workDoc.getPname() == null) {
                        continue;
                    }
                    if(workDoc.getPname().length() <= 2) {
                        continue;
                    }
                    CrawlerUtils.parseWorkDocForCompleteInformation(workDoc);
                    count++;
                    if (count >= 10) break;
                }
                if(page >= total_page) break;
            }

        }
    }
}
