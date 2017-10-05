package org.hspconsortium.platform.api.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.JpaSystemProviderDstu2;
import ca.uhn.fhir.jpa.provider.dstu3.JpaSystemProviderDstu3;
import ca.uhn.fhir.jpa.provider.r4.JpaSystemProviderR4;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import org.hl7.fhir.dstu3.model.Meta;
import org.hspconsortium.platform.api.conformance.HspcConformanceProviderDstu2;
import org.hspconsortium.platform.api.conformance.HspcConformanceProviderR4;
import org.hspconsortium.platform.api.conformance.HspcConformanceProviderStu3;
import org.hspconsortium.platform.api.fhir.repository.MetadataRepositoryDstu2Impl;
import org.hspconsortium.platform.api.fhir.repository.MetadataRepositoryR4;
import org.hspconsortium.platform.api.fhir.repository.MetadataRepositoryStu3;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

public class HapiFhirServlet extends RestfulServer {

    private static final long serialVersionUID = 1L;

    private String fhirMappingPath;

    private String openMappingPath;

    public HapiFhirServlet() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void initialize() throws ServletException {
        super.initialize();

        // get the context holder values
        WebApplicationContext myAppCtx = HapiFhirServletContextHolder.getInstance().getMyAppCtx();
        fhirMappingPath = HapiFhirServletContextHolder.getInstance().getFhirMappingPath();
        openMappingPath = HapiFhirServletContextHolder.getInstance().getOpenMappingPath();
        FhirVersionEnum fhirVersionEnum = HapiFhirServletContextHolder.getInstance().getFhirVersionEnum();

        setFhirContext(new FhirContext(fhirVersionEnum));


        /*
         * The BaseJavaConfigDstu3.java class is a spring configuration
		 * file which is automatically generated as a part of hapi-fhir-jpaserver-base and
		 * contains bean definitions for a resource provider for each resource type
		 */
        String resourceProviderBeanName;

        if (fhirVersionEnum == FhirVersionEnum.DSTU2) {
            resourceProviderBeanName = "myResourceProvidersDstu2";
        } else if (fhirVersionEnum == FhirVersionEnum.DSTU3) {
            resourceProviderBeanName = "myResourceProvidersDstu3";
        } else if (fhirVersionEnum == FhirVersionEnum.R4) {
            resourceProviderBeanName = "myResourceProvidersR4";
        } else {
            throw new IllegalStateException("Not a supported FHIR Version: " + fhirVersionEnum);
        }

        List<IResourceProvider> beans = myAppCtx.getBean(resourceProviderBeanName, List.class);
        setResourceProviders(beans);

		/*
         * The system provider implements non-resource-type methods, such as
		 * transaction, and global history.
		 */

        Object systemProvider;
        if (fhirVersionEnum == FhirVersionEnum.DSTU2) {
            systemProvider = myAppCtx.getBean("mySystemProviderDstu2", JpaSystemProviderDstu2.class);
        } else if (fhirVersionEnum == FhirVersionEnum.DSTU3) {
            systemProvider = myAppCtx.getBean("mySystemProviderDstu3", JpaSystemProviderDstu3.class);
        } else if (fhirVersionEnum == FhirVersionEnum.R4) {
            systemProvider = myAppCtx.getBean("mySystemProviderR4", JpaSystemProviderR4.class);
        } else {
            throw new IllegalStateException();
        }
        setPlainProviders(systemProvider);


        if (fhirVersionEnum == FhirVersionEnum.DSTU2) {
            IFhirSystemDao<Bundle, MetaDt> systemDao = myAppCtx.getBean("mySystemDaoDstu2", IFhirSystemDao.class);
            HspcConformanceProviderDstu2 confProvider = new HspcConformanceProviderDstu2(this, systemDao,
                    myAppCtx.getBean(DaoConfig.class),
                    myAppCtx.getBean(MetadataRepositoryDstu2Impl.class));
            confProvider.setImplementationDescription("HSPC Reference API Server - DSTU2");
            setServerConformanceProvider(confProvider);
        } else if (fhirVersionEnum == FhirVersionEnum.DSTU3) {
            IFhirSystemDao<org.hl7.fhir.dstu3.model.Bundle, Meta> systemDao = myAppCtx.getBean("mySystemDaoDstu3", IFhirSystemDao.class);
            HspcConformanceProviderStu3 confProvider = new HspcConformanceProviderStu3(
                    this,
                    systemDao,
                    myAppCtx.getBean(DaoConfig.class),
                    myAppCtx.getBean(MetadataRepositoryStu3.class));
            confProvider.setImplementationDescription("HSPC Reference API Server - STU3");
            setServerConformanceProvider(confProvider);
        } else if (fhirVersionEnum == FhirVersionEnum.R4) {
            IFhirSystemDao<org.hl7.fhir.r4.model.Bundle, org.hl7.fhir.r4.model.Meta> systemDao = myAppCtx.getBean("mySystemDaoR4", IFhirSystemDao.class);
            HspcConformanceProviderR4 confProvider = new HspcConformanceProviderR4(
                    this,
                    systemDao,
                    myAppCtx.getBean(DaoConfig.class),
                    myAppCtx.getBean(MetadataRepositoryR4.class));
            confProvider.setImplementationDescription("HSPC Reference API Server - R4");
            setServerConformanceProvider(confProvider);
        } else {
            throw new IllegalStateException();
        }


        /*
         * Enable ETag Support (this is already the default)
		 */
        setETagSupport(ETagSupportEnum.ENABLED);

        /*
         * This server tries to dynamically generate narratives
		 */
        FhirContext ctx = getFhirContext();
        ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

		/*
         * Default to JSON and pretty printing
		 */
        setDefaultPrettyPrint(true);
        setDefaultResponseEncoding(EncodingEnum.JSON);

		/*
         * -- New in HAPI FHIR 1.5 --
		 * This configures the server to page search results to and from
		 * the database, instead of only paging them to memory. This may mean
		 * a performance hit when performing searches that return lots of results,
		 * but makes the server much more scalable.
		 */
        setPagingProvider(myAppCtx.getBean(DatabaseBackedPagingProvider.class));

		/*
         * Load interceptors for the server from Spring (these are defined in FhirServerConfig.java)
		 */
        Collection<IServerInterceptor> interceptorBeans = myAppCtx.getBeansOfType(IServerInterceptor.class).values();
        for (IServerInterceptor interceptor : interceptorBeans) {
            this.registerInterceptor(interceptor);
        }

		/*
         * If you are hosting this server at a specific DNS name, the server will try to
		 * figure out the FHIR base URL based on what the web container tells it, but
		 * this doesn't always work. If you are setting links in your search bundles that
		 * just refer to "localhost", you might want to use a server address strategy:
		 */
        //setServerAddressStrategy(new HardcodedServerAddressStrategy("http://mydomain.com/fhir/baseDstu3"));

		/*
         * If you are using DSTU3+, you may want to add a terminology uploader, which allows
		 * uploading of external terminologies such as Snomed CT. Note that this uploader
		 * does not have any security attached (any anonymous user may use it by default)
		 * so it is a potential security vulnerability. Consider using an AuthorizationInterceptor
		 * with this feature.
		 */
        //if (fhirVersion == FhirVersionEnum.DSTU3) {
        //	 registerProvider(myAppCtx.getBean(TerminologyUploaderProviderDstu3.class));
        //}
    }

