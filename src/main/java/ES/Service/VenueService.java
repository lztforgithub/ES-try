package ES.Service;

import ES.Common.Response;

import java.io.IOException;

public interface VenueService {

    Response<Object> view(String venue_id);

    Response<Object> paper(String venue_id) throws IOException;
}
