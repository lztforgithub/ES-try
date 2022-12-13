package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.Response;
import ES.Dao.CollectDao;
import ES.Entity.CollectRecords;
import ES.Entity.Collected;
import ES.Ret.CollectRet;
import ES.Service.CollectService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectServiceImpl implements CollectService {
    @Autowired
    CollectDao collectDao;
    @Autowired
    EsUtileService esUtileService;

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
        List<Collected> result = new ArrayList<>();
        for (Collected i:collectedList){
            collectRecords = collectDao.selectByCTIDandPID(i.getCTID(),paper_id);
            if (collectRecords!=null){
                result.add(i);
            }
        }
        return Response.success("文档所在收藏夹如下:",result);
    }

    @Override
    public Response<Object> CollectPaper(String user_id, String paper_id, List<String> collect_id){
        collectDao.deleteCollectRecords(user_id,paper_id);
        try{
            for (String i:collect_id){
                if (i == ""){
                    return Response.success("取消收藏成功!");
                }
                CollectRecords collectRecords = new CollectRecords(i,paper_id);
                collectDao.insertCollectRecords(collectRecords);
            }
            return Response.success("修改收藏成功!");
        }catch (Exception e){
            return Response.fail("修改收藏失败!");
        }
    }

    @Override
    public Response<Object> AddCollect(String user_id, String collect_name){
        Collected collected = new Collected(user_id, collect_name);
        if (collectDao.insertCollected(collected)>0){
            return Response.success("新建收藏夹成功!",collected);
        }
        return Response.fail("新建收藏夹失败!");
    }

    @Override
    public Response<Object> viewCollectPaper(String user_id){
        List<Collected> collectedList = collectDao.viewCollect(user_id);
        if (collectedList==null){
            return Response.success("用户收藏夹为空!");
        }
        List<CollectRet> collectRets = new ArrayList<>();
        List<CollectRecords> collectRecords;
        List<JSONObject> jsonObjects = new ArrayList<>();
        JSONObject t;
        for (Collected i:collectedList){
            collectRecords = collectDao.selectByCID(i.getCTID());
            jsonObjects = new ArrayList<>();
            for (CollectRecords j:collectRecords){
                t = esUtileService.queryDocById("works",j.getCR_PID());
                if (t==null){
                    continue;
                }
                jsonObjects.add(t);
            }
            collectRets.add(new CollectRet(
                    i.getCTname(),
                    i.getCTID(),
                    jsonObjects
            ));
        }
        return Response.success("收藏夹文献如下:",collectRets);
    }
}
