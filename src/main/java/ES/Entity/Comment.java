package ES.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @Column(name = "CID", nullable = false)
    String comment_id;

    @Column(name = "C_UID", nullable = false)
    String uid;

    //对应论文id，论文存在es
    @Column(name = "C_PID", nullable = false)
    String pid;

    @Column(name = "Ccontent", nullable = false)
    String content;

    @Column(name = "Ctime", nullable = false)
    Timestamp time;

    // 点赞数
    @Column(name = "Clikes", nullable = false)
    int likes;

    // 是否置顶
    @Column(name = "Ctop", nullable = false)
    boolean top;
}
