package ES.Service;

import ES.Common.Response;

import java.util.List;

public interface CollectService {
    Response<Object> viewCollect(String user_id);

    Response<Object> viewPaperCollect(String user_id, String paper_id);

    Response<Object> CollectPaper(String user_id, String paper_id, List<String> collect_id);

    Response<Object> AddCollect(String user_id, String collect_name);
}
