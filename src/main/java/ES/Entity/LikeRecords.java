package ES.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "like_records")
public class LikeRecords {
    @Id
    @Column(name = "LR_ID", nullable = false)
    String likeRecords_id;

    @Column(name = "C_ID", nullable = false)
    String comment_id;

    @Column(name = "UID", nullable = false)
    String user_id;

    public LikeRecords(String user_id, String comment_id){
        this.likeRecords_id = UUID.randomUUID().toString();
        this.user_id = user_id;
        this.comment_id = comment_id;
    }
}
