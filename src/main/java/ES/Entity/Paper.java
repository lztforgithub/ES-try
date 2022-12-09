package ES.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Document(indexName = "works")
public class Paper {
    @Id
    @Field(type= FieldType.text, store = true)
    private String PID;

    @Field(type= FieldType.text, store = true)
    private String DOI;

    @Field(type= FieldType.text, store = true)
    private String Pname;

    @Field(type= FieldType.text, store = true)
    private String P_VID;

    @Field(type= FieldType.text, store = true)
    private String P_Vname;

    @Field(type= FieldType.text, store = true)
    private String P_Vurl;

    @Field(type= FieldType.text, store = true)
    private String Pauthor;

    @Field(type= FieldType.text, store = true)
    private String Pdate;

    @Field(type= FieldType.text, store = true)
    private String Pcite;

    @Field(type= FieldType.text, store = true)
    private String is_retracted;

    @Field(type= FieldType.text, store = true)
    private String is_paratext;

    @Field(type= FieldType.text, store = true)
    private String Plink;

    @Field(type= FieldType.text, store = true)
    private String Pabstract;

    @Field(type= FieldType.text, store = true)
    private String Pabstractwords;

    @Field(type= FieldType.text, store = true)
    private String Pabstractcount;

    @Field(type= FieldType.text, store = true)
    private String Pconcepts;

    @Field(type= FieldType.text, store = true)
    private String Preferences;

    @Field(type= FieldType.text, store = true)
    private String Prelated;

    @Field(type= FieldType.text, store = true)
    private String Pbecited;

    @Field(type= FieldType.text, store = true)
    private String Pcitednum;
    
}
