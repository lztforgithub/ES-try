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
@Table(name = "user")
public class User {
    @Id
    @Column(name = "UID", nullable = false)
    String user_id;

    @Column(name = "Uname", nullable = false)
    String username;

    @Column(name = "Upassword", nullable = false)
    String passwd;

    @Column(name = "Uemail", nullable = false)
    String email;

    @Column(name = "Ubio", nullable = true)
    String info;

    @Column(name = "Utype", nullable = false)
    String type;
}
