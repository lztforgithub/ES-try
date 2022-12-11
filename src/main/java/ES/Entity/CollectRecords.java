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
@Table(name = "collect_records")
public class CollectRecords {
    @Id
    @Column(name = "CRID", nullable = false)
    String CRID;

    @Column(name = "CR_CTID", nullable = false)
    String CR_CTID;

    //论文id 论文保存在es
    @Column(name = "CR_PID", nullable = false)
    String CR_PID;

    public CollectRecords(String collect_id,String pid){
        this.CRID = UUID.randomUUID().toString();
        this.CR_CTID = collect_id;
        this.CR_PID = pid;
    }
}
