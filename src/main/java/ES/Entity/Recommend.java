package ES.Entity;

import java.util.ArrayList;

public class Recommend {
    private int count;
    private String cName;
    private ArrayList<Object> paperResults = new ArrayList<>();

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

    public ArrayList<Object> getPaperResults() {
        return paperResults;
    }

    public void setPaperResults(ArrayList<Object> paperResults) {
        this.paperResults = paperResults;
    }

    public void addPaperResults(Object o)
    {
        this.paperResults.add(o);
    }
}
