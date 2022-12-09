package ES.Service;

import ES.Common.Response;

public interface CollectService {
    Response<Object> viewCollect(String user_id);

    Response<Object> viewPaperCollect(String user_id, String paper_id);
}
