package ES.Service;

import ES.Common.Response;

import java.io.IOException;

public interface PaperService {
    Response<Object> view(String paper_id);

    Response<Object> cite(String paper_id);

    Response<Object> viewComment(String paper_id, String user_id);

    Response<Object> commentAdd(String paper_id, String user_id, String content);

    Response<Object> like(String user_id, String comment_id);

    Response<Object> unlike(String user_id, String comment_id);

    Response<Object> getRecommendWorks() throws IOException;

    Response<Object> getRecommendConf();

    Response<Object> getRecommendJournal();

    Response<Object> systemTags(String paper_id);

    void crawlWorkURLByAuthor() throws IOException;

    Response<Object> getDetails(String pid);
}