    /**
     * account for tenant and mapping
     */
    @Override
    protected String getRequestPath(String requestFullPath, String servletContextPath, String servletPath) {

        // trim off the servletContextPath
        String remainder = requestFullPath.substring(escapedLength(servletContextPath));

        if (remainder.length() > 0 && remainder.charAt(0) == '/') {
            remainder = remainder.substring(1);
        }

        // followed by tenant and fhir mapping
        String[] split = remainder.split("/", 3);

        // capture the whole path after the fhir mapping
        StringBuffer stringBuffer = new StringBuffer();
        boolean foundFhirMappingPath = false;
        for (String part : split) {
            if (foundFhirMappingPath) {
                stringBuffer.append(part);
                stringBuffer.append("/");
            } else {
                // check each of the fhirMappingPaths to see if one is found
                if (part.equals(fhirMappingPath) || part.equals(openMappingPath)) {
                    foundFhirMappingPath = true;
                }
            }
        }

        return stringBuffer.length() > 0
                ? stringBuffer.substring(0, stringBuffer.length() - 1)
                : "";
    }

    /**
     * Returns the server base URL (with no trailing '/') for a given request
     */
    @Override
    public String getServerBaseForRequest(HttpServletRequest theRequest) {
        String fhirServerBase = getServerAddressStrategy().determineServerBase(getServletContext(), theRequest);

        String[] split = fhirServerBase.split("/");

        StringBuffer result = new StringBuffer();
        for (String current : split) {
            result.append(current);

            if (current.equals(fhirMappingPath) || current.equals(openMappingPath)) {
                // found the base for request
                return result.toString();
            }

            // continue
            result.append("/");
        }

        throw new RuntimeException("Something bad happened, only matched: " + result.toString());
    }

}
