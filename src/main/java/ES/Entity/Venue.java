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
@Document(indexName = "works")
public class Venue {
    @Id
    @Field(type= FieldType.text, store = true)
    private String VID;

    @Field(type= FieldType.text, store = true)
    private String Vtype;

    @Field(type= FieldType.text, store = true)
    private String Valtnames;

    @Field(type= FieldType.text, store = true)
    private String Vfullname;

    @Field(type= FieldType.text, store = true)
    private String VconceptIDs;

    @Field(type= FieldType.text, store = true)
    private String Vconceptscores;

    @Field(type= FieldType.Long, store = true)
    private int Vworkscount;

    @Field(type= FieldType.Long, store = true)
    private int Vcitecount;

    @Field(type= FieldType.text)
    private String Vhomepage;

    @Field(type= FieldType.text, store = true)
    private String Vworksyear;

    @Field(type= FieldType.text, store = true)
    private String VworksAccumulate;

    @Field(type= FieldType.text, store = true)
    private String Vcitesyear;

    @Field(type= FieldType.text, store = true)
    private String VcitesAccumulate;
}
