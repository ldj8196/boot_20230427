package com.example.boot_20230427.controller.jpa.library;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.boot_20230427.entity.library.Student2;
import com.example.boot_20230427.repository.library.Student2Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping(value = "/student2")
@RequiredArgsConstructor
@Slf4j
public class Student2Controller {
    
    @Autowired Student2Repository s2Repository;
    final String format = "Student2Controller => {}";
    BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();


    @GetMapping(value = "/login.do")
    public String loginGET() {
        try {
            return "/student2/login";
        } catch (Exception e) {
            return "redirect:/home.do";
        }
    }

    @PostMapping(value = "/login.do")
    public String loginPOST(@ModelAttribute Student2 obj) {
        try {
            // log.info(format, obj);
            // Student2 s2 = s2Repository.findByemail(format);
            // if(bcpe.matches(obj.getPassword(), s2.getPassword())) {
            //     return "redirect:/home.do";
            // }
            return "redirect:/student2/login.do";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/student2/login.do";
        }
    }

    @GetMapping(value = "/insert.do")
    public String insertGET() {
        try {
            return "/student2/insert";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/home.do";
        }
    }

    @PostMapping(value="/insert.do")
    public String insertPOST(@ModelAttribute Student2 obj) {
        try {
            obj.setPassword(bcpe.encode(obj.getPassword()));
            log.info(format, obj.toString());
            s2Repository.save(obj);
            return "redirect:/student2/login.do";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/home.do";
        }
        
    }
    

}