package ES.Service;

import ES.Common.Response;

import java.sql.Timestamp;
import java.util.List;

public interface SearchService {

    Response<Object> defaultSearch(String user_id, String normalSearch, Timestamp start_time, Timestamp end_time, List<String> filterAuthors, List<String> filterPublicationTypes, String sort);
}
