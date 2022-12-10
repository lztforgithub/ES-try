package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Common.Response;
import ES.Service.SearchService;
import ES.config.ElasticSearchConfig;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.incrementer.HanaSequenceMaxValueIncrementer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    EsUtileService esUtileService = new EsUtileService();

    @Override
    public Response<Object> defaultSearch(
            String user_id,
            String normalSearch,
            Timestamp start_time,
            Timestamp end_time,
            String filterAuthors,
            String filterPublicationTypes,
            String sort) throws IOException {
        Map<String,Object> ormap = new HashMap<>();
        Map<String,Object> andmap = new HashMap<>();
        ormap.put("Pname",normalSearch);
        ormap.put("Pabstract",normalSearch);
        ormap.put("Pconcepts",normalSearch);
        boolean flag = false;
        if (filterAuthors!=null){
            andmap.put("Pauthor",filterAuthors);
            flag = true;
        }
        if (filterPublicationTypes!=null){
            andmap.put("P_VID",filterPublicationTypes);
            flag = true;
        }
        if (!flag){
            andmap = null;
        }

        PageResult<JSONObject> t = esUtileService.defaultSearch("paper",100,20,"",andmap,ormap,null,null,null,null,start_time,end_time);
        return Response.success("搜索结果如下:",t);
    }


}
