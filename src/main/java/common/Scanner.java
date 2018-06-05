package common;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import annotation.Controller;
import annotation.RequestMapping;

public class Scanner {
   /**
    * �Ӱ�package�л�ȡ���е�Class
    *
    * @param packageName
    * @return
    */
   public  Set<Class<?>> getClasses(String packageName) throws Exception{

      // ��һ��class��ļ���
      //List<Class<?>> classes = new ArrayList<Class<?>>();
      Set<Class<?>> classes = new HashSet<>();
      // �Ƿ�ѭ������
      boolean recursive = true;
      // ��ȡ�������� �������滻
      String packageDirName = packageName.replace('.', '/');
      // ����һ��ö�ٵļ��� ������ѭ�����������Ŀ¼�µ�things
      Enumeration<URL> dirs;
      try {
         dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
         // ѭ��������ȥ
         while (dirs.hasMoreElements()) {
            // ��ȡ��һ��Ԫ��
            URL url = dirs.nextElement();
            // �õ�Э�������
            String protocol = url.getProtocol();
            // ��������ļ�����ʽ�����ڷ�������
            if ("file".equals(protocol)) {
               // ��ȡ��������·��
               String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
               // ���ļ��ķ�ʽɨ���������µ��ļ� ����ӵ������У��������ַ���������
               //���ϵĵ�һ�ַ�����
               findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
               //���ϵĵڶ��ַ���
               //addClass(classes,filePath,packageName);
            } else if ("jar".equals(protocol)) {
               // �����jar���ļ�
               // ����һ��JarFile
               JarFile jar;
               try {
                  // ��ȡjar
                  jar = ((JarURLConnection) url.openConnection()).getJarFile();
                  // �Ӵ�jar�� �õ�һ��ö����
                  Enumeration<JarEntry> entries = jar.entries();
                  // ͬ���Ľ���ѭ������
                  while (entries.hasMoreElements()) {
                     // ��ȡjar���һ��ʵ�� ������Ŀ¼ ��һЩjar����������ļ� ��META-INF���ļ�
                     JarEntry entry = entries.nextElement();
                     String name = entry.getName();
                     // �������/��ͷ��
                     if (name.charAt(0) == '/') {
                        // ��ȡ������ַ���
                        name = name.substring(1);
                     }
                     // ���ǰ�벿�ֺͶ���İ�����ͬ
                     if (name.startsWith(packageDirName)) {
                        int idx = name.lastIndexOf('/');
                        // �����"/"��β ��һ����
                        if (idx != -1) {
                           // ��ȡ���� ��"/"�滻��"."
                           packageName = name.substring(0, idx).replace('/', '.');
                        }
                        // ������Ե�����ȥ ������һ����
                        if ((idx != -1) || recursive) {
                           // �����һ��.class�ļ� ���Ҳ���Ŀ¼
                           if (name.endsWith(".class") && !entry.isDirectory()) {
                              // ȥ�������".class" ��ȡ����������
                              String className = name.substring(packageName.length() + 1, name.length() - 6);
                              try {
                                 // ��ӵ�classes
                                 classes.add(Class.forName(packageName + '.' + className));
                              } catch (ClassNotFoundException e) {
                                 e.printStackTrace();
                              }
                           }
                        }
                     }
                  }
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

      return classes;
   }

   public   void addClass(Set<Class<?>> classes, String filePath, String packageName) throws Exception{
      File[] files=new File(filePath).listFiles(new FileFilter() {
         @Override
         public boolean accept(File file) {
            return (file.isFile()&&file.getName().endsWith(".class"))||file.isDirectory();
         }
      });
      for(File file:files){
         String fileName=file.getName();
         if(file.isFile()){
            String classsName=fileName.substring(0,fileName.lastIndexOf("."));
            if(packageName != null && packageName.length() != 0){
                       classsName=packageName+"."+classsName;
            }
            doAddClass(classes,classsName);
         }

      }
   }

   public   void doAddClass(Set<Class<?>> classes, final String classsName) throws Exception{
      ClassLoader classLoader=new ClassLoader() {
         @Override
         public Class<?> loadClass(String name) throws ClassNotFoundException {
            return super.loadClass(name);
         }
      };
      //Class<?> cls= ClassLoader.loadClass(classsName);
      classes.add(classLoader.loadClass(classsName));
   }

   //��Ҳ����Controllerע�����
   private Set<Class<?>> controllers;

   public Set<Class<?>> getControllers(String basePackage) throws Exception{
      if (controllers == null) {
         controllers = new HashSet<>();
         Set<Class<?>> clsList = getClasses(basePackage);
         if (clsList != null && clsList.size() > 0) {
            for (Class<?> cls : clsList) {
               if (cls.getAnnotation(Controller.class) != null) {
                  controllers.add(cls);
               }
            }
         }
      }
      return controllers;
   }
   public void getMapping(String basePackage,String mapping,Object... args) throws Exception{
      for (Class<?> cls : getControllers(basePackage)) {
         Method[] methods = cls.getMethods();
         for (Method method : methods) {
            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
            if (annotation != null) {
               String value = annotation.value();//�ҵ�RequestMapping��ע��valueֵ
               if (value.equals(mapping)) {//�ж��ǲ���/about���ǵĻ����͵���about(args)����
                  method.invoke(cls.newInstance(), args); //�ڶ��������Ƿ�����Ĳ���
               }
            }
         }
      }
   }
   /**
    * ���ļ�����ʽ����ȡ���µ�����Class
    *
    * @param packageName
    * @param packagePath
    * @param recursive
    * @param classes
    */
   public static void findAndAddClassesInPackageByFile(String packageName,
                                          String packagePath, final boolean recursive, Set<Class<?>> classes) {
      // ��ȡ�˰���Ŀ¼ ����һ��File
      File dir = new File(packagePath);
      // ��������ڻ��� Ҳ����Ŀ¼��ֱ�ӷ���
      if (!dir.exists() || !dir.isDirectory()) {
         // log.warn("�û�������� " + packageName + " ��û���κ��ļ�");
         return;
      }
      // ������� �ͻ�ȡ���µ������ļ� ����Ŀ¼
      File[] dirfiles = dir.listFiles(new FileFilter() {
         // �Զ�����˹��� �������ѭ��(������Ŀ¼) ��������.class��β���ļ�(����õ�java���ļ�)
         public boolean accept(File file) {
            return (recursive && file.isDirectory())
                  || (file.getName().endsWith(".class"));
         }
      });
      // ѭ�������ļ�
      for (File file : dirfiles) {
         // �����Ŀ¼ �����ɨ��
         if (file.isDirectory()) {
            findAndAddClassesInPackageByFile(packageName + "."
                        + file.getName(), file.getAbsolutePath(), recursive,
                  classes);
         } else {
            // �����java���ļ� ȥ�������.class ֻ��������
            String className = file.getName().substring(0,
                  file.getName().length() - 6);
            try {
               // ��ӵ�������ȥ
               //classes.add(Class.forName(packageName + '.' + className));
               //�����ظ�ͬѧ�����ѣ�������forName��һЩ���ã��ᴥ��static������û��ʹ��classLoader��load�ɾ�
               classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
            } catch (ClassNotFoundException e) {
               // log.error("����û��Զ�����ͼ����� �Ҳ��������.class�ļ�");
               e.printStackTrace();
            }
         }
      }
   }
}