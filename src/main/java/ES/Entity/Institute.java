package ES.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Document(indexName = "institutions")
public class Institute {
    @Id
    @Field(type= FieldType.text, store = true)
    private String IID;

    @Field(type= FieldType.text, store = true)
    private String Iname;

    @Field(type= FieldType.text, store = true)
    private String Icountry;

    @Field(type= FieldType.text, store = true)
    private String Itype;

    @Field(type= FieldType.text, store = true)
    private String Ihomepage;

    @Field(type= FieldType.text, store = true)
    private String Iimage;

    @Field(type= FieldType.text, store = true)
    private String Iacronyms;

    @Field(type= FieldType.text, store = true)
    private String Ialternames;

    @Field(type= FieldType.Long, store = true)
    private int Iworksnum;

    @Field(type= FieldType.Long, store = true)
    private int Icitednum;

    @Field(type= FieldType.text, store = true)
    private String Ichinesename;

    @Field(type= FieldType.text, store = true)
    private String IassociateIns;

    @Field(type= FieldType.text, store = true)
    private String Irelation;

    @Field(type= FieldType.text, store = true)
    private String Icount;

    @Field(type= FieldType.text, store = true)
    private String Icited;

    @Field(type= FieldType.text, store = true)
    private String Iconcept;

    @Field(type= FieldType.text, store = true)
    private String Iresearchers;

    @Field(type= FieldType.text, store = true)
    private String IworksURL;

}
