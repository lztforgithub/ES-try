package ES.Service;

import ES.Common.Response;
import ES.Entity.AdmissionApplication;

import java.io.IOException;
import java.util.List;

public interface AdmissionApplicationService {
    Response<Object> getList0();
    Response<Object> getList1();

    Response<Object> update(String aa_id, int acc, String opinion);

    Response<Object> RUID() throws IOException;

    Response<Object> base() throws IOException;
}
