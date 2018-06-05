package common;

import java.lang.reflect.Method;

public class Handler {
   private Method mothod;
   private Object obj;
   public Method getMothod() {
      return mothod;
   }
   public void setMothod(Method mothod) {
      this.mothod = mothod;
   }
   public Object getObj() {
      return obj;
   }
   public void setObj(Object obj) {
      this.obj = obj;
   }
   public Handler(Method mothod, Object obj) {
      super();
      this.mothod = mothod;
      this.obj = obj;
   }
   @Override
   public String toString() {
      return "Handler [mothod=" + mothod.getName() + ", obj=" + obj.getClass().getName() + "]";
   }
}
