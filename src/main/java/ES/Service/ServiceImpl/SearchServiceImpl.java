package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.Response;
import ES.Service.SearchService;
import ES.config.ElasticSearchConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired(required = false)
    EsUtileService esUtileService = new EsUtileService();

    @Override
    public Response<Object> defaultSearch(String user_id, String normalSearch, Timestamp start_time, Timestamp end_time, List<String> filterAuthors, List<String> filterPublicationTypes, String sort){
        return Response.fail("正在写");

    }


}
