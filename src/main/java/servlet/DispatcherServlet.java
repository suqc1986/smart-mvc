package servlet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import annotation.Controller;
import common.Handler;
import common.HandlerMapping;
import common.Scanner;
import common.ViewResolver;

public class DispatcherServlet extends HttpServlet{
   private static final long serialVersionUID = -8391854573883431763L;
   private HandlerMapping mapping = null;
   private ViewResolver resolver = new ViewResolver();;
   @Override
   public void init(ServletConfig config) throws ServletException {
      String configPath = config.getInitParameter("configLocation");
      initHandlerMapping(configPath);
   }

   private void initHandlerMapping(String configPath) {
      mapping = new HandlerMapping();
      SAXReader reader = new SAXReader();
      InputStream in = getClass().getClassLoader().getResourceAsStream(configPath);
      try {
         Document doc = reader.read(in);
         Element root = doc.getRootElement();
         List<Object> beans = new ArrayList<>();
         @SuppressWarnings("unchecked")
         List<Element> subList = root.elements();
         for(Element element:subList){
            String className = element.attribute("class").getValue();
            beans.add(Class.forName(className).newInstance());
         }
         mapping.process(beans);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   @Override
   protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      try {
      String uri = req.getRequestURI();
      String key = uri.substring(req.getContextPath().length(),uri.lastIndexOf(".do"));
      Handler handler = mapping.getHandler(key);
      String result = (String)handler.getMothod().invoke(handler.getObj(), req,resp);
         resolver.resolver(result,req,resp);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
         e.printStackTrace();
      }catch (Exception e) {
         e.printStackTrace();
      }
   }
}
