package ES.Common;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class HttpUtils {

    public static String handleResponse(CloseableHttpResponse response){
        String result = "";
        try {
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            result = s.hasNext() ? s.next() : "";
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static String handleRequestWithParams(String dest,
                                                 ArrayList<NameValuePair> nameValuePairs) {
        String ret = "";
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(dest);
            URI uri = new URIBuilder(httpGet.getUri())
                    .addParameters(nameValuePairs)
                    .build();
            httpGet.setUri(uri);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                ret = handleResponse(response);
                response.close();
                return ret;
            } catch (Exception e){
                e.printStackTrace();
                return "ERR_GET";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERR_CLIENT";
        }
    }

    public static String handleRequestURL(String dest) {
        String ret = "";
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(dest);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                ret = handleResponse(response);
                response.close();
                return ret;
            } catch (Exception e){
                e.printStackTrace();
                return "ERR_GET";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERR_CLIENT";
        }
    }

    public static String fetchWebpage(URL url, String proxyHost, int proxyPort) {
        String line = null;
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            HttpURLConnection uc = (HttpURLConnection)url.openConnection(proxy);
            uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:107.0) Gecko/20100101 Firefox/107.0");
            uc.connect();
            line = null;
            StringBuffer tmp = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while ((line = in.readLine()) != null) {
                tmp.append(line);
            }
            return tmp.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static String buildURL(ArrayList<NameValuePair> nameValuePairs, String baseURL) {
        String requestString = "";
        try {
            URI uri = new URIBuilder(baseURL)
                    .addParameters(nameValuePairs)
                    .build();
            requestString = uri.toString();
        } catch (Exception e) {

        }

        return requestString;
    }
}
