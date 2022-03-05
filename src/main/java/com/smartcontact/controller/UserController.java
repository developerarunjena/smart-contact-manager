package com.smartcontact.controller;

import com.smartcontact.helper.Message;
import com.smartcontact.model.Contact;
import com.smartcontact.model.User;
import com.smartcontact.repo.ContactRepository;
import com.smartcontact.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    // method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        System.out.println("USERNAME " + userName);

        // get the user using usernamne(Email)

        User user = userRepository.getUserByUserName(userName);
        System.out.println("USER " + user);
        model.addAttribute("user", user);

    }

    @RequestMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "client/dashboard";
    }

    // open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());

        return "client/add_contact_form";
    }

    // processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
                                 Principal principal, HttpSession session) {

        try {

            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            // processing and uploading file..

            if (file.isEmpty()) {
                // if the file is empty then try our message
                System.out.println("File is empty");
                contact.setImage("contact.png");

            } else {
                // file the file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image is uploaded");

            }

            user.getContacts().add(contact);

            contact.setUser(user);

            this.userRepository.save(user);

            System.out.println("DATA " + contact);

            System.out.println("Added to data base");

            // message success.......
            session.setAttribute("message", new Message("Your contact is added !! Add more..", "success"));

        } catch (Exception e) {
            System.out.println("ERROR " + e.getMessage());
            e.printStackTrace();
            // message error
            session.setAttribute("message", new Message("Some went wrong !! Try again..", "danger"));

        }

        return "client/add_contact_form";
    }

    // show contacts handler
    // per page = 5[n]
    // current page = 0 [page]
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
        m.addAttribute("title", "Show User Contacts");
        // contact ki list ko bhejni hai

        String userName = principal.getName();

        User user = this.userRepository.getUserByUserName(userName);

        // currentPage-page
        // Contact Per page - 5
        Pageable pageable = PageRequest.of(page, 8);

        Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());

        return "client/show_contacts";
    }
    // showing particular contact details.

    @RequestMapping("/{contactId}/contact")
    public String showContactDetail(@PathVariable("contactId") Integer cId, Model model, Principal principal) {
        System.out.println("CID " + cId);

        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();

        //
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getFirstName());
        }

        return "client/contact_detail";
    }

    // delete contact handler

    @GetMapping("/delete/{contactId}")
    @Transactional
    public String deleteContact(@PathVariable("contactId") Integer cId, Model model, HttpSession session,
                                Principal principal) {
        System.out.println("CID " + cId);

        Contact contact = this.contactRepository.findById(cId).get();
        // check...Assignment..image delete

        // delete old photo

        User user = this.userRepository.getUserByUserName(principal.getName());

        user.getContacts().remove(contact);

        this.userRepository.save(user);

        System.out.println("DELETED");
        session.setAttribute("message", new Message("Contact deleted succesfully...", "success"));

        return "redirect:/user/show-contacts/0";
    }

    // open update form handler
    @PostMapping("/update-contact/{contactId}")
    public String updateForm(@PathVariable("contactId") Integer cid, Model m) {

        m.addAttribute("title", "Update Contact");

        Contact contact = this.contactRepository.findById(cid).get();

        m.addAttribute("contact", contact);

        return "client/update_form";
    }

    // update contact handler
    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
                                Model m, HttpSession session, Principal principal) {

        try {

            // old contact details
            Contact oldcontactDetail = this.contactRepository.findById(contact.getContactId()).get();

            // image..
            if (!file.isEmpty()) {
                // file work..
                // rewrite

//				delete old photo

                File deleteFile = new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile, oldcontactDetail.getImage());
                file1.delete();

//				update new photo

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(file.getOriginalFilename());

            } else {
                contact.setImage(oldcontactDetail.getImage());
            }

            User user = this.userRepository.getUserByUserName(principal.getName());

            contact.setUser(user);

            this.contactRepository.save(contact);

            session.setAttribute("message", new Message("Your contact is updated...", "success"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("CONTACT NAME " + contact.getFirstName());
        System.out.println("CONTACT ID " + contact.getContactId());
        return "redirect:/user/" + contact.getContactId() + "/contact";
    }

    // your profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model) {
        model.addAttribute("title", "Profile Page");
        return "client/profile";
    }

    // open settings handler
    @GetMapping("/settings")
    public String openSettings() {
        return "client/settings";
    }

    // change password..handler
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {
        System.out.println("OLD PASSWORD " + oldPassword);
        System.out.println("NEW PASSWORD " + newPassword);

        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(userName);
        System.out.println(currentUser.getPassword());

        if (this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
            // change the password

            currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);
            session.setAttribute("message", new Message("Your password is successfully changed..", "success"));

        } else {
            // error...
            session.setAttribute("message", new Message("Please Enter correct old password !!", "danger"));
            return "redirect:/user/settings";
        }

        return "redirect:/user/dashboard";
    }


    //creating order for payment
    /*@PostMapping("/create_order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> data) throws Exception
    {
        //System.out.println("Hey order function ex.");
        System.out.println(data);

        int amt=Integer.parseInt(data.get("amount").toString());

        var client=new RazorpayClient("rzp_test_haDRsJIQo9vFPJ", "owKJJes2fwE6YD6DToishFuH");

        JSONObject ob=new JSONObject();
        ob.put("amount", amt*100);
        ob.put("currency", "INR");
        ob.put("receipt", "txn_235425");

        //creating new order

        Order order = client.Orders.create(ob);
        System.out.println(order);

        //if you want you can save this to your data..
        return order.toString();
    }*/



}
