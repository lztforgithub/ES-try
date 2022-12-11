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
@Table(name = "system_tags")
public class System_tags {
    @Id
    @Column(name = "ST_ID", nullable = false)
    String ST_ID;

    @Column(name = "STname", nullable = false)
    String STname;
}
