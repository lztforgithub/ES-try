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
@Table(name = "SystemTags")
public class System_tags {
    @Id
    @Column(name = "ST_ID", nullable = false)
    String systemTags_id;

    @Column(name = "STname", nullable = false)
    String name;
}
