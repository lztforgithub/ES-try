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
@Document(indexName = "concept")
public class Concepts {
    @Id
    @Field(type= FieldType.text, store = true)
    private String CID;

    @Field(type= FieldType.text, store = true)
    private String Cname;

    @Field(type= FieldType.text, store = true)
    private String CnameCN;

    @Field(type= FieldType.Long, store = true)
    private int Clevel;

    @Field(type= FieldType.text, store = true)
    private String CancestorID;

}
