package ES.storage;


import ES.Common.HttpUtils;
import ES.Common.PageResult;
import ES.Common.Response;
import ES.Common.WebITS;
import ES.Crawler.ResearcherCrawler;
import ES.Document.ResearcherDoc;
import ES.Document.WorkDoc;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.csl.CSLItemDataBuilder;
import de.undercouch.citeproc.csl.CSLName;
import de.undercouch.citeproc.csl.CSLType;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RestController
public class ToolBoxService {

    @RequestMapping(value = "/translate", method = RequestMethod.POST)
    public Response<Object> translate(@RequestBody Map<String, String> map1) {
        String sourceText = map1.get("sourceText");
        String originLanguage = map1.get("originLanguage");
        String targetLanguage = map1.get("targetLanguage");
        String ret = "";
        try {
            ret = WebITS.translate(sourceText, originLanguage, targetLanguage);
        } catch (Exception e) {
            System.out.println("Error occur when accessing translate api");
            return Response.fail("翻译API连接错误");
        }
        return Response.success("翻译成功", ret);
    }

    @RequestMapping(value = "/crawlResearchersAgain", method = RequestMethod.POST)
    public Response<Object> crawlResearchersAgain(@RequestBody Map<String, String> map1) {
        String Rname = map1.get("Rname");
        String Rinstitution = map1.get("Rinstitution");
        InstitutionStorage institutionStorage = new InstitutionStorage();
        ResearcherStorage researcherStorage = new ResearcherStorage();
        ConceptStorage conceptStorage = new ConceptStorage();
        JSONObject object = institutionStorage.searchInstitutionByName(Rinstitution);
        if (object.getInteger("count") == 0) {
            return Response.fail("未找到对应的研究机构。");
        }
        JSONObject instituteObject =object.getJSONArray("results").getJSONObject(0);
        String R_IID = instituteObject.getString("iID");
        String R_Iname = instituteObject.getString("iname");
        String R_Ichinesename = "暂无参考中文名";
        if (instituteObject.getString("ichinesename") != null) {
            R_Ichinesename = instituteObject.getString("ichinesename");
        }

        String requestString = "https://api.openalex.org/authors";
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("filter", "last_known_institution.id:" + R_IID + ",display_name.search:" + Rname));
        requestString = HttpUtils.buildURL(nameValuePairs, requestString);
        System.out.println(requestString);
        ArrayList<ResearcherDoc> temp = ResearcherCrawler.getResearchersByURL(requestString, 1);
        if (temp.size() == 0) {
            return Response.fail("未根据学者姓名及机构名称查询到对应的学者。请检查英文拼写。");
        }
        ResearcherDoc researcherDoc = temp.get(0);

        // 检查数据库中是否已经有记录

        JSONObject object1 = researcherStorage.searchResearcherById(researcherDoc.getRID());
        if (object1 != null) {
            // 已经有记录
            return Response.fail("未查询到更多记录。");
        }


        institutionStorage.esUtileService.addDoc("researcher", researcherDoc);

