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
    @Column(name = "LRID", nullable = false)
    String LRID;

    @Column(name = "LR_CID", nullable = false)
    String LR_CID;

    @Column(name = "LR_UID", nullable = false)
    String LR_UID;

    public LikeRecords(String user_id, String comment_id){
        this.LRID = UUID.randomUUID().toString();
        this.LR_UID = user_id;
        this.LR_CID = comment_id;
    }
}
