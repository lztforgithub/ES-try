package ES.Controller;

import ES.Service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CollectController {
    @Autowired
    CollectService collectService;
}
