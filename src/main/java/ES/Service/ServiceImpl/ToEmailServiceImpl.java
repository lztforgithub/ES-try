package ES.Service.ServiceImpl;

import ES.Dao.ToEmailDao;
import ES.Entity.ToEmail;
import ES.Service.ToEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToEmailServiceImpl implements ToEmailService {

    @Autowired
    ToEmailDao toEmailDao;

    @Override
    public ToEmail selectByEmail(String to){
        return toEmailDao.selectByEmail(to);
    }

    @Override
    public void insertEmail(ToEmail toEmail){
        toEmailDao.insertEmail(toEmail);
    }

    @Override
    public void updateEmail(ToEmail toEmail){
        toEmailDao.updateEmail(toEmail);
    }
}
