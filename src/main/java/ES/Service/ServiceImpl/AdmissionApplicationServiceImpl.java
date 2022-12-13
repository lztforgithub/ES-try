package ES.Service.ServiceImpl;

import ES.Common.EsUtileService;
import ES.Common.PageResult;
import ES.Common.Response;
import ES.Dao.AdmissionApplicationDao;
import ES.Dao.UserDao;
import ES.Entity.AdmissionApplication;
import ES.Ret.AARet;
import ES.Ret.BaseRet;
import ES.Service.AdmissionApplicationService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

@Service
public class AdmissionApplicationServiceImpl implements AdmissionApplicationService {

    //	引入邮件接口
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    AdmissionApplicationDao admissionApplicationDao;
    @Autowired
    EsUtileService esUtileService = new EsUtileService();

    @Override
    public Response<Object> getList0(){
        List<AdmissionApplication> t = admissionApplicationDao.getList0();
        List<AARet> q = new ArrayList<>();
        for (AdmissionApplication i:t){
            String name = admissionApplicationDao.selectUnameByID(i.getAA_UID());
            q.add(new AARet(
                    i.getAAID(),
                    i.getAAtype(),
                    i.getAA_UID(),
                    i.getAA_RID(),
                    i.getAAtime(),
                    i.getAAlastUpdateTime(),
                    i.getAAname(),
                    i.getAAinstitution(),
                    i.getAAemail(),
                    i.getAAinterestedAreas(),
                    i.getAAhomepage(),
                    i.getAAintroduction(),
                    i.getAAccept(),
                    i.getAOpinion(),
                    name
            ));
        }
        return Response.success("列表如下:",q);
    }

    @Override
    public Response<Object> getList1(){
        List<AdmissionApplication> t = admissionApplicationDao.getList1();
        List<AARet> q = new ArrayList<>();
        for (AdmissionApplication i:t){
            String name = admissionApplicationDao.selectUnameByID(i.getAA_UID());
            q.add(new AARet(
                    i.getAAID(),
                    i.getAAtype(),
                    i.getAA_UID(),
                    i.getAA_RID(),
                    i.getAAtime(),
                    i.getAAlastUpdateTime(),
                    i.getAAname(),
                    i.getAAinstitution(),
                    i.getAAemail(),
                    i.getAAinterestedAreas(),
                    i.getAAhomepage(),
                    i.getAAintroduction(),
                    i.getAAccept(),
                    i.getAOpinion(),
                    name
            ));
        }
        return Response.success("列表如下:",q);
    }

    @Override
    public Response<Object> update(String aa_id, int acc, String opinion){
        AdmissionApplication admissionApplication = null;
        if (acc==1){
            admissionApplication = admissionApplicationDao.selectById(aa_id);
        }
        Timestamp timestamp = new Timestamp(new Date().getTime());
        if (admissionApplicationDao.update(aa_id,acc,opinion,timestamp)>0){
            if (acc==1){
                //update学者门户
                JSONObject jsonObject = esUtileService.queryDocById("researcher",admissionApplication.getAA_RID());
                jsonObject.put("r_UID",admissionApplication.getAA_UID());
                jsonObject.put("rverifytime",new Time(new Date().getTime()));
                jsonObject.put("rcustomconcepts",admissionApplication.getAAinterestedAreas());
                //jsonObject.put("Rinstitute",admissionApplication.getInstitution());
                jsonObject.put("rcontact",admissionApplication.getAAemail());
                if (admissionApplication.getAAhomepage()!=null) {
                    //可能为空
                    jsonObject.put("rpersonalPage", admissionApplication.getAAhomepage());
                }
                jsonObject.put("rgateinfo",admissionApplication.getAAintroduction());

                esUtileService.updateDoc("researcher",admissionApplication.getAA_RID(),jsonObject);

                admissionApplicationDao.updateUser(admissionApplication.getAA_UID());
            }
            if (sendEmail(
                    admissionApplication.getAAemail(),
                    admissionApplication.getAAname(),
                    acc
            )){
                return Response.success("审核成功");
            }
            return Response.fail("审核通过但邮件发送失败");
        }
        return Response.fail("审核失败!");
    }

    @Value("${spring.mail.username}")
    private String from;
    public boolean sendEmail(String to,String name,int accept){
        String acc = "拒绝";
        if (accept==1) acc="通过";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setText("尊敬的AceGate用户,您好:\n"
                + "\n您申请成为入驻用户,学者名称为: " + name + " 的入驻申请 已被" + acc +"\n"
                + "\n如您未申请入驻过  AceGate  ，请忽略该邮件。\n(这是一封通过自动发送的邮件，请不要直接回复）");
        try{
            mailSender.send(message);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public Response<Object> RUID() throws IOException {
        PageResult<JSONObject> t;
        Map<String,Object> map = new HashMap<>();
        map.put("r_UID","*");
        t = esUtileService.conditionSearch("researcher",100,20,"",map,null,null,null);
        return Response.success("已入驻学者名单如下:",t);
    }


    @Override
    public Response<Object> base() throws IOException {
        int userSum = admissionApplicationDao.countUser();

        PageResult<JSONObject> t;

        //t = esUtileService.conditionSearch("researcher",1,1,"",null,null,map,null);
        //int iScholarSum = (int) t.getTotal();
        int iScholarSum = admissionApplicationDao.getIscholarSum("verified");

        t = esUtileService.conditionSearch("researcher",1,1,"",null,null,null,null);
        int scholarSum = (int) t.getTotal();

        t = esUtileService.conditionSearch("concept",1,1,"",null,null,null,null);
        int fieldSum = (int) t.getTotal();

        t = esUtileService.conditionSearch("works",1,1,"",null,null,null,null);
        int paperSum = (int) t.getTotal();

        t = esUtileService.conditionSearch("institutions",1,1,"",null,null,null,null);
        int insSum = (int) t.getTotal();

        BaseRet baseRet = new BaseRet(userSum,iScholarSum,scholarSum,fieldSum,paperSum,insSum);

        return Response.success("平台基本信息如下:",baseRet);
    }
}
