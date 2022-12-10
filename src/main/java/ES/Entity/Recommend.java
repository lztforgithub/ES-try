package ES.Entity;

import java.util.ArrayList;

public class Recommend {
    private int count;
    private String cName;
    private ArrayList<PInfo> paperResults = new ArrayList<PInfo>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public ArrayList<PInfo> getPaperResults() {
        return paperResults;
    }

    public void setPaperResults(ArrayList<PInfo> paperResults) {
        this.paperResults = paperResults;
    }

    public void addPaperResults(PInfo pInfo) {
        this.paperResults.add(pInfo);
    }
}
