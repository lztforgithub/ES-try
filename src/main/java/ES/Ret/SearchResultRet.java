package ES.Ret;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResultRet {
    List<JSONObject> list;
    List<SimpleAuthor> author;
    List<SimpleVenue> venue;
    int num;
}
