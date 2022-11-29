package ES.Document;

import java.util.ArrayList;
import java.util.Date;

public class WorkDoc {
    private String PID;
    private String DOI;
    private String Pname;
    private String P_VID;
    private String P_Vname;
    private String P_Vurl;
    private ArrayList<String> Pauthor = new ArrayList<>();
    private Date Pdate;
    private int Pcite;
    private String Plink;
    private String Pabstract;
    private ArrayList<String> Pconcepts = new ArrayList<>();
    private ArrayList<String> Preferences = new ArrayList<>();
    private ArrayList<String> Prelated = new ArrayList<>();
    private String Pbecited;
    private ArrayList<Integer> Pcitednum = new ArrayList<>();

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getDOI() {
        return DOI;
    }

    public void setDOI(String DOI) {
        this.DOI = DOI;
    }

    public String getPname() {
        return Pname;
    }

    public void setPname(String pname) {
        Pname = pname;
    }

    public String getP_VID() {
        return P_VID;
    }

    public void setP_VID(String p_VID) {
        P_VID = p_VID;
    }

    public String getP_Vname() {
        return P_Vname;
    }

    public void setP_Vname(String p_Vname) {
        P_Vname = p_Vname;
    }

    public String getP_Vurl() {
        return P_Vurl;
    }

    public void setP_Vurl(String p_Vurl) {
        P_Vurl = p_Vurl;
    }

    public ArrayList<String> getPauthor() {
        return Pauthor;
    }

    public void setPauthor(ArrayList<String> pauthor) {
        Pauthor = pauthor;
    }

    public void addPauthor(String author)
    {
        Pauthor.add(author);
    }

    public Date getPdate() {
        return Pdate;
    }

    public void setPdate(Date pdate) {
        Pdate = pdate;
    }

    public int getPcite() {
        return Pcite;
    }

    public void setPcite(int pcite) {
        Pcite = pcite;
    }

    public String getPlink() {
        return Plink;
    }

    public void setPlink(String plink) {
        Plink = plink;
    }

    public String getPabstract() {
        return Pabstract;
    }

    public void setPabstract(String pabstract) {
        Pabstract = pabstract;
    }

    public ArrayList<String> getPconcepts() {
        return Pconcepts;
    }

    public void setPconcepts(ArrayList<String> pconcepts) {
        Pconcepts = pconcepts;
    }

    public void addPconcepts(String concept)
    {
        Pconcepts.add(concept);
    }

    public ArrayList<String> getPreferences() {
        return Preferences;
    }

    public void setPreferences(ArrayList<String> preferences) {
        Preferences = preferences;
    }

    public void addPreference(String reference)
    {
        Preferences.add(reference);
    }

    public ArrayList<String> getPrelated() {
        return Prelated;
    }

    public void setPrelated(ArrayList<String> prelated) {
        Prelated = prelated;
    }

    public void addPrelated(String related)
    {
        Prelated.add(related);
    }

    public String getPbecited() {
        return Pbecited;
    }

    public void setPbecited(String pbecited) {
        Pbecited = pbecited;
    }

    public ArrayList<Integer> getPcitednum() {
        return Pcitednum;
    }

    public void setPcitednum(ArrayList<Integer> pcitednum) {
        Pcitednum = pcitednum;
    }

    public void addPcitednum(int num)
    {
        Pcitednum.add(num);
    }
}
