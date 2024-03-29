package ES.Crawler;

import ES.Common.HttpUtils;
import ES.Document.WorkDoc;
import ES.storage.ResearcherStorage;
import ES.storage.WorkStorage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class WorkCrawler {
    private String url;

    private int layer = 0;

    private WorkDoc workDoc;

    private String generateAbstract(JSONObject abstract_inverted)
    {
        StringBuffer text = new StringBuffer();
        Set<String> keys = abstract_inverted.keySet();
        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<ArrayList<Integer>> valueList = new ArrayList<>();
        for(String s:keys)
        {
            keyList.add(s);
        }
        for(String s:keyList)
        {
            JSONArray array = abstract_inverted.getJSONArray(s);
            ArrayList<Integer> temp = new ArrayList<>();
            for(int i=0; i< array.size(); i++)
            {
                String n = array.getString(i);
                temp.add(Integer.parseInt(n));
            }
            valueList.add(temp);
        }
        int i = 0;
        while(true)
        {
            boolean flag = false;
            for(ArrayList<Integer> t: valueList)
            {
                if(t.contains(i))
                {
                    flag = true;
                    text.append(keyList.get(valueList.indexOf(t)));
                    text.append(" ");
                }
            }
            if(!flag)
            {
                break;
            }
            i++;
        }
//        System.out.println(text);
        return text.toString();
    }

    /**
     * 爬取单页结果。
     * @param url
     * @param num
     * @return
     */
    public ArrayList<WorkDoc> crawlWorks(String url, int num) {
        ArrayList<WorkDoc> workDocs = new ArrayList<>();

        String response = HttpUtils.handleRequestURL(url);
        JSONObject responseJSON = JSONObject.parseObject(response);
        JSONArray results = responseJSON.getJSONArray("results");
        for(int i = 0; i < num; i++) {
            JSONObject object = results.getJSONObject(i);
            workDocs.add(json2Doc(object.toJSONString()));
        }
        return workDocs;
    }

    public String crawlWork()
    {
        assert this.url != null;
        InputStreamReader reader = null;
        BufferedReader in = null;
        StringBuffer content = new StringBuffer();

        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(1000);
            reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
            in = new BufferedReader(reader);

            String line = null;

            while ((line = in.readLine())!=null)
            {
                content.append(line);
            }
            System.out.println("crawl "+url+" done.");
        } catch (IOException e) {
            System.out.println("can't crawl "+url);;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println("cannot close buffered reader!!!");
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("cannot close inputstream reader!!!");
                }
            }
        }
        return content.toString();
    }

    public WorkDoc json2Doc(String jsonStr)
    {
        WorkDoc workDoc = new WorkDoc();
        JSONObject jsonObject = JSON.parseObject(jsonStr);

        String is_retracted = jsonObject.getString("is_retracted");
        boolean retracted = is_retracted!=null && is_retracted.equals("true");
        String is_paratext = jsonObject.getString("is_paratext");
        boolean paratext = is_paratext!=null && is_paratext.equals("true");

        if(retracted || paratext)
        {
            return null;
        }

        String id = "W"+jsonObject.getString("id").split("W")[1];
        workDoc.setPID(id);

        String doi = jsonObject.getString("doi");
        workDoc.setDOI(doi);

        String title = jsonObject.getString("title");
        workDoc.setPname(title);

        JSONObject host_venue = jsonObject.getJSONObject("host_venue");
        if(host_venue.getString("id")!=null)
        {
            String hostVID = "V"+host_venue.getString("id").split("V")[1];
            workDoc.setP_VID(hostVID);
            String hostVname = host_venue.getString("publisher");
            workDoc.setP_Vname(hostVname);
        }
        else
        {
            workDoc.setP_VID(null);
            workDoc.setP_Vname(null);
        }

        JSONObject access = jsonObject.getJSONObject("open_access");
        String isAccessible = access.getString("is_oa");
        if(isAccessible.equals("true"))
        {
            String oa_url = access.getString("oa_url");
            workDoc.setP_Vurl(oa_url);
        }
        else
        {
            workDoc.setP_Vurl(null);
        }

        JSONArray authors = jsonObject.getJSONArray("authorships");
        for(int i=0; i< authors.size(); i++)
        {
            JSONObject authorinfo = authors.getJSONObject(i);
            JSONObject author = authorinfo.getJSONObject("author");
            String authorID = "A"+author.getString("id").split("A")[1];
            String authorName = author.getString("display_name");
            workDoc.addPauthor(authorID);
            workDoc.addPauthorname(authorName);
        }

        Date date = null;
        String publicationDate = jsonObject.getString("publication_date");
        if(publicationDate!=null)
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date = simpleDateFormat.parse(publicationDate);
            } catch (ParseException e) {
                System.out.println("parse date error");
            }
        }
        workDoc.setPdate(date);

        String citedByCount = jsonObject.getString("cited_by_count");
        if(citedByCount!=null)
        {
            int cited_by_count = Integer.parseInt(citedByCount);
            workDoc.setPcite(cited_by_count);
        }
        else
        {
            workDoc.setPcite(-1);                                             // not have this info
        }

        JSONObject abstract_inverted_index = jsonObject.getJSONObject("abstract_inverted_index");
        if(abstract_inverted_index==null)
        {
            workDoc.setPabstract(null);
        }
        else
        {
            String abstract_text = generateAbstract(abstract_inverted_index);
            workDoc.setPabstract(abstract_text);
            Set<String> words = abstract_inverted_index.keySet();
            for(String s:words)
            {
                workDoc.addPabstractwords(s);
                workDoc.addPabstractcount(abstract_inverted_index.get(s).toString().split(",").length+1);
            }
        }

        JSONArray concepts = jsonObject.getJSONArray("concepts");
        if(concepts!=null)
        {
            for(int i=0; i<concepts.size()&&i<8; i++)
            {
                JSONObject concept = concepts.getJSONObject(i);
                String concept_name = concept.getString("display_name");
                workDoc.addPconcepts(concept_name);
            }
        }

        JSONArray referenced_works = jsonObject.getJSONArray("referenced_works");
        if(referenced_works!=null)
        {
            for(int i=0; i< referenced_works.size(); i++)
            {
                String refer = referenced_works.getString(i);
                String referID = "W"+refer.split("W")[1];
                workDoc.addPreference(refer);
            }
        }

        JSONArray related_works = jsonObject.getJSONArray("related_works");
        if(related_works!=null)
        {
            for(int i=0; i< related_works.size(); i++)
            {
                String relate = related_works.getString(i);
                String relateID = "W"+relate.split("W")[1];
                workDoc.addPrelated(relate);
            }
        }

        String cited_by_api_url = jsonObject.getString("cited_by_api_url");
        workDoc.setPbecited(cited_by_api_url);

        JSONArray citeArray = jsonObject.getJSONArray("counts_by_year");
        int count = 0;
        for(int i=0; i<5; i++)
        {
            if(count<citeArray.size())
            {
                JSONObject citeInfo = citeArray.getJSONObject(count);
                int year = Integer.parseInt(citeInfo.getString("year"));
                while(year<2022-i)
                {
                    workDoc.addPcitednum(0);
                    i++;
                }
                if(i<5)
                {
                    workDoc.addPcitednum(Integer.parseInt(citeInfo.getString("cited_by_count")));
                    count++;
                }
            }
            else
            {
                workDoc.addPcitednum(0);
            }
        }

        String pub_year = jsonObject.getString("publication_year");
        workDoc.addPsystemTags(pub_year);
        int counter = 0;
        if(concepts!=null)
        {
            for(int i=0; i<concepts.size()&&counter<3; i++)
            {
                JSONObject concept = concepts.getJSONObject(i);
                int level = Integer.parseInt(concept.getString("level"));
                if(level>0)
                {
                    String concept_id = "C"+concept.getString("id").split("C")[1];
                    workDoc.addPconcepts(concept_id);
                    counter += 1;
                }
            }
        }


        // System.out.println("generate "+workDoc.getPID()+" doc done.");
        this.workDoc = workDoc;
        return workDoc;
    }

    public ArrayList<String> getFirstFiveResults(String search_url)
    {
        ArrayList<String> ret = new ArrayList<>();
        InputStreamReader reader = null;
        BufferedReader in = null;
        StringBuffer content = new StringBuffer();

        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(1000);
            reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
            in = new BufferedReader(reader);

            String line = null;

            while ((line = in.readLine())!=null)
            {
                content.append(line);
            }
            // System.out.println("crawl "+url+" done.");
        } catch (IOException e) {
            System.out.println("can't crawl "+url);;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println("cannot close buffered reader!!!");
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("cannot close inputstream reader!!!");
                }
            }
        }

        JSONObject response = JSON.parseObject(content.toString());
        JSONArray results = response.getJSONArray("results");
        for(int i=0; i< results.size()&&i<5; i++)
        {
            JSONObject result = results.getJSONObject(i);
            String pid = "W"+result.getString("id").split("W")[1];
            ret.add(pid);
        }

        return ret;
    }
    public WorkCrawler(String url)
    {
        this.url = url;
    }

    public WorkCrawler(String url, int layer)
    {
        this.url = url;
        this.layer = layer;
    }

    public WorkDoc run()
    {
        WorkCrawler workCrawler = new WorkCrawler(url);
        String jsonStr = workCrawler.crawlWork();
//        System.out.println(jsonStr);
        return workCrawler.json2Doc(jsonStr);
    }

    public WorkDoc getWorkDoc() {
        return workDoc;
    }

    public static void main(String[] args) throws IOException {
        FileReader fileReader = new FileReader("works_urls.txt");
        String urls_string = "";
        int c = 0;
        while((c=fileReader.read())!=-1)
        {
            urls_string += (char)c;
        }
        String[] urls = urls_string.split("\n");
        for(String url:urls)
        {
            WorkCrawler workCrawler = new WorkCrawler(url);
            workCrawler.run();
        }
    }
}
