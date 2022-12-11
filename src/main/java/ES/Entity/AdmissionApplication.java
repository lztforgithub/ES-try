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
    String admissionApplication_id;

    @Column(name = "AAtype", nullable = false)
    int type;

    @Column(name = "AA_UID", nullable = false)
    String uid;

    //学者id 学者在es上
    @Column(name = "AA_RID", nullable = false)
    String rid;

    @Column(name = "AAtime", nullable = false)
    Timestamp time;

    //最后处理时间 通过于。。/拒绝于。。
    @Column(name = "AAlastUpdateTime", nullable = false)
    Timestamp lastUpdateTime;

    @Column(name = "AAname", nullable = false)
    String name;

    //研究单位，申请者自行填写
    @Column(name = "AAinstitution", nullable = false)
    String institution;

    @Column(name = "AAemail", nullable = false)
    String email;

    //研究领域，申请者自行填写
    @Column(name = "AAinterestedareas", nullable = false)
    String interestedareas;

    //主页链接，申请者自行填写,可不填
    @Column(name = "AAhomepage", nullable = true)
    String homepage;

    @Column(name = "AAintroduction", nullable = false)
    String introduction;

    //0 待审核 1 通过 2 拒绝
    @Column(name = "AAccept", nullable = false)
    int accept;

    @Column(name = "AOpinion", nullable = true)
    String opinion;

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
        this.admissionApplication_id = UUID.randomUUID().toString();
        this.type = 1;
        this.uid = user_id;
        this.rid = researcher_id;
        this.time = new Timestamp(new Date().getTime());
        this.lastUpdateTime = this.time;
        this.name = researcher_name;
        this.institution = institute;
        this.email = contact;
        this.interestedareas = interestedAreas;
        this.homepage = homepage;
        this.introduction = introduction;
        this.accept = 0;
        this.opinion = "";
    }

}
