package ES.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
    String comment_id;

    //论文id 论文保存在es
    @Column(name = "CR_PID", nullable = false)
    String pid;
}
