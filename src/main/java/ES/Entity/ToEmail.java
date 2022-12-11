package ES.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "email")
public class ToEmail implements Serializable {
    @Id
    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "vercode", nullable = false)
    String vercode;

    @Column(name = "code_time", nullable = false)
    Timestamp code_time;

    public ToEmail(String email, String vercode){
        this.email = email;
        this.vercode = vercode;
        this.code_time = new Timestamp(new Date().getTime());
    }

}

