package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.config.CustomUserDetailsService;
import sec.project.domain.Account;
import sec.project.domain.Message;
import sec.project.repository.AccountRepository;
import sec.project.repository.MessageRepository;

import java.util.List;

@Controller
public class SignupController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MessageRepository messageRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String username, @RequestParam String password) {

        if(accountRepository.findByUsername(username)==null) {
            customUserDetailsService.saveAccount(username, password);
            return "done";
        }

        return "form";
    }


    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String redirectUser(Authentication authentication) {

        if (authentication.isAuthenticated())
            return "redirect:/user/" + accountRepository.findByUsername(authentication.getName()).getId();
        return "redirect:/form";
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public String getAccount(Model model, @PathVariable Long id, Authentication authentication) {
        //Remove commented lines to fix access control. (A5)
        try {
            Account target = accountRepository.getOne(id);
            if (target == null) {
                return "redirect:/form";
            }
            /*if (!authentication.getName().equals(target.getUsername())) {
                return "redirect:/user";
            }*/
            List<Message> messageList = messageRepository.findBySenderId(id);
            model.addAttribute("user", target);
            model.addAttribute("messages", messageList);
            model.addAttribute("id", id);
            return "account";
        } catch (NullPointerException e) {
            return "redirect:/form";
        }

    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.POST)
    public String postMessage(@PathVariable Long id, @RequestParam String message) {
        messageRepository.save(new Message(accountRepository.findOne(id), message));
        return "redirect:/user/" + id;

    }

    @RequestMapping(value = "/search/{id}", method = RequestMethod.POST)
    public String searchMessage(Authentication authentication, Model model, @PathVariable Long id, @RequestParam String content) {
        /*
        if (!authentication.getName().equals(target.getUsername())) {
            return "redirect:/user";

        }
        List<Message> results = messageRepository.findBySenderIdAndMessageContains(id, content);
        */

        //Uncomment lines above and remove line below to disable injection into database. (A1)
        //Also prevents sensitive data exposure. (A3)

        List results = messageRepository.customMethod(content);

        model.addAttribute("search", results);
        return "result";
    }

}
