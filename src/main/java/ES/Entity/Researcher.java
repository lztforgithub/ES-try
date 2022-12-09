package ES.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Document(indexName = "researcher")
public class Researcher {
    @Id
    @Field(type= FieldType.text, store = true)
    private String RID;

    @Field(type= FieldType.text)
    private String R_UID;

    @Field(type= FieldType.Date)
    private Timestamp Rverifytime;

    @Field(type= FieldType.text, store = true)
    private String Rname;

    @Field(type= FieldType.text, store = true)
    private String Rnamealternative;

    @Field(type= FieldType.Long, store = true)
    private int Rcitescount;

    @Field(type= FieldType.Long, store = true)
    private int Rworkscount;

    @Field(type= FieldType.text, store = true)
    private String Rcitesyear;

    @Field(type= FieldType.text, store = true)
    private String Rworksyear;

    @Field(type= FieldType.text, store = true)
    private String Rconcepts;

    @Field(type= FieldType.text, store = true)
    private String Rcustomconcepts;

    @Field(type= FieldType.text, store = true)
    private String Rworks_api_url;

    @Field(type= FieldType.text, store = true)
    private String Rcoauthor;

    @Field(type= FieldType.text, store = true)
    private String R_IID;

    @Field(type= FieldType.text, store = true)
    private String Rinstitute;

    @Field(type= FieldType.text, store = true)
    private String Rcontact;

    @Field(type= FieldType.text, store = true)
    private String RpersonalPage;

    @Field(type= FieldType.text, store = true)
    private String Rgateinfo;

    @Field(type= FieldType.text, store = true)
    private String Rgatepubs;

}
