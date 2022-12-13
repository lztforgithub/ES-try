package ES.Common;

import ES.Crawler.ResearcherCrawler;
import ES.Crawler.WorkCrawler;
import ES.Document.ResearcherDoc;
import ES.Document.VenueDoc;
import ES.Document.WorkDoc;
import ES.storage.ResearcherStorage;
import ES.storage.WorkStorage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
        JSONObject firstDoc = new JSONObject();
        try {
            firstDoc = JSONObject.parseObject(response).getJSONArray("results").getJSONObject(0);
            WorkCrawler workCrawler = new WorkCrawler("none");
            WorkDoc workDoc = workCrawler.json2Doc(firstDoc.toJSONString());
            if (workDoc == null) {
                System.out.println("Work not found: " + url);
                workDoc = new WorkDoc();
                workDoc.setPID("ERROR");
                return workDoc;
            }
            // System.out.println("Check crawler: " + workDoc.getPID());
            return workDoc;
        } catch (Exception e) {
            System.out.println("Work not found: " + url);
            WorkDoc workDoc = new WorkDoc();
            workDoc.setPID("ERROR");
            return workDoc;
        }

    }


    /**
     * 返回false，代表不相关；返回true，代表相关
     */
    public static boolean checkVenueConceptScore(VenueDoc venueDoc, String CID, int pos) {
        ArrayList<String> conceptIDs = venueDoc.getVconceptIDs();
        for (int i = 0; i < conceptIDs.size(); i++) {
            if (i >= pos) {
                break;
            }
            String currentConcept = conceptIDs.get(i);
            if (currentConcept.equals(CID)) {
                System.out.printf("%s related.\n", venueDoc.getVfullname());
                return true;
            }
        }
        System.out.printf("%s not related.\n", venueDoc.getVfullname());
        return false;
    }

    /**
     * 返回false，代表不相关；返回true，代表相关
     */
    public static boolean checkVenueConceptScore(JSONObject venueJSON, String CID, int pos) {
        JSONArray conceptIDs = venueJSON.getJSONArray("vconceptIDs");
        for (int i = 0; i < conceptIDs.size(); i++) {
            if (i >= pos) {
                break;
            }
            String currentConcept = conceptIDs.getString(i);
            if (currentConcept.equals(CID)) {
//                System.out.printf("%s related.\n", venueJSON.getString("vfullname"));
                return true;
            }
        }
//        System.out.printf("%s not related.\n", venueJSON.getString("vfullname"));
        return false;
    }

    public static boolean checkWorkConceptRelevance(JSONObject work, String Cname, int pos) {
        JSONArray arr = work.getJSONArray("pconcepts");
        for (int i = 0; i < arr.size(); i++) {
            String cur = arr.getString(i);
            if (Cname.equals(cur)) {
//                System.out.printf("%s related to %s. \n", work.getString("pID"), Cname);
                return true;
            }
            if ( (i + 1) > pos) {
//                System.out.printf("%s not related to %s. \n", work.getString("pID"), Cname);
                return false;
            }
        }
        return false;
    }


    public static void parseWorkDocForCompleteInformation(WorkDoc workDoc) {
        // 爬取相关文献
        CrawlerUtils.crawlRelatedDocs(workDoc);
        // 爬取引用文献
        CrawlerUtils.crawlReferencedDocs(workDoc);
        // 爬取作者
        CrawlerUtils.crawlDocsResearchers(workDoc);
        // 存储自己！
        if (CrawlerUtils.checkDocExist("works", workDoc.getPID()) == 0) {
            System.out.printf("    Store new work: [%s]%s\n", workDoc.getPID(), workDoc.getPname());
            esUtileService.addDoc("works", workDoc);
        }
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

            if (workDoc.getPID().equals("ERROR")) {
                continue;
            }

            esUtileService.addDoc("works", workDoc);
            crawledDocsIDs.add(workDoc.getPID());
            counter++;
            if (counter >= 10) {
                break;
            }
            ret.add(workDoc);
        }

        System.out.println("        Crawled following new related docs:" + crawledDocsIDs);

        return ret;
    }

    public static ArrayList<WorkDoc> crawlReferencedDocs(WorkDoc originDoc) {

        ArrayList<WorkDoc> ret = new ArrayList<>();

        ArrayList<String> referenceDocsIDs = originDoc.getPreferences();
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

            if (workDoc.getPID().equals("ERROR")) {
                continue;
            }


            esUtileService.addDoc("works", workDoc);
            crawledDocsIDs.add(workDoc.getPID());
            count++;
            if (count >= 10) {
                break;
            }
            ret.add(workDoc);
        }

        System.out.println("        Crawled following new referenced docs: " + crawledDocsIDs);

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
                if (temp.size() == 0) {
                    System.out.println("Researcher not found: " + researcherID);
                    continue;
                }
                ResearcherDoc researcherDoc = temp.get(0);
                esUtileService.addDoc("researcher", researcherDoc);
//                System.out.println("        Get new researcher: " + temp.get(0).getRname());
                ret.add(researcherDoc);
                crawledResearchersIDs.add(researcherDoc.getRID());
                count++;
                if (count >= 10) {
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