package ES.Document;

import java.util.ArrayList;
import java.util.Date;

public class ResearcherDoc {
    private String RID;
    private String R_UID;
    private Date Rverifytime;
    private String Rname;
    private ArrayList<String> Rnamealternative;
    private int Rcitescount;
    private int Rworkscount;
    private ArrayList<Integer> Rcitesyear;
    private ArrayList<Integer> Rworksyear;
    private ArrayList<String> Rconcepts;
    private ArrayList<String> Rcustomcnocepts;
    private String Rworks_api_url;
    private ArrayList<String> Rcoauthor;
    private ArrayList<String> RcoauthorInstitute;
    private String R_IID;
    private String Rinstitute;
    private String Rcontact;
    private String RpersonalPage;
    private String Rgateinfo;
    private ArrayList<String> Rgatepubs;


    public Date getRverifytime() {
        return Rverifytime;
    }

    public void setRverifytime(Date rverifytime) {
        Rverifytime = rverifytime;
    }

    public String getRname() {
        return Rname;
    }

    public void setRname(String rname) {
        Rname = rname;
    }

    public ArrayList<String> getRnamealternative() {
        return Rnamealternative;
    }

    public void setRnamealternative(ArrayList<String> rnamealternative) {
        Rnamealternative = rnamealternative;
    }

    public int getRcitescount() {
        return Rcitescount;
    }

    public void setRcitescount(int rcitescount) {
        Rcitescount = rcitescount;
    }

    public int getRworkscount() {
        return Rworkscount;
    }

    public void setRworkscount(int rworkscount) {
        Rworkscount = rworkscount;
    }

    public ArrayList<Integer> getRcitesyear() {
        return Rcitesyear;
    }

    public void setRcitesyear(ArrayList<Integer> rcitesyear) {
        Rcitesyear = rcitesyear;
    }

    public ArrayList<Integer> getRworksyear() {
        return Rworksyear;
    }

    public void setRworksyear(ArrayList<Integer> rworksyear) {
        Rworksyear = rworksyear;
    }

    public ArrayList<String> getRconcepts() {
        return Rconcepts;
    }

    public void setRconcepts(ArrayList<String> rconcepts) {
        Rconcepts = rconcepts;
    }

    public ArrayList<String> getRcustomcnocepts() {
        return Rcustomcnocepts;
    }

    public void setRcustomcnocepts(ArrayList<String> rcustomcnocepts) {
        Rcustomcnocepts = rcustomcnocepts;
    }

    public String getRworks_api_url() {
        return Rworks_api_url;
    }

    public void setRworks_api_url(String rworks_api_url) {
        Rworks_api_url = rworks_api_url;
    }

    public ArrayList<String> getRcoauthor() {
        return Rcoauthor;
    }

    public void setRcoauthor(ArrayList<String> rcoauthor) {
        Rcoauthor = rcoauthor;
    }

    public ArrayList<String> getRcoauthorInstitute() {
        return RcoauthorInstitute;
    }

    public void setRcoauthorInstitute(ArrayList<String> rcoauthorInstitute) {
        RcoauthorInstitute = rcoauthorInstitute;
    }

    public String getR_IID() {
        return R_IID;
    }

    public void setR_IID(String r_IID) {
        R_IID = r_IID;
    }

    public String getRinstitute() {
        return Rinstitute;
    }

    public void setRinstitute(String rinstitute) {
        Rinstitute = rinstitute;
    }

    public String getRcontact() {
        return Rcontact;
    }

    public void setRcontact(String rcontact) {
        Rcontact = rcontact;
    }

    public String getRpersonalPage() {
        return RpersonalPage;
    }

    public void setRpersonalPage(String rpersonalPage) {
        RpersonalPage = rpersonalPage;
    }

    public String getRgateinfo() {
        return Rgateinfo;
    }

    public void setRgateinfo(String rgateinfo) {
        Rgateinfo = rgateinfo;
    }

    public ArrayList<String> getRgatepubs() {
        return Rgatepubs;
    }

    public void setRgatepubs(ArrayList<String> rgatepubs) {
        Rgatepubs = rgatepubs;
    }

    public String getRID() {
        return RID;
    }

    public void setRID(String RID) {
        this.RID = RID;
    }

    public String getR_UID() {
        return R_UID;
    }

    public void setR_UID(String r_UID) {
        R_UID = r_UID;
    }
}
