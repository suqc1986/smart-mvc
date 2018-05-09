package servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@WebServlet(name="helloServlet",value={"/helloServlet"},initParams={@WebInitParam(name="username",value="suqc")})
public class HelloServlet extends HttpServlet{
   private static final long serialVersionUID = 8332497227665487689L;

   @Override
   protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String value = getServletConfig().getInitParameter("username");
        System.out.println(req.getScheme());//     http
        System.out.println(req.getServerName());//    localhost
        System.out.println(req.getServerPort());//    8080
        System.out.println(req.getServletContext());//   org.apache.catalina.core.ApplicationContextFacade@78d40cb2
        System.out.println(req.getServletContext().getServletContextName());// TestWeb
        System.out.println(req.getContextPath());//      /TestWeb
        System.out.println(req.getServletPath());//      /helloServlet
        System.out.println(req.getRequestURL());//       http://localhost:8080/TestWeb/helloServlet
        System.out.println(req.getRequestURI());//    /TestWeb/helloServlet
        System.out.println(req.getQueryString());//   name=suqc
        System.out.println(req.getServletContext().getRealPath("/helloServlet"));//   E:\apache-tomcat-7.0.67\webapps\TestWeb\helloServlet
        req.setAttribute("name", req.getParameter("name"));
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
   }
}
