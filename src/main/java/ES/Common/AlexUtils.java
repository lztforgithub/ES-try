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


    public static String generateAbbr(String fullname) {
        String[] parts = fullname.split(" ");

        if(parts.length <= 2) {
            return "none";
        }
        if(parts[0].equals("arXiv:")) {
            return "none";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String part : parts) {
            if (part.equals("IEEE")){
                continue;
            }
            char ch = part.charAt(0);
            if (ch >= 'A' && ch <= 'Z') {
                stringBuilder.append(ch);
            }
        }

        String ret = stringBuilder.toString();
        if (ret.length() > 5) {
            ret = ret.substring(0, 5);
        }
        if (ret.length() <= 1){
            ret = "none";
        }
        return ret;
    }
}
