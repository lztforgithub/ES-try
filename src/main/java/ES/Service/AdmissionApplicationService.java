package ES.Service;

import ES.Common.Response;
import ES.Entity.AdmissionApplication;

import java.util.List;

public interface AdmissionApplicationService {
    List<AdmissionApplication> getList0();
    List<AdmissionApplication> getList1();

    Response<Object> update(String aa_id, int acc, String opinion);
}
