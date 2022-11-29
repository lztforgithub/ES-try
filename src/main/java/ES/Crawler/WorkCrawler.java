package ES.Crawler;

import ES.Document.WorkDoc;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public WorkDoc workDoc;

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
        boolean retracted = is_retracted.equals("true");
        String is_paratext = jsonObject.getString("is_paratext");
        boolean paratext = is_paratext.equals("true");

        if(retracted || paratext)
        {
            return null;
        }

        String id = jsonObject.getString("id");
        workDoc.setPID(id);

        String doi = jsonObject.getString("doi");
        workDoc.setDOI(doi);

        String title = jsonObject.getString("title");
        workDoc.setPname(title);

        JSONObject host_venue = jsonObject.getJSONObject("host_venue");
        String hostVID = host_venue.getString("id");
        workDoc.setP_VID(hostVID);
        String hostVname = host_venue.getString("publisher");
        workDoc.setP_Vname(hostVname);

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
            String authorID = author.getString("id");
            workDoc.addPauthor(authorID);
        }

        Date date = null;
        String publicationDate = jsonObject.getString("publication_date");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = simpleDateFormat.parse(publicationDate);
        } catch (ParseException e) {
            System.out.println("parse date error");
        }
        assert date!=null;
        workDoc.setPdate(date);

        int cited_by_count = Integer.parseInt(jsonObject.getString("cited_by_count"));
        workDoc.setPcite(cited_by_count);

        JSONObject abstract_inverted_index = jsonObject.getJSONObject("abstract_inverted_index");
        String abstract_text = generateAbstract(abstract_inverted_index);
        workDoc.setPabstract(abstract_text);

        JSONArray concepts = jsonObject.getJSONArray("concepts");
        for(int i=0; i<concepts.size(); i++)
        {
            JSONObject concept = concepts.getJSONObject(i);
            String concept_name = concept.getString("display_name");
            workDoc.addPconcepts(concept_name);
        }

        JSONArray referenced_works = jsonObject.getJSONArray("referenced_works");
        for(int i=0; i< referenced_works.size(); i++)
        {
            String refer = referenced_works.getString(i);
            workDoc.addPreference(refer);
        }

        JSONArray related_works = jsonObject.getJSONArray("related_works");
        for(int i=0; i< related_works.size(); i++)
        {
            String relate = related_works.getString(i);
            workDoc.addPrelated(relate);
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
        System.out.println("generate doc done.");
        this.workDoc = workDoc;
        return workDoc;
    }
    public WorkCrawler(String url)
    {
        this.url = url;
    }

    public void run()
    {
        WorkCrawler workCrawler = new WorkCrawler(url);
        String jsonStr = workCrawler.crawlWork();
        workCrawler.json2Doc(jsonStr);
    }

    public static void main(String[] args) {
        WorkCrawler workCrawler = new WorkCrawler("https://api.openalex.org/works/W2741809807");
        workCrawler.run();
    }
}
