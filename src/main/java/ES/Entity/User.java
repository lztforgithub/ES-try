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
@Entity
@Table(name = "user")
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "UID")
    String UID;

    @Column(name = "Uname")
    String Uname;

    @Column(name = "Upassword")
    String Upassword;

    @Column(name = "Uemail")
    String Uemail;

    @Column(name = "Ubio")
    String Ubio;

    @Column(name = "Utype")
    String Utype;

    public User(String username, String passwd, String email, String bio){
        this.UID = UUID.randomUUID().toString();
        this.Uname = username;
        this.Upassword = passwd;
        this.Uemail = email;
        this.Ubio = bio;
        this.Utype = "default";
    }

}
