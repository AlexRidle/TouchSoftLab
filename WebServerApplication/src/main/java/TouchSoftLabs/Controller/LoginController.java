package TouchSoftLabs.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String registration(@RequestParam(name = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("response", "error");
        }
        return "login";
    }
}
