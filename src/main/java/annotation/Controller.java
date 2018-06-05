package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.management.remote.TargetedNotification;
import javax.sound.sampled.TargetDataLine;
import javax.swing.text.html.parser.TagElement;
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
   String name() default "";
}
