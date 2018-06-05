package common;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotation.RequestMapping;

public class HandlerMapping {
   private Map<String,Handler> map = new HashMap<String,Handler>();
   public Handler getHandler(String path){
      return map.get(path);
   }
   public void process(List<Object> beans) {
        for(Object obj:beans){
           Method[] methods = obj.getClass().getDeclaredMethods();
           for(Method m:methods){
              RequestMapping arm = m.getAnnotation(RequestMapping.class);
              map.put(arm.value(), new Handler(m,obj));
           }
        }
   }
   
}
