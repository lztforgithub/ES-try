package ES.Common;

public class AlexUtils {
    /**
     * 输入网址形式的ID，返回只有大写字母开头和其后数字的ID。
     * @param rawAlexID 网址形式的ID（从OpenAlex上爬取的ID）
     * @return 只有大写字母开头和其后数字的ID
     */
    public static String getRawID(String rawAlexID){
        String[] IDs = rawAlexID.split("/");
        return IDs[IDs.length - 1];
    }
}
