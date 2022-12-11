package ES.Ret;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Recommendation {
    List<JSONObject> author;
    List<JSONObject> institute;
}
