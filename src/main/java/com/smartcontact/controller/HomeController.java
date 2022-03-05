package com.smartcontact.controller;

import com.smartcontact.helper.Message;
import com.smartcontact.model.User;
import com.smartcontact.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class HomeController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title","Home - Contact Manager");
        return "index";
    }
    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About - Contact Manager");
        return "about";
    }
    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title","Register - Contact Manager");
        model.addAttribute("user",new User());
        return "signup";
    }
    //.. handler for register user
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String registerUser(
            @Valid
            @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam(value = "agreement",defaultValue = "false") boolean agreement,
            Model model,
            HttpSession session
            ){
      try{
          // if agreement not accepted
          if(!agreement){
              System.out.println("Please accept terms and condition");
              throw new Exception("please accept terms and condition");
          }
          if(bindingResult.hasErrors()){
              System.out.println("Error " + bindingResult.toString());
              model.addAttribute("user", user);
              return "signup";
          }
          user.setRole("ROLE_USER");
          user.setEnabled(true);
          user.setImageUrl("banner.jpg");
          user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

          System.out.println("Agreement "+ agreement);

          System.out.println("USER" + user);
          User result = userRepository.save(user);
          model.addAttribute("user", new User());
          session.setAttribute("message", new Message("successfully registered","alert-success"));
          return "signup";
      }catch(Exception e){
            e.printStackTrace();
            model.addAttribute("user",user);
            session.setAttribute("message", new Message("Server error "+ e.getMessage(),"alert-danger"));
            return "signup";
        }
    }
    @GetMapping(value = "/login")
    public String login(Model model){
        model.addAttribute("title","Login - Contact Manager");
        return "login";
    }


}
