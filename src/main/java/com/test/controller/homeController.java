package com.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ZXF on 2018/9/14.
 */
@Controller
public class homeController {

  @RequestMapping("home")
  public String toHome(HttpServletRequest request, Model model) {
      return "home";
  }



    @RequestMapping("Test")
    public ModelAndView toTest(HttpServletRequest request, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("TEST");
        modelAndView.addObject("name","1");
        return modelAndView;
    }

    @RequestMapping("Tree")
    public String toTree(HttpServletRequest request, Model model) {


        return "Tree";
    }
}
