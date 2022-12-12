package ES.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@Entity
@Table(name = "admission_application")
@NoArgsConstructor
public class AdmissionApplication {
    @Id
    @Column(name = "AAID", nullable = false)
    String AAID;

    @Column(name = "AAtype", nullable = false)
    int AAtype;

    @Column(name = "AA_UID", nullable = false)
    String AA_UID;

    //学者id 学者在es上
    @Column(name = "AA_RID", nullable = false)
    String AA_RID;

    @Column(name = "AAtime", nullable = false)
    Timestamp AAtime;

    //最后处理时间 通过于。。/拒绝于。。
    @Column(name = "AAlastUpdateTime", nullable = false)
    Timestamp AAlastUpdateTime;

    @Column(name = "AAname", nullable = false)
    String AAname;

    //研究单位，申请者自行填写
    @Column(name = "AAinstitution", nullable = false)
    String AAinstitution;

    @Column(name = "AAemail", nullable = false)
    String AAemail;

    //研究领域，申请者自行填写
    @Column(name = "AAinterestedareas", nullable = false)
    String AAinterestedareas;

    //主页链接，申请者自行填写,可不填
    @Column(name = "AAhomepage", nullable = false)
    String AAhomepage;

    @Column(name = "AAintroduction", nullable = false)
    String AAintroduction;

    //0 待审核 1 通过 2 拒绝
    @Column(name = "AAccept", nullable = false)
    int AAccept;

    @Column(name = "AOpinion", nullable = false)
    String AOpinion;

    public AdmissionApplication(
            String user_id,
            String researcher_id,
            String researcher_name,
            String institute,
            String contact,
            String interestedAreas,
            String homepage,
            String introduction
    ){
        this.AAID = UUID.randomUUID().toString();
        this.AAtype = 1;
        this.AA_UID = user_id;
        this.AA_RID = researcher_id;
        this.AAtime = new Timestamp(new Date().getTime());
        this.AAlastUpdateTime = this.AAtime;
        this.AAname = researcher_name;
        this.AAinstitution = institute;
        this.AAemail = contact;
        this.AAinterestedareas = interestedAreas;
        this.AAhomepage = homepage;
        this.AAintroduction = introduction;
        this.AAccept = 0;
        this.AOpinion = "";
    }

}
