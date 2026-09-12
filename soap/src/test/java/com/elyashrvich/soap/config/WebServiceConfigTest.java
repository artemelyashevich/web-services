package com.elyashrvich.soap.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;


@ExtendWith(MockitoExtension.class)
class WebServiceConfigTest {

    private static final String WEATHER_XSD_CLASSPATH = "xsd/weather.xsd";

    @Mock
    private ApplicationContext applicationContext;

    private final WebServiceConfig webServiceConfig = new WebServiceConfig();

    @ParameterizedTest(name = "servletMapping \"{0}\" is registered as-is")
    @CsvSource({"/ws/*", "/soap/*", "/api/weather/*"})
    void messageDispatcherServletGivenMapping_RegistersServletUnderThatMapping(String servletMapping) {
        ServletRegistrationBean<MessageDispatcherServlet> registration =
                webServiceConfig.messageDispatcherServlet(applicationContext, servletMapping);

        assertTrue(registration.getUrlMappings().contains(servletMapping));
    }

    @ParameterizedTest(name = "namespace={0}, portType={1}, location={2} all end up in the generated WSDL")
    @CsvSource({
            "http://elyashrvich.com/soap/weather, WeatherPort, /ws",
            "http://example.org/other-service, OtherPort, /custom-ws"
    })
    void defaultWsdl11DefinitionGivenProperties_ProducesWsdlContainingThoseValues(
            String namespaceUrl, String portTypeName, String locationUri) throws Exception {
        XsdSchema schema = initializedWeatherSchema();

        DefaultWsdl11Definition definition =
                webServiceConfig.defaultWsdl11Definition(schema, namespaceUrl, portTypeName, locationUri);
        definition.afterPropertiesSet();

        String wsdl = toXmlString(definition.getSource());

        assertAll(
                () -> assertTrue(wsdl.contains("targetNamespace=\"" + namespaceUrl + "\"")),
                () -> assertTrue(wsdl.contains(portTypeName)),
                () -> assertTrue(wsdl.contains(locationUri))
        );
    }

    @Test
    void weatherSchemaGivenClasspathRoot_ResolvesToWeatherXsdNamespace() throws Exception {
        SimpleXsdSchema schema = (SimpleXsdSchema) webServiceConfig.weatherSchema(WEATHER_XSD_CLASSPATH);
        schema.afterPropertiesSet();

        assertEquals("http://elyashrvich.com/soap/weather", schema.getTargetNamespace());
    }

    private XsdSchema initializedWeatherSchema() throws Exception {
        SimpleXsdSchema schema = (SimpleXsdSchema) webServiceConfig.weatherSchema(WEATHER_XSD_CLASSPATH);
        schema.afterPropertiesSet();
        return schema;
    }

    private static String toXmlString(Source source) throws Exception {
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(source, new StreamResult(writer));
        return writer.toString();
    }
}
