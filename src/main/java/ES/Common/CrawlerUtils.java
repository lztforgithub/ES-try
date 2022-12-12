package ES.Common;

import ES.Crawler.ResearcherCrawler;
import ES.Crawler.WorkCrawler;
import ES.Document.ResearcherDoc;
import ES.Document.WorkDoc;
import ES.storage.ResearcherStorage;
import ES.storage.WorkStorage;
import com.alibaba.fastjson.JSONObject;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class CrawlerUtils {


    public static EsUtileService esUtileService = new EsUtileService();

    public static int checkDocExist(String indexName, String ID) {
        JSONObject res = esUtileService.queryDocById(indexName, ID);
        // System.out.println("Check exist: " + ID);
        if (res == null) {
            return 0;
        }
        return 1;
    }

    public static WorkDoc frogCrawlSingleDoc(String url) {
        String response = HttpUtils.handleRequestURL(url);
        JSONObject firstDoc = JSONObject.parseObject(response).getJSONArray("results").getJSONObject(0);
        WorkCrawler workCrawler = new WorkCrawler("none");
        WorkDoc workDoc = workCrawler.json2Doc(firstDoc.toJSONString());
        // System.out.println("Check crawler: " + workDoc.getPID());
        return workDoc;
    }

    public static ArrayList<WorkDoc> crawlRelatedDocs(WorkDoc originDoc) {

        ArrayList<WorkDoc> ret = new ArrayList<>();

        ArrayList<String> relatedDocsIDs = originDoc.getPrelated();
        ArrayList<String> crawledDocsIDs = new ArrayList<>();
        int counter = 0;

        for (String relatedDocID : relatedDocsIDs) {
            relatedDocID = AlexUtils.getRawID(relatedDocID);
            // System.out.println("        Crawling related doc ID:" + relatedDocID);

            // 先检查数据库里是否有记录
            if (checkDocExist("works", relatedDocID) == 1) {
                counter++;
                continue;
            }

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("filter", "openalex:" + relatedDocID));
            String requestString = HttpUtils.buildURL(nameValuePairs, "https://api.openalex.org/works");
            // System.out.println("        Request URL:" + requestString);
            WorkDoc workDoc = frogCrawlSingleDoc(requestString);
            esUtileService.addDoc("works", workDoc);
            crawledDocsIDs.add(workDoc.getPID());
            counter++;
            if (counter >= 20) {
                break;
            }
            ret.add(workDoc);
        }

        System.out.println("        Crawled following new docs:" + crawledDocsIDs);

        return ret;
    }

    public static ArrayList<WorkDoc> crawlReferencedDocs(WorkDoc originDoc) {

        ArrayList<WorkDoc> ret = new ArrayList<>();

        ArrayList<String> referenceDocsIDs = originDoc.getPrelated();
        ArrayList<String> crawledDocsIDs = new ArrayList<>();
        int count = 0;

        for (String referenceDocID : referenceDocsIDs) {
            referenceDocID = AlexUtils.getRawID(referenceDocID);

            // 先检查数据库里是否有记录
            if (checkDocExist("works", referenceDocID) == 1) {
                count++;
                continue;
            }

//            System.out.println("        Crawling reference doc ID:" + referenceDocID);
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("filter", "openalex:" + referenceDocID));
            String requestString = HttpUtils.buildURL(nameValuePairs, "https://api.openalex.org/works");
//            System.out.println("        Request URL:" + requestString);
            WorkDoc workDoc = frogCrawlSingleDoc(requestString);
            esUtileService.addDoc("works", workDoc);
            crawledDocsIDs.add(workDoc.getPID());
            count++;
            if (count >= 20) {
                break;
            }
            ret.add(workDoc);
        }

        System.out.println("        Crawled following new docs: " + crawledDocsIDs);

        return ret;
    }

    public static ArrayList<ResearcherDoc> crawlDocsResearchers(WorkDoc originDoc) {

        ArrayList<ResearcherDoc> ret = new ArrayList<>();

        ArrayList<String> researchersIDs = originDoc.getPauthor();
        ArrayList<String> crawledResearchersIDs = new ArrayList<>();
        int count = 0;

        ResearcherStorage researcherStorage = new ResearcherStorage();

        for (String researcherID : researchersIDs) {

//            System.out.println("        Crawing researcher ID:" + researcherID);
            JSONObject result = researcherStorage.searchResearcherById(researcherID);
            if (result == null) {
                // 没有找到
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("filter", "openalex:" + researcherID));
                String requestString = HttpUtils.buildURL(nameValuePairs, "https://api.openalex.org/authors");
//                System.out.println("        Request string: " + requestString);
                ArrayList<ResearcherDoc> temp = ResearcherCrawler.getResearchersByURL(requestString);
                ResearcherDoc researcherDoc = temp.get(0);
                esUtileService.addDoc("researcher", researcherDoc);
//                System.out.println("        Get new researcher: " + temp.get(0).getRname());
                ret.add(researcherDoc);
                crawledResearchersIDs.add(researcherDoc.getRID());
                count++;
                if (count >= 20) {
                    break;
                }
            } else {
//                System.out.printf("        Researcher %s[%s] already exists.\n", researcherID, result.getString("rname"));
            }
        }

        System.out.println("        Crawled researchers: " + crawledResearchersIDs);

        return ret;
    }

}