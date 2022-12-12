package ES.Ret;

import ES.Entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class CommentRet {
    String cid;
    String content;
    String name;
    boolean isLike;
    Timestamp time;
    int likes;

    /*public CommentRet(Comment comment,boolean isLike){
        this.comment = comment;
        this.isLike = isLike;
    }*/
}
