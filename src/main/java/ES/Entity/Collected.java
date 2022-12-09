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
@Table(name = "collected")
public class Collected {
    @Id
    @Column(name = "CTID", nullable = false)
    String collected_id;

    @Column(name = "CTname", nullable = false)
    String name;

    @Column(name = "CT_UID", nullable = false)
    String uid;
}
