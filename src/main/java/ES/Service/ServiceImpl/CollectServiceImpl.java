package ES.Service.ServiceImpl;

import ES.Common.Response;
import ES.Dao.CollectDao;
import ES.Entity.CollectRecords;
import ES.Entity.Collected;
import ES.Service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectServiceImpl implements CollectService {
    @Autowired
    CollectDao collectDao;

    @Override
    public Response<Object> viewCollect(String user_id){
        List<Collected> collectedList = collectDao.viewCollect(user_id);
        if (collectedList!=null){
            return Response.success("收藏夹如下:",collectedList);
        }
        return Response.fail("收藏列表为空!");
    }

    @Override
    public Response<Object> viewPaperCollect(String user_id, String paper_id){
        List<Collected> collectedList = collectDao.viewCollect(user_id);
        if (collectedList==null){
            return Response.fail("收藏夹为空!");
        }
        CollectRecords collectRecords;
        for (Collected i:collectedList){
            collectRecords = collectDao.selectByCTIDandPID(i.getCollected_id(),paper_id);
            if (collectRecords!=null){
                return Response.success("位于收藏夹:",i);
            }
        }
        return Response.fail("文档未收藏!");
    }
}
