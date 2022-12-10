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
@Table(name = "collected")
public class Collected {
    @Id
    @Column(name = "CTID", nullable = false)
    String collected_id;

    @Column(name = "CTname", nullable = false)
    String name;

    @Column(name = "CT_UID", nullable = false)
    String uid;

    public Collected(String uid,String name){
        this.collected_id = UUID.randomUUID().toString();
        this.name = name;
        this.uid = uid;
    }
}
