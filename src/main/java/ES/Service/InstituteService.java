package ES.Service;

import ES.Common.Response;

public interface InstituteService {
    Response<Object> getScholarList(String iid);

    Response<Object> getScholarInfo(String iid);
}
