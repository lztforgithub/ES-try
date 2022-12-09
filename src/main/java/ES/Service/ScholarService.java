package ES.Service;

import ES.Common.Response;

public interface ScholarService {
    Response<Object> scholarPortal(String researcher_id);
}
