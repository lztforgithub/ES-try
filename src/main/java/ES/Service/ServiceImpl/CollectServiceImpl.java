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
    @Autowired(required = false)
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

    @Override
    public Response<Object> CollectPaper(String user_id, String paper_id, String collect_id){
        collectDao.deleteCollectRecords(user_id,paper_id);
        CollectRecords collectRecords = new CollectRecords(collect_id,paper_id);
        if (collectDao.insertCollectRecords(collectRecords)>0){
            return Response.success("收藏成功!",collectRecords);
        }
        return Response.fail("收藏失败!");
    }

    @Override
    public Response<Object> AddCollect(String user_id, String collect_name){
        Collected collected = new Collected(user_id, collect_name);
        if (collectDao.insertCollected(collected)>0){
            return Response.success("新建收藏夹成功!",collected);
        }
        return Response.fail("新建收藏夹失败!");
    }
}
