package common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ViewResolver {

   public void resolver(String result, HttpServletRequest req, HttpServletResponse resp) throws Exception {
      if(result.startsWith("redirect:")){
         resp.sendRedirect(req.getContextPath()+result.substring("redirect:".length()));
      }else{
         req.getRequestDispatcher("/WEB-INF/jsp/"+result+".jsp").forward(req, resp);
      }
   }
}
