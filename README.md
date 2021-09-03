# spring-proxy-support
<h3>local invoker advice 
   Spring runs the dynamic agent to woven into internal method calls no enhancement. 
   For some small friends who don't want to use aspectj and AOP Context troubles can use 
   this annotation @ Advice, or the regular expression is matched later. 
   One of the projects of the project is that the processing method is not enhanced within the process.
</h3>
 <h3>
   Spring runs dynamic agent to woven, internal method calls no advice, the official website gives a switch to Aspect, 
   (Enable Load Time Weaving). Or open expose proxy = true) Current proxy class (local thread storage defects invoking in sublines)
 </h3>
 <h3>
   
   
@Component
public class AsyncService implements AsyncIService  {

    @Override
    public void advice() {
        //TODO...
        //  when exposing the proxy class, it is used in the child thread=>Cannot find current proxy:
        //  Set 'exposeProxy' property on Advised to 'true' to make it available,and ensure that AopContext.currentProxy()
        //  is invoked in the same thread as the AOP invocation context.
        new Thread(()-> ((AsyncService)AopContext.currentProxy()).adviceExecute(),"local-advice-name").start();

    }

    @Transactional
    @Override
    public void adviceExecute() {
        //TODO...

    }

}
   </h3>
 
 
 
 
 
 <h1>Dynamic agent processing method :</h1>

<h3>Way one:</h3>

@Service
public class AdviceService{

    public void invoker(){
        //TODO...
        handler();
    }

    @Advice(describe = "Enhanced internal method call notes")
    public void handler(){
        //TODO...
    }

}

<h3>Way two:</h3>

  @Service
  public class AdviceService{

    @InjectAdvised
    private  AdviceService adviceService;

    public void invoker(){
        //TODO...
        adviceService.handler();
    }

    public void handler(){
        //TODO...
    }

 }

<h3>Way three :</h3>

@Service
public class AdviceService implements AdviceAware {

    private  AdviceService adviceService;

    @Override
    public void setAdvice(Advised advised) {
        this.adviceService=(AdviceService)advised;
    }

    public void invoker(){
        //TODO...
        adviceService.handler();
    }

    public void handler(){
        //TODO...
    }

}


<h3>Way four :</h3>
@Configuration
@EnableAdvice(advice = @EnableAdvice.AdviceFilter(type = AdviceType.ASPECTJ,expression = {"execution(* com.qycr.AdviceService.*(..)) "}))
//@EnableAdvice(advice = @EnableAdvice.AdviceFilter(type = AdviceType.ASPECTJ,expression = {"execution(* com.qycr.AdviceService.*(..) ) && @annotation(org.springframework.transaction.annotation.Transactional)"}))  //Fine-grained
public class ConfigClass {


}

@Service
public class AdviceService {

    public void invoker(){
        //TODO...
        handler();
    }

    public void handler(){
        //TODO...
    }

}
<h1>Turn on usage:   Annotation | XML</h1>

@Configuration
@EnableAdvice
public class ConfigClass {


}

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:advice="http://www.qycr.framework/schema/advice"
xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd
http://www.qycr.framework/schema/advice
http://www.qycr.framework/schema/advice/spring-advice.xsd">

<advice:advice-driven/>

</beans>
