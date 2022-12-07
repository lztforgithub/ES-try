package ES.Document;

import ES.Common.AlexUtils;
import ES.Common.HttpUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ConceptDoc {
    private String CID;
    private String Cname;
    private String CnameCN;
    private int Clevel;
    private ArrayList<String> CancestorID;

    public ConceptDoc (String CID, String Cname, String CnameCN, int Clevel) {
        this.CID = CID;
        this.Cname = Cname;
        this.CnameCN = CnameCN;
        this.Clevel = Clevel;
    }

    public ConceptDoc (String CID, String Cname, String CnameCN, int Clevel, ArrayList<String> CancestorID) {
        this.CID = CID;
        this.Cname = Cname;
        this.CnameCN = CnameCN;
        this.Clevel = Clevel;
        this.CancestorID = CancestorID;
    }

    /**
     * 爬取该概念的所有父级概念。
     * @return 成功返回0；中间有出错则返回-1。
     */
    public int getAncestors(){
        assert this.CID.length() > 1;
        this.CancestorID.clear();
        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("filter", "ids.openalex:" + this.CID));
            String response = HttpUtils.handleRequestWithParams("https://api.openalex.org/concepts", nameValuePairs);
            JSONObject responseJSON = new JSONObject(response);
            // 拿取第一个记录
            JSONObject entry = responseJSON.getJSONArray("results").getJSONObject(0);
            JSONArray ancestors = entry.getJSONArray("ancestors");
            for (int i = 0; i < ancestors.length(); i++) {
                this.CancestorID.add(AlexUtils.getRawID(ancestors.getJSONObject(i).getString("id")));
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public String getCID() {
        return CID;
    }

    public void setCID(String cid) {
        this.CID = cid;
    }

    public String getCname() {
        return Cname;
    }

    public void setCname(String cname) {
        this.Cname = cname;
    }

    public String getCnameCN() {
        return CnameCN;
    }

    public void setCnameCN(String cnameCN) {
        CnameCN = cnameCN;
    }

    public int getClevel() {
        return Clevel;
    }

    public void setClevel(int clevel) {
        Clevel = clevel;
    }
}
