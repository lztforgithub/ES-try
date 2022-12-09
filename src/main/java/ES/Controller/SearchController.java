package ES.Controller;

import ES.Common.EsUtileService;
import ES.Common.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SearchController {
    @Autowired
    EsUtileService esUtileService;

    @PostMapping("/conditionSearch")
    public Response<Object> conditionSearchTest(@RequestBody Map<String,String> map){

    }
}
