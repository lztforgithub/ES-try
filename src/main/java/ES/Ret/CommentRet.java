package ES.Ret;

import ES.Entity.Comment;

public class CommentRet {
    Comment comment;
    boolean isLike;

    public CommentRet(Comment comment,boolean isLike){
        this.comment = comment;
        this.isLike = isLike;
    }
}
