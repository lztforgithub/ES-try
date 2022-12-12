package ES.storage;


import ES.Common.Response;
import ES.Common.WebITS;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class ToolBoxService {

    @RequestMapping(value = "/translate", method = RequestMethod.GET)
    public Response<Object> translate(String sourceText, String originLanguage, String targetLanguage) {
        String ret = "";
        try {
            ret = WebITS.translate(sourceText, originLanguage, targetLanguage);
        } catch (Exception e) {
            System.out.println("Error occur when accessing translate api");
            return Response.fail("翻译API连接错误");
        }
        return Response.success("翻译成功", ret);
    }
}
