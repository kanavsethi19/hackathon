package uk.co.vodafone.hackathon.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.co.vodafone.hackathon.bean.User;
import uk.co.vodafone.hackathon.service.ESIndexing;

@Controller
public class BasicController {
	@Autowired
	private ESIndexing esIndexing;
	
	@RequestMapping(value ="/form", method=RequestMethod.GET)
	public String save(Model model) {
		model.addAttribute("user",new User());
		return "form";
	}
	
	@RequestMapping(value="/form", method=RequestMethod.POST)
    public String customerSubmit(@ModelAttribute User user, Model model) {
         
        model.addAttribute("user", user);
        try {
        	try {
				if(!esIndexing.checkIfIndexExists("ecomm"))
					esIndexing.createIndex();
			} catch (Exception e) {
				e.printStackTrace();
			}
			esIndexing.indexQuery(user.getUserName(),user.getPhoneNumber(),user.getEmailId(),""+user.getEmailId().split("@")[0],user.getRequirement());
		} catch (IOException e) {
			e.printStackTrace();
		}
        System.out.println(user.getUserName());
        System.out.println(user.getPhoneNumber());
        System.out.println(user.getEmailId());
        System.out.println(user.getRequirement());
        return "result";
    }
}
	