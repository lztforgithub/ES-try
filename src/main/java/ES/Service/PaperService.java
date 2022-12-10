package ES.Service;

import ES.Common.Response;

public interface PaperService {
    Response<Object> view(String paper_id);

    Response<Object> cite(String paper_id);

    Response<Object> viewComment(String paper_id, String user_id);

    Response<Object> commentAdd(String paper_id, String user_id, String content);

    Response<Object> like(String user_id, String comment_id);

    Response<Object> unlike(String user_id, String comment_id);

    Response<Object> getRecommendWork(String type);
}
