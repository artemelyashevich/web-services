package com.elyashrvich.soap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.soap.server.endpoint.SoapFaultAnnotationExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@Configuration
@EnableWs
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext,
            @Value("${app.servlet-mapping}") String servletMapping) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, servletMapping);
    }

    @Bean(name = "weather")
    public DefaultWsdl11Definition defaultWsdl11Definition(
            XsdSchema weatherSchema,
            @Value("${app.namespace-url}") String namespaceUrl,
            @Value("${app.port-type-name}") String portTypeName,
            @Value("${app.location-uri}") String locationUri
    ) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName(portTypeName);
        wsdl11Definition.setLocationUri(locationUri);
        wsdl11Definition.setTargetNamespace(namespaceUrl);
        wsdl11Definition.setSchema(weatherSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema weatherSchema(@Value("${app.classpath-root}") String classpathRoot) {
        return new SimpleXsdSchema(new ClassPathResource(classpathRoot));
    }

    @Bean
    public Jaxb2Marshaller marshaller(@Value("${app.jaxb-context-path}") String jaxbContextPath) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(jaxbContextPath);
        return marshaller;
    }

    @Bean
    public SoapFaultAnnotationExceptionResolver exceptionResolver(
            @Value("${app.fault.server-message}") String serverFaultMessage) {
        SoapFaultAnnotationExceptionResolver resolver = new SoapFaultAnnotationExceptionResolver();
        resolver.setOrder(1);
        SoapFaultDefinition serverFault = new SoapFaultDefinition();
        serverFault.setFaultCode(SoapFaultDefinition.SERVER);
        serverFault.setFaultStringOrReason(serverFaultMessage);
        resolver.setDefaultFault(serverFault);
        return resolver;
    }
}
