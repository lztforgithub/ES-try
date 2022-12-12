package ES.Ret;

import java.util.ArrayList;

public class PaperDetails {
    private int citeNum;
    private int beCitedNum;
    private int commentNum;
    private int collectNum;
    private ArrayList<Integer> citeNums = new ArrayList<>();
    private ArrayList<Integer> citeyears = new ArrayList<>();

    public int getCiteNum() {
        return citeNum;
    }

    public void setCiteNum(int citeNum) {
        this.citeNum = citeNum;
    }

    public int getBeCitedNum() {
        return beCitedNum;
    }

    public void setBeCitedNum(int beCitedNum) {
        this.beCitedNum = beCitedNum;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(int commentNum) {
        this.commentNum = commentNum;
    }

    public int getCollectNum() {
        return collectNum;
    }

    public void setCollectNum(int collectNum) {
        this.collectNum = collectNum;
    }

    public ArrayList<Integer> getCiteNums() {
        return citeNums;
    }

    public void setCiteNums(ArrayList<Integer> citeNums) {
        this.citeNums = citeNums;
    }

    public void addCiteNums(int num)
    {
        this.citeNums.add(num);
    }

    public ArrayList<Integer> getCiteyears() {
        return citeyears;
    }

    public void setCiteyears(ArrayList<Integer> citeyears) {
        this.citeyears = citeyears;
    }

    public void addCiteyears(int n)
    {
        this.citeyears.add(n);
    }
}
