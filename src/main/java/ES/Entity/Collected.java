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
    String CTID;

    @Column(name = "CTname", nullable = false)
    String CTname;

    @Column(name = "CT_UID", nullable = false)
    String CT_UID;

    public Collected(String uid,String name){
        this.CTID = UUID.randomUUID().toString();
        this.CTname = name;
        this.CT_UID = uid;
    }
}
