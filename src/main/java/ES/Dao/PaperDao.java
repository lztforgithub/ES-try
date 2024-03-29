package ES.Dao;

import ES.Entity.Comment;
import ES.Entity.LikeRecords;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaperDao {
    List<Comment> selectByPID(String paper_id);

    LikeRecords isLike(@Param("comment_id") String comment_id,@Param("user_id") String user_id);

    int insertComment(Comment comment);

    int insertLikeRecords(LikeRecords likeRecords);

    int updateLike(String comment_id);

    int deleteLikeRecords(@Param("user_id") String user_id,@Param("comment_id") String comment_id);

    int updateUnLike(String comment_id);

    int getCommentNum(String pid);

    int getCollectNum(String pid);

    String selectUNameByCUID(String c_uid);
}
