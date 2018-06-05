package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import annotation.Controller;
import annotation.RequestMapping;
@Controller
public class HelloController {
   @RequestMapping("/index")
   public String index(HttpServletRequest request,HttpServletResponse response){
      return "index";
   }
   @RequestMapping("/redirectIndex")
   public String redirectIndex(HttpServletRequest request,HttpServletResponse response){
      return "redirect:/index.do";
   }
}