        try {
            Map<String,Object> map = new HashMap<>();
            map.put("rname",researcherDoc.getRname());
            map.put("r_IID", researcherDoc.getR_IID());
            System.out.printf("%s %s %s\n", researcherDoc.getRID(), researcherDoc.getRname(), researcherDoc.getR_IID());
//            System.out.println(researcherStorage.searchResearcherById(researcherDoc.getRID()));
            JSONObject object2 = researcherStorage.searchResearcherById(researcherDoc.getRID());
            PageResult<JSONObject> t = new PageResult<JSONObject>();
            ArrayList<JSONObject> objects = new ArrayList<>();
            objects.add(object2);
            t.setList(objects);
            t.setTotal(1);
            t.setPageNum(1);
            t.setPageSize(20);
            t.setTotalPage(1);
            t.setCurrentPage((long) 1);
            t.setNumberOfElements(1);
            return Response.success("匹配的学者如下:",t);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.fail("未查询到更多记录。");
        }

    }


    @RequestMapping(value = "/citations", method = RequestMethod.POST)
    public Response<Object> getNewCitation(@RequestBody Map<String, String> map1){
        String PID = map1.get("PID");

        String[] months = {"Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun.", "Jul.", "Aug.", "Sep.", "Oct.", "Nov.", "Dec."};


        try {
            JSONObject ret = new JSONObject();
            WorkStorage workStorage = new WorkStorage();
            JSONObject object = workStorage.findDocByID("works", PID);
            if (object == null) {
//            ret.put("status", "ERROR");
                return Response.fail("未查找到论文");
            }
            String Pname = object.getString("pname");
            String P_Vname;
            if (object.getString("p_Vname") == null) {
                P_Vname = "[none]";
            } else {
                P_Vname = object.getString("p_Vname");
                if (P_Vname.length() <= 2) {
                    P_Vname = "none";
                }
            }

            JSONArray researchers = object.getJSONArray("pauthorname");
            ArrayList<String> parsedResearchers = new ArrayList<>();
            for (int i = 0; i < researchers.size(); i++) {
                String rname = researchers.getString(i);
                if (rname == null) {
                    parsedResearchers.add("[none] [none]");
                    continue;
                }
                if (rname.length() <= 2) {
                    parsedResearchers.add("[none] [none]");
                    continue;
                }
                if (rname.contains(" ")) {
                    parsedResearchers.add(researchers.getString(i));
                } else {
                    parsedResearchers.add("[none] [none]");
                }

            }
            Calendar cal = Calendar.getInstance();
            int month = 12;
            int day = 14;
            int year = 2022;

            if (object.getLong("pdate") != null && object.getLong("pdate") >= 0) {
                cal.setTimeInMillis(object.getLong("pdate"));
                System.out.println("Cal: " + cal.get(Calendar.MONTH));
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                year = cal.get(Calendar.YEAR);
            }

            StringBuilder APABuilder = new StringBuilder();
            StringBuilder MLABuilder = new StringBuilder();
            StringBuilder IEEEBuilder = new StringBuilder();

            APABuilder.append("[1]");
            MLABuilder.append("[1]");
            IEEEBuilder.append("[1]");
            // 处理作者姓名

            int counter = 0;
            for (String temp : parsedResearchers) {
                counter++;

                int index = temp.indexOf(' ');
                String givenName = temp.substring(0, index);
                String familyName = temp.substring(index + 1);

                APABuilder.append(givenName).append(", ").append(familyName.charAt(0)).append(". ");
                MLABuilder.append(givenName).append(", ").append(familyName).append(". ");
                IEEEBuilder.append(familyName.charAt(0)).append(". ").append(givenName).append(", ");

                if (counter >= 4) {
                    APABuilder.append("et. al. ");
                    MLABuilder.append("et. al. ");
                    IEEEBuilder.append("et. al. ");
                    break;
                }
            }

            //APA：年份+文章标题
            APABuilder.append("(").append(year).append("). ").append(Pname);

            //MLA:文章标题+年份
            MLABuilder.append(Pname).append(" ").append(months[month]).append(" ").append(year).append(".");

            // IEEE: 文章标题+年份
            IEEEBuilder.append('"').append(Pname).append('"').append(" ").append(months[month]).append(" ").append(year).append(".");

            ret = new JSONObject();
            ret.put("IEEE", IEEEBuilder.toString());
            ret.put("MLA", MLABuilder.toString());
            ret.put("APA", APABuilder.toString());
            return Response.success("生成引用成功！", ret);
        }catch (Exception e) {
            return Response.fail("生成引用失败");
        }

    }


    @RequestMapping(value = "/old_citations", method = RequestMethod.POST)
    public Response<Object> getCitation(@RequestBody Map<String, String> map1) {

        String PID = map1.get("PID");

        try {
            JSONObject ret = new JSONObject();
            WorkStorage workStorage = new WorkStorage();
            JSONObject object = workStorage.findDocByID("works", PID);
            if (object == null) {
//            ret.put("status", "ERROR");
                return Response.fail("未查找到论文");
            }
            String Pname = object.getString("pname");
            String P_Vname;
            if (object.getString("p_Vname") == null) {
                P_Vname = "[none]";
            } else {
                P_Vname = object.getString("p_Vname");
                if (P_Vname.length() <= 2) {
                    P_Vname = "none";
                }
            }
            JSONArray researchers = object.getJSONArray("pauthorname");
            ArrayList<String> parsedResearchers = new ArrayList<>();
            for (int i = 0; i < researchers.size(); i++) {
                String rname = researchers.getString(i);
                if (rname == null) {
                    parsedResearchers.add("[none] [none]");
                    continue;
                }
                if (rname.length() <= 2) {
                    parsedResearchers.add("[none] [none]");
                    continue;
                }
                if (rname.contains(" ")) {
                    parsedResearchers.add(researchers.getString(i));
                } else {
                    parsedResearchers.add("[none] [none]");
                }

            }
            Calendar cal = Calendar.getInstance();
            int month = 12;
            int day = 14;
            int year = 2022;

            if (object.getLong("pdate") != null && object.getLong("pdate") >= 0) {
                cal.setTimeInMillis(object.getLong("pdate"));
                System.out.println("Cal: " + cal.get(Calendar.MONTH));
                month = cal.get(Calendar.MONTH) + 1;
                day = cal.get(Calendar.DAY_OF_MONTH);
                year = cal.get(Calendar.YEAR);
            }

            String P_Vurl = "none";
            if(object.getString("p_Vurl") != null) {
                P_Vurl = object.getString("p_Vurl");
            }

            CSLItemDataBuilder builder = new CSLItemDataBuilder()
                    .type(CSLType.ARTICLE_JOURNAL)
                    .title(Pname);
            for (String temp : parsedResearchers) {

                int index = temp.indexOf(' ');
                String givenName = temp.substring(0, index);
                String familyName = temp.substring(index + 1);

                builder = builder.author(givenName, familyName);
            }

            builder
                    .issued(year, month, day)
                    .accessed(2022, 12, 14)
                    .build();



            FrogItemProvider frogItemProvider = new FrogItemProvider();
            frogItemProvider.builder = builder;


            try {
//                System.out.println("Avail " + CSL.supportsStyle("ieee"));
//                File file = new File(getClass().getResource("/ieee.csl").getFile());
//                InputStream in = new FileInputStream(file);
//                Scanner s = new Scanner(in).useDelimiter("\\A");
//                String ieeeStyle = s.hasNext() ? s.next() : "";
//                in.close();

                CSL cslIEEE = new CSL(frogItemProvider, "ieee");
                cslIEEE.setOutputFormat("text");
                cslIEEE.registerCitationItems("1");
                ret.put("IEEE", cslIEEE.makeBibliography().makeString());
                System.out.println(cslIEEE.makeBibliography().makeString());
//
//                file = new File(getClass().getResource("/modern-language-association.csl").getFile());
//                in = new FileInputStream(file);
//                s = new Scanner(in).useDelimiter("\\A");
//                String mlaStyle = s.hasNext() ? s.next() : "";
//                in.close();

                CSL cslMLA = new CSL(frogItemProvider, "modern-language-association");
                cslMLA.setOutputFormat("text");
                cslMLA.registerCitationItems("1");
                ret.put("MLA", "[1]" + cslMLA.makeBibliography().makeString());


//                file = new File(getClass().getResource("/apa.csl").getFile());
//                in = new FileInputStream(file);
//                s = new Scanner(in).useDelimiter("\\A");
//                String apaStyle = s.hasNext() ? s.next() : "";
//                in.close();
//                s.close();

                CSL cslAPA = new CSL(frogItemProvider, "apa");
                cslAPA.setOutputFormat("text");
                cslAPA.registerCitationItems("1");
                ret.put("APA", "[1]" + cslAPA.makeBibliography().makeString());

                return Response.success("成功生成引用", ret);
            } catch (Exception e) {
                e.printStackTrace();
                return Response.fail("引用生成失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.fail("test");
    }
}



class FrogItemProvider implements ItemDataProvider {

    public CSLItemDataBuilder builder;

    @Override
    public CSLItemData retrieveItem(String id) {
        // System.out.println("Call with " + id);
        return builder.id(id).build();
    }

    @Override
    public String[] getIds() {
        String ids[] = {"ID-0", "ID-1", "ID-2"};
        return ids;
    }
}
