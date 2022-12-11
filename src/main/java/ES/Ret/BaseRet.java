package ES.Ret;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseRet {
    int userSum;
    int iScholarSum;
    int scholarSum;
    int fieldSum;
    int paperSum;
    int insSum;

    /*public BaseRet(int userSum,int iScholarSum,int scholarSum,int fieldSum,int paperSum,int insSum){
        this.userSum = userSum;
        this.iScholarSum = iScholarSum;
        this.scholarSum = scholarSum;
        this.fieldSum = fieldSum;
        this.paperSum = paperSum;
        this.insSum = insSum;
    }*/
}
