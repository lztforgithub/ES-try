package ES.Document;

import java.util.ArrayList;

public class InstitutionDoc {
    private String IID;
    private String Iname;
    private String Icountry;
    private String Itype;
    private String Ihomepage;
    private String Iimage;
    private ArrayList<String> Iacronyms = new ArrayList<>();
    private ArrayList<String> Ialtername = new ArrayList<>();
    private int Iworksum;
    private int Icitednum;
    private String Ichinesename;
    private ArrayList<String> Iassociations = new ArrayList<>();
    private ArrayList<String> Irelation = new ArrayList<>();
    private ArrayList<Integer> Icount = new ArrayList<>();
    private ArrayList<Integer> Icited = new ArrayList<>();
    private ArrayList<String> Iconcept = new ArrayList<>();
    private ArrayList<String> Iresearchers = new ArrayList<>();
    private String IworksURL;

    public String getIID() {
        return IID;
    }

    public void setIID(String IID) {
        this.IID = IID;
    }

    public String getIname() {
        return Iname;
    }

    public void setIname(String iname) {
        Iname = iname;
    }

    public String getIcountry() {
        return Icountry;
    }

    public void setIcountry(String icountry) {
        Icountry = icountry;
    }

    public String getItype() {
        return Itype;
    }

    public void setItype(String itype) {
        Itype = itype;
    }

    public String getIhomepage() {
        return Ihomepage;
    }

    public void setIhomepage(String ihomepage) {
        Ihomepage = ihomepage;
    }

    public String getIimage() {
        return Iimage;
    }

    public void setIimage(String iimage) {
        Iimage = iimage;
    }

    public ArrayList<String> getIacronyms() {
        return Iacronyms;
    }

    public void setIacronyms(ArrayList<String> iacronyms) {
        Iacronyms = iacronyms;
    }

    public void addIacronyms(String s)
    {
        Iacronyms.add(s);
    }

    public ArrayList<String> getIaltername() {
        return Ialtername;
    }

    public void setIaltername(ArrayList<String> ialtername) {
        Ialtername = ialtername;
    }

    public void addIaltername(String s)
    {
        Ialtername.add(s);
    }

    public int getIworksum() {
        return Iworksum;
    }

    public void setIworksum(int iworksum) {
        Iworksum = iworksum;
    }

    public int getIcitednum() {
        return Icitednum;
    }

    public void setIcitednum(int icitednum) {
        Icitednum = icitednum;
    }

    public String getIchinesename() {
        return Ichinesename;
    }

    public void setIchinesename(String ichinesename) {
        Ichinesename = ichinesename;
    }

    public ArrayList<String> getIassociations() {
        return Iassociations;
    }

    public void setIassociations(ArrayList<String> iassociations) {
        Iassociations = iassociations;
    }

    public void addIassociations(String s)
    {
        Iassociations.add(s);
    }

    public ArrayList<String> getIrelation() {
        return Irelation;
    }

    public void setIrelation(ArrayList<String> irelation) {
        Irelation = irelation;
    }

    public void addIrelation(String s)
    {
        Irelation.add(s);
    }

    public ArrayList<Integer> getIcount() {
        return Icount;
    }

    public void setIcount(ArrayList<Integer> icount) {
        Icount = icount;
    }

    public void addIcount(int i)
    {
        Icount.add(i);
    }

    public ArrayList<Integer> getIcited() {
        return Icited;
    }

    public void setIcited(ArrayList<Integer> icited) {
        Icited = icited;
    }

    public void addIcited(int i)
    {
        Icited.add(i);
    }

    public ArrayList<String> getIconcept() {
        return Iconcept;
    }

    public void setIconcept(ArrayList<String> iconcept) {
        Iconcept = iconcept;
    }

    public void addIconcept(String s)
    {
        Iconcept.add(s);
    }

    public ArrayList<String> getIresearchers() {
        return Iresearchers;
    }

    public void setIresearchers(ArrayList<String> iresearchers) {
        Iresearchers = iresearchers;
    }

    public void addIresearchers(String s)
    {
        Iresearchers.add(s);
    }

    public String getIworksURL() {
        return IworksURL;
    }

    public void setIworksURL(String iworksURL) {
        IworksURL = iworksURL;
    }
}
