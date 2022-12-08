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

    public ConceptDoc() {

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

    public void setCancestorID (ArrayList<String> ancestorID) {
        this.CancestorID = ancestorID;
    }

    public ArrayList<String> getCancestorID() {
        return this.CancestorID;
    }
}
