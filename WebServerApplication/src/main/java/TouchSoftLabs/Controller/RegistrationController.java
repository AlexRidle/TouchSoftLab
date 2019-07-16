package TouchSoftLabs.Controller;

import TouchSoftLabs.Entity.User;
import TouchSoftLabs.Enumeration.Role;
import TouchSoftLabs.Repository.UserRepository;
import TouchSoftLabs.Service.UserService;
import TouchSoftLabs.Utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class RegistrationController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(final UserService userService, final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("roles", Role.values());
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@Valid User user,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        model.addAttribute("roles", Role.values());
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("user", user);
            if (userRepository.existsByUsername(user.getUsername())) {
                model.addAttribute("message", "error");
            }
            return "registration";
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (!userService.addUser(user)) {
                model.addAttribute("message", "error");
                return "registration";
            }
            redirectAttributes.addFlashAttribute("response", "success");
            return "redirect:/login";
        }
    }

}
