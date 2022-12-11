package ES.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @Column(name = "CID", nullable = false)
    String CID;

    @Column(name = "C_UID", nullable = false)
    String C_UID;

    //对应论文id，论文存在es
    @Column(name = "C_PID", nullable = false)
    String C_PID;

    @Column(name = "Ccontent", nullable = false)
    String Ccontent;

    @Column(name = "Ctime", nullable = false)
    Timestamp Ctime;

    // 点赞数
    @Column(name = "Clikes", nullable = false)
    int Clikes;

    // 是否置顶
    @Column(name = "Ctop", nullable = false)
    boolean Ctop;

    public Comment(String paper_id, String user_id, String content){
        this.CID = UUID.randomUUID().toString();
        this.C_UID = user_id;
        this.C_PID = paper_id;
        this.Ccontent = content;
        this.Ctime = new Timestamp(new Date().getTime());
        this.Clikes = 0;
        this.Ctop = false;
    }
}
