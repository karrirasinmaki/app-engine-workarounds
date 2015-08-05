# Summary #
The Google App Engine is a great way of hosting your Java applications. And most importantly it's free! (at least if you don't exceed certain quota) Unfortunately, because Google modified the JDK they run your applications with, you will sometimes encounter little obstacles that prevent you from building applications like you're used to.

This library provides a collection of utilities and workarounds to help you build applications running on the Google App Engine. Feel free to contribute any other workarounds and utilities to the project!

# Features #
  * Inbound email processing using the `EmailMessage` class
  * `MultipartResolver` implementation to support file uploads in Spring MVC
  * `JavaMailSender` (Spring) implementation to send emails
  * Utility class for determining the content type of images received from the image service.

# Other workarounds #
Some workarounds don't require extra classes. Instead they could be just some advise or a piece of configuration.
## Working with JPA using Spring ##
Normally you would configure JPA by declaring what provider to use (e.g. Hibarnate) and what database to use (e.g. MySQL). With the Google App Engine you instead use Google's implementation. The way to configure an `EntityManagerFactory` in your Spring application context:
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
                           http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-2.5.xsd">

    <bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalEntityManagerFactoryBean" lazy-init="true">
        <property name="persistenceUnitName" value="transactions-optional"/>
    </bean>

    <bean name="transactionManager" class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"/>
    </bean>
    
    <context:annotation-config/>

    <tx:annotation-driven/>
</beans>
```
This example requires your `<persistence-unit>` to be named "transactions-optional" in `/META-INF/persistence.xml`. Now all you have to do to acquire an `EntityManager` instance is add the `@PersistenceContext` annotation to a field in your class like this:
```
@Repository
@Transactional
public class MyAwesomeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void doAwesomeStuff() {
        // Do anything with entityManager you like
    }
}
```
Of course, this class has to be configured as a Spring bean as well. Also, the `@Transactional` annotation doesn't have to be on this class, you could also implement a service class which has this annotation and calls a repository.

## Google services as Spring services ##
This might be obvious to some, but this is how you can have Google services (like the image or user service)
available as Spring beans. This way you can inject them as singletons into any of your own Spring managed beans.
```
    <bean class="com.google.appengine.api.images.ImagesServiceFactory" factory-method="getImagesService"/>

    <bean class="com.google.appengine.api.users.UserServiceFactory" factory-method="getUserService"/>
```