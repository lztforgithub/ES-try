package ES.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class VenueDoc {
    private String VID;
    private String Vtype;
    private ArrayList<String> Valtnames;
    private String Vfullname;
    private ArrayList<String> VconceptIDs;
    private ArrayList<Double> Vconceptscores;
    private int VworksCount;
    private int Vcitecount;
    private String Vhomepage;
    private ArrayList<Integer> Vworksyear;
    private ArrayList<Integer> VworksAccumulate;
    private ArrayList<Integer> Vcitesyear;
    private ArrayList<Integer> VcitesAccumulate;


    public String getVID() {
        return VID;
    }

    public void setVID(String VID) {
        this.VID = VID;
    }

    public String getVtype() {
        return Vtype;
    }

    public void setVtype(String vtype) {
        Vtype = vtype;
    }

    public ArrayList<String> getValtnames() {
        return Valtnames;
    }

    public void setValtnames(ArrayList<String> valtnames) {
        Valtnames = valtnames;
    }

    public String getVfullname() {
        return Vfullname;
    }

    public void setVfullname(String vfullname) {
        Vfullname = vfullname;
    }

    public ArrayList<String> getVconceptIDs() {
        return VconceptIDs;
    }

    public void setVconceptIDs(ArrayList<String> vconceptIDs) {
        VconceptIDs = vconceptIDs;
    }

    public ArrayList<Double> getVconceptscores() {
        return Vconceptscores;
    }

    public void setVconceptscores(ArrayList<Double> vconceptscores) {
        Vconceptscores = vconceptscores;
    }

    public int getVworksCount() {
        return VworksCount;
    }

    public void setVworksCount(int vworksCount) {
        VworksCount = vworksCount;
    }

    public int getVcitecount() {
        return Vcitecount;
    }

    public void setVcitecount(int vcitecount) {
        Vcitecount = vcitecount;
    }

    public String getVhomepage() {
        return Vhomepage;
    }

    public void setVhomepage(String vhomepage) {
        Vhomepage = vhomepage;
    }

    public ArrayList<Integer> getVworksyear() {
        return Vworksyear;
    }

    public void setVworksyear(ArrayList<Integer> vworksyear) {
        Vworksyear = vworksyear;
    }

    public ArrayList<Integer> getVworksAccumulate() {
        return VworksAccumulate;
    }

    public void setVworksAccumulate(ArrayList<Integer> vworksAccumulate) {
        VworksAccumulate = vworksAccumulate;
    }

    public ArrayList<Integer> getVcitesyear() {
        return Vcitesyear;
    }

    public void setVcitesyear(ArrayList<Integer> vcitesyear) {
        Vcitesyear = vcitesyear;
    }

    public ArrayList<Integer> getVcitesAccumulate() {
        return VcitesAccumulate;
    }

    public void setVcitesAccumulate(ArrayList<Integer> vcitesAccumulate) {
        VcitesAccumulate = vcitesAccumulate;
    }
}