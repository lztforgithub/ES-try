package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.Response;
import ES.Dao.PaperDao;
import ES.Entity.Comment;
import ES.Entity.LikeRecords;
import ES.Ret.CommentRet;
import ES.Service.PaperService;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.common.recycler.Recycler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaperServiceImpl implements PaperService {

    @Autowired
    EsUtileService esUtileService = new EsUtileService();
    @Autowired
    PaperDao paperDao;

    @Override
    public Response<Object> view(String paper_id){
        JSONObject jsonObject = esUtileService.queryDocById("works",paper_id);
        if (jsonObject==null){
            return Response.fail("PID错误!");
        }
        return Response.success("文献详情如下:",
                jsonObject);
    }

    @Override
    public Response<Object> cite(String paper_id){
        JSONObject jsonObject = esUtileService.queryDocById("works",paper_id);
        if (jsonObject==null){
            return Response.fail("PID错误!");
        }
        return Response.success("引用格式如下:",
                jsonObject.getString("Pbecited"));
    }

    @Override
    public Response<Object> viewComment(String paper_id, String user_id){
        List<Comment> comments = paperDao.selectByPID(paper_id);
        List<CommentRet> commentRets = new ArrayList<>();
        LikeRecords likeRecords;
        for (Comment i:comments){
            likeRecords=paperDao.isLike(i.getComment_id(),user_id);
            if (likeRecords!=null){
                commentRets.add(new CommentRet(i,true));
            }
            else {
                commentRets.add(new CommentRet(i,false));
            }
        }
        return Response.success("评论如下:",commentRets);
    }

    @Override
    public Response<Object> commentAdd(String paper_id, String user_id, String content){
        Comment comment = new Comment(paper_id,user_id,content);
        if (paperDao.insertComment(comment)>0){
            return Response.success("评论成功",comment);
        }
        return Response.fail("评论失败!");
    }
}
