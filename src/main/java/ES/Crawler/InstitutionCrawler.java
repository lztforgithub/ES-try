package ES.Crawler;

import ES.Common.AlexUtils;
import ES.Common.HttpUtils;
import ES.Common.PageResult;
import ES.Document.InstitutionDoc;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class InstitutionCrawler {
    private String url;

    public String crawlInstitution()
    {
        assert url != null;
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

    public ArrayList<InstitutionDoc> crawlInstitutions(String url) {
        String response = HttpUtils.handleRequestURL(url);
        JSONObject responseJSON;
        ArrayList<InstitutionDoc> institutionDocs = new ArrayList<>();
        try {
            responseJSON = JSONObject.parseObject(response);
            JSONArray arr = responseJSON.getJSONArray("results");
            for (int i = 0; i < arr.size(); i++) {
                String singleInstitution = arr.getJSONObject(i).toString();
                System.out.println("Crawing Institute: " + arr.getJSONObject(i).getString("display_name"));
                institutionDocs.add(json2Doc(singleInstitution));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return institutionDocs;

    }

    public InstitutionDoc json2Doc(String jsonStr)
    {
        InstitutionDoc institutionDoc = new InstitutionDoc();
        JSONObject jsonObject = JSON.parseObject(jsonStr);

        String id = AlexUtils.getRawID(jsonObject.getString("id"));
        institutionDoc.setIID(id);

        String displayName = jsonObject.getString("display_name");
        institutionDoc.setIname(displayName);

        String countryCode = jsonObject.getString("country_code");
        institutionDoc.setIcountry(countryCode);

        String type = jsonObject.getString("type");
        institutionDoc.setItype(type);

        String homepageURL = jsonObject.getString("homepage_url");
        institutionDoc.setIhomepage(homepageURL);

        String imageURL = jsonObject.getString("image_url");
        institutionDoc.setIimage(imageURL);

        JSONArray acronyms = jsonObject.getJSONArray("display_name_acronyms");
        if(acronyms!=null)
        {
            for(int i=0; i<acronyms.size(); i++)
            {
                String s = acronyms.getString(i);
                institutionDoc.addIacronyms(s);
            }
        }

        JSONArray altername = jsonObject.getJSONArray("display_name_alternatives");
        if(altername!=null)
        {
            for(int i=0; i< altername.size(); i++)
            {
                String s = altername.getString(i);
                institutionDoc.addIaltername(s);
            }
        }

        int worksCount = Integer.parseInt(jsonObject.getString("works_count"));
        institutionDoc.setIworksum(worksCount);

        int cited_by_count = Integer.parseInt(jsonObject.getString("cited_by_count"));
        institutionDoc.setIcitednum(cited_by_count);

        JSONObject international = jsonObject.getJSONObject("international");
        if(international!=null)
        {
            JSONObject display_name = international.getJSONObject("display_name");
            if(display_name!=null)
            {
                String zh_cn = display_name.getString("zh-cn");
                institutionDoc.setIchinesename(zh_cn);
            }
            else
            {
                institutionDoc.setIchinesename(null);
            }
        }
        else
        {
            institutionDoc.setIchinesename(null);
        }


        JSONArray associate = jsonObject.getJSONArray("associated_institutions");
        if(associate!=null)
        {
            for(int i=0; i<associate.size(); i++)
            {
                JSONObject assoInfo = associate.getJSONObject(i);
                String assoID = assoInfo.getString("id");
                institutionDoc.addIassociations(assoID);
                String assoRelation = assoInfo.getString("relationship");
                institutionDoc.addIrelation(assoRelation);
            }
        }

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
                    institutionDoc.addIcited(0);
                    institutionDoc.addIcount(0);
                    i++;
                }
                if(i<5)
                {
                    institutionDoc.addIcited(Integer.parseInt(citeInfo.getString("cited_by_count")));
                    institutionDoc.addIcount(Integer.parseInt(citeInfo.getString("works_count")));
                    count++;
                }
            }
            else
            {
                institutionDoc.addIcited(0);
                institutionDoc.addIcount(0);
            }
        }

        JSONArray concepts = jsonObject.getJSONArray("x_concepts");
        if(concepts!=null)
        {
            for(int i=0; i<concepts.size(); i++)
            {
                JSONObject conceptInfo = concepts.getJSONObject(i);
                String concept = conceptInfo.getString("display_name");
                institutionDoc.addIconcept(concept);
            }
        }

        String worksURL = jsonObject.getString("works_api_url");
        institutionDoc.setIworksURL(worksURL);
        System.out.println("generate "+institutionDoc.getIID()+" doc done.");
        return institutionDoc;
    }

    public InstitutionCrawler(String url)
    {
        this.url = url;
    }

    public InstitutionDoc run()
    {
        InstitutionCrawler institutionCrawler = new InstitutionCrawler(url);
        return institutionCrawler.json2Doc(institutionCrawler.crawlInstitution());
    }

    public static void main(String[] args) {
        InstitutionCrawler instituteCrawler = new InstitutionCrawler("https://api.openalex.org/institutions/I114027177");
        instituteCrawler.run();
    }
}
