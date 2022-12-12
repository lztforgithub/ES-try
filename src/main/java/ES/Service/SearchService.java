package ES.Service;

import ES.Common.Response;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public interface SearchService {

    Response<Object> defaultSearch(String user_id, String normalSearch, Timestamp start_time, Timestamp end_time, String filterAuthors, String filterPublicationTypes, String sort,int page) throws IOException;

    Response<Object> advancedSearch(String user_id, List<JSONObject> advancedSearch, Timestamp from, Timestamp to, String filterAuthors, String filterPublicationTypes, String sort,int page) throws IOException;
}
