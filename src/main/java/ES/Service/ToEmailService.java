package ES.Service;

import ES.Entity.ToEmail;

public interface ToEmailService {
    ToEmail selectByEmail(String to);

    void insertEmail(ToEmail toEmail);

    void updateEmail(ToEmail toEmail);
}
