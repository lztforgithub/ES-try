package ES.Service;

import ES.Common.Response;
import ES.Entity.AdmissionApplication;

import java.io.IOException;

public interface ScholarService {
    Response<Object> scholarPortal(String researcher_id,String user_id) throws IOException;

    Response<Object> selectResearcherByNameAndInstitute(String researcher_name, String institute) throws IOException;

    Response<Object> applyPortal(AdmissionApplication admissionApplication);

    Response<Object> editPortal(String researcher_id);

    Response<Object> editPortal2(String researcher_id, String avatar, String contact, String interestedAreas, String homepage, String introduction);
}
