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
    String collectRecords_id;

    @Column(name = "CR_CTID", nullable = false)
    String collect_id;

    //论文id 论文保存在es
    @Column(name = "CR_PID", nullable = false)
    String pid;

    public CollectRecords(String collect_id,String pid){
        this.collectRecords_id = UUID.randomUUID().toString();
        this.collect_id = collect_id;
        this.pid = pid;
    }
}
