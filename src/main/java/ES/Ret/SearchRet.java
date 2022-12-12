package ES.Ret;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
public class SearchRet {
    int type;
    String category;
    String content;
}
