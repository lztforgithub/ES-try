package ES.Ret;

import ES.Entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentRet {
    Comment comment;
    boolean isLike;

    /*public CommentRet(Comment comment,boolean isLike){
        this.comment = comment;
        this.isLike = isLike;
    }*/
}
