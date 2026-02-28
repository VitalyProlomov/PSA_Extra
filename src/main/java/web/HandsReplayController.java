package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HandsReplayController {
    @GetMapping("/hands")
    public String hands() {
        return "hands";
    }
}
