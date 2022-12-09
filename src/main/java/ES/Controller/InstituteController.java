package ES.Controller;

import ES.Service.InstituteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstituteController {
    @Autowired
    InstituteService instituteService;
}
