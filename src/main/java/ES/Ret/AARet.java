package ES.Ret;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class AARet {
    String AAID;

    int AAtype;

    String AA_UID;

    String AA_RID;

    Timestamp AAtime;

    Timestamp AAlastUpdateTime;

    String AAname;

    String AAinstitution;

    String AAemail;

    String AAinterestedareas;

    String AAhomepage;

    String AAintroduction;

    int AAccept;

    String AOpinion;

    String Uname;
}
