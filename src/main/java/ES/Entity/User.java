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

    public User(String username, String passwd, String email, String bio){
        this.user_id = UUID.randomUUID().toString();
        this.username = username;
        this.passwd = passwd;
        this.email = email;
        this.info = bio;
        this.type = "default";
    }
}
