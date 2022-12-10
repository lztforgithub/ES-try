package ES.Entity;

import java.util.ArrayList;

public class Recommend {
    private int count;
    private String type;
    private ArrayList<Object> conferenceResults = new ArrayList<>();
    private ArrayList<Object> journalResults = new ArrayList<>();
    private ArrayList<Object> paperResults = new ArrayList<>();

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Object> getConferenceResults() {
        return conferenceResults;
    }

    public void setConferenceResults(ArrayList<Object> conferenceResults) {
        this.conferenceResults = conferenceResults;
    }

    public void addConferenceResults(Object o)
    {
        this.conferenceResults.add(o);
    }

    public ArrayList<Object> getJournalResults() {
        return journalResults;
    }

    public void setJournalResults(ArrayList<Object> journalResults) {
        this.journalResults = journalResults;
    }

    public void addJournalResults(Object o)
    {
        this.journalResults.add(o);
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
