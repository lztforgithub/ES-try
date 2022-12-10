package ES.Crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class PreCrawl {
    public ArrayList<String> concept_urls = new ArrayList<>();
    public ArrayList<String> venue_urls = new ArrayList<>();
    public ArrayList<String> works_urls = new ArrayList<>();

    public void crawlConceptURL()
    {
        String URL = "https://api.openalex.org/concepts?filter=level:1,ancestors.id:C41008148&per_page=50";
        InputStreamReader reader = null;
        BufferedReader in = null;
        StringBuffer content = new StringBuffer();

        int counter = 0;

        String url = URL;
        content = new StringBuffer();
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(1000);
            reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
            in = new BufferedReader(reader);

            String line = null;

            while ((line = in.readLine()) != null) {
                content.append(line);
            }
            System.out.println("crawl " + url + " done.");
        } catch (IOException e) {
            System.out.println("can't crawl " + url);
            ;
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

        JSONObject jsonObject = JSON.parseObject(content.toString());
        JSONArray results = jsonObject.getJSONArray("results");
        for(int i=0; i<results.size(); i++)
        {
            JSONObject result = results.getJSONObject(i);
            String id = "C"+result.getString("id").split("C")[1];
            concept_urls.add(id);
        }
        System.out.println(concept_urls.size());
    }

    public void crawlVenueURL() {
        for(String s:concept_urls)
        {
            String URL = "https://api.openalex.org/venues?sort=cited_by_count:desc&filter=concept.id:"+s+"&per_page=50";    // computer science
            InputStreamReader reader = null;
            BufferedReader in = null;
            StringBuffer content = new StringBuffer();

            String appendix = "*";

            int counter = 0;

            while (appendix != null) {
                String url = URL;
                content = new StringBuffer();
                try {
                    URLConnection connection = new URL(url).openConnection();
                    connection.setConnectTimeout(1000);
                    reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                    in = new BufferedReader(reader);

                    String line = null;

                    while ((line = in.readLine()) != null) {
                        content.append(line);
                    }
                    System.out.println("crawl " + url + " done.");
                } catch (IOException e) {
                    System.out.println("can't crawl " + url);
                    ;
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

                JSONObject jsonObject = JSON.parseObject(content.toString());

                JSONArray results = jsonObject.getJSONArray("results");
                for (int j = 0; j < results.size(); j++) {
                    JSONObject obj = results.getJSONObject(j);
                    String id = obj.getString("id");
                    String venue_id = id.split("V")[1];
                    venue_urls.add(venue_id);
                }

//            appendix = jsonObject.getJSONObject("meta").getString("next_cursor");
                appendix = null;

                System.out.println("crawling page " + counter++);
            }
            System.out.println("venue size is "+this.venue_urls.size());
        }
    }

    public void crawlWorkURL() throws IOException {
        String url = "";
        InputStreamReader reader = null;
        BufferedReader in = null;
        StringBuffer content = new StringBuffer();

        String appendix = "*";
        FileWriter fileWriter = new FileWriter("works_urls.txt");

        String next_url = "https://api.openalex.org/works?per_page=50&filter=host_venue.id:V";
        for(String s:venue_urls)
        {
            while(appendix!=null)
            {
                content = new StringBuffer();
                try {
                    url = next_url+s;
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

                JSONArray result = JSON.parseObject(content.toString()).getJSONArray("results");
                for(int i=0; i<result.size(); i++)
                {
                    JSONObject work = result.getJSONObject(i);
                    String work_url = work.getString("id");
                    works_urls.add(work_url);
                    fileWriter.write("https://api.openalex.org/works/W"+work_url.split("W")[1]+"\n");
                }

//                appendix = JSON.parseObject(content.toString()).getJSONObject("meta").getString("next_cursor");
                appendix = null;

                System.out.println("works_urls size="+works_urls.size());
            }
        }

        fileWriter.close();
    }

    public static void main(String[] args) throws IOException {
        PreCrawl preCrawl = new PreCrawl();
        preCrawl.crawlConceptURL();
        preCrawl.crawlVenueURL();
//        preCrawl.crawlWorkURL();
        System.out.println("preCrawl done");
    }
}
