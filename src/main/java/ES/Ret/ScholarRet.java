package ES.Ret;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScholarRet {
    JSONObject jsonObject;
    boolean flag;

    /*public ScholarRet(JSONObject jsonObject,boolean flag){
        this.jsonObject = jsonObject;
        this.flag = flag;
    }*/
}
