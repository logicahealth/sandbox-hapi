/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
 *  * %%
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  * #L%
 */


package org.hspconsortium.platform.api;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.DaoRegistry;
import ca.uhn.fhir.jpa.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.BaseJpaResourceProvider;
import ca.uhn.fhir.jpa.provider.JpaSystemProviderDstu2;
import ca.uhn.fhir.jpa.provider.dstu3.JpaSystemProviderDstu3;
import ca.uhn.fhir.jpa.provider.r4.JpaSystemProviderR4;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.jpa.term.api.ITermReadSvcR4;
import ca.uhn.fhir.jpa.util.ResourceProviderFactory;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hspconsortium.platform.api.smart.fhir.HspcConformanceProviderDstu2;
import org.hspconsortium.platform.api.smart.fhir.HspcConformanceProviderR4;
import org.hspconsortium.platform.api.smart.fhir.HspcConformanceProviderStu3;
import org.hspconsortium.platform.api.smart.fhir.MetadataRepositoryDstu2Impl;
import org.hspconsortium.platform.api.smart.fhir.MetadataRepositoryR4Impl;
import org.hspconsortium.platform.api.smart.fhir.MetadataRepositoryStu3Impl;
import org.opencds.cqf.common.evaluation.EvaluationProviderFactory;
import org.opencds.cqf.common.retrieve.JpaFhirRetrieveProvider;
import org.opencds.cqf.cql.searchparam.SearchParameterResolver;
import org.opencds.cqf.cql.terminology.TerminologyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import java.util.Collection;

@Component("fhirRestServlet")
public class FhirRestServlet extends RestfulServer {

    private static final long serialVersionUID = 1L;

    @Autowired
    private ApplicationContext myAppCtx;

    @Autowired
    private Environment environment;

    // The FhirContext is created in the "BaseJavaConfig<fhirVersion>" class. So for STU3, it is "BaseJavaConfigDstu3"
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private FhirContext fhirContext;

    @Value("${hspc.platform.api.fhir.description}")
    private String serverDescription;

    @Value("${hspc.platform.api.fhir.serverAddress:default}")
    private String serverAddress;

    @Value("${hspc.platform.api.fhir.fhirSecureServletPath}")
    private String fhirSecureServletPath;

    private String fhirSecureServletPathPart;

    @Value("${hspc.platform.api.fhir.fhirOpenServletPath}")
    private String fhirOpenServletPath;

    private String fhirOpenServletPathPart;

    DaoRegistry registry;

    @SuppressWarnings("unchecked")
    @Override
    protected void initialize() throws ServletException {
        super.initialize();
        setFhirContext(fhirContext);

        this.registry = myAppCtx.getBean(DaoRegistry.class);

        fhirSecureServletPathPart = (fhirSecureServletPath != null && fhirSecureServletPath.length() > 0 ? fhirSecureServletPath.substring(1) : "");

        fhirOpenServletPathPart = (fhirOpenServletPath != null && fhirOpenServletPath.length() > 0 ? fhirOpenServletPath.substring(1) : "");

        FhirVersionEnum fhirVersion = fhirContext.getVersion().getVersion();


        // configure the parser to allow versioned resource references, see http://hapifhir.io/doc_resource_references.html#Versioned_References
        getFhirContext().getParserOptions().setStripVersionsFromReferences(false);

        ResourceProviderFactory resourceProviders;

		/* 
         * The system provider implements non-resource-type methods, such as
		 * transaction, and global history.
		 */
        Object systemProvider;
        if (fhirVersion == FhirVersionEnum.DSTU2) {
            resourceProviders = myAppCtx.getBean("myResourceProvidersDstu2", ResourceProviderFactory.class);
            systemProvider = myAppCtx.getBean("mySystemProviderDstu2", JpaSystemProviderDstu2.class);
        } else if (fhirVersion == FhirVersionEnum.DSTU3) {
            resourceProviders = myAppCtx.getBean("myResourceProvidersDstu3", ResourceProviderFactory.class);
            systemProvider = myAppCtx.getBean("mySystemProviderDstu3", JpaSystemProviderDstu3.class);
        } else if(fhirVersion == FhirVersionEnum.R4) {
            resourceProviders = myAppCtx.getBean("myResourceProvidersR4", ResourceProviderFactory.class);
            systemProvider = myAppCtx.getBean("mySystemProviderR4", JpaSystemProviderR4.class);
        } else {
            throw new IllegalStateException();
        }
        registerProviders(resourceProviders.createProviders());
        registerProvider(systemProvider);

		/*
         * The conformance provider exports the supported resources, search parameters, etc for
		 * this server. The JPA version adds resource counts to the exported statement, so it
		 * is a nice addition.
		 */
        if (fhirVersion == FhirVersionEnum.DSTU2) {
            IFhirSystemDao<ca.uhn.fhir.model.dstu2.resource.Bundle, MetaDt> systemDao = myAppCtx.getBean("mySystemDaoDstu2", IFhirSystemDao.class);
            HspcConformanceProviderDstu2 confProvider = new HspcConformanceProviderDstu2(this, systemDao,
                    myAppCtx.getBean(DaoConfig.class),
                    myAppCtx.getBean(MetadataRepositoryDstu2Impl.class));
            confProvider.setImplementationDescription(serverDescription);
            setServerConformanceProvider(confProvider);
        } else if (fhirVersion == FhirVersionEnum.DSTU3) {
            IFhirSystemDao<org.hl7.fhir.dstu3.model.Bundle, Meta> systemDao = myAppCtx.getBean("mySystemDaoDstu3", IFhirSystemDao.class);
            HspcConformanceProviderStu3 confProvider = new HspcConformanceProviderStu3(this, systemDao,
                    myAppCtx.getBean(DaoConfig.class),
                    myAppCtx.getBean(MetadataRepositoryStu3Impl.class));
            confProvider.setImplementationDescription(serverDescription);
            setServerConformanceProvider(confProvider);

            // CQF implementation
//            JpaDataProvider provider = new JpaDataProvider(beans);
//            // TODO JpaTerminologyProvider must be upgraded to HAPI 4.2
//            TerminologyProvider terminologyProvider = new org.opencds.cqf.dstu3.providers.JpaTerminologyProvider(myAppCtx.getBean("terminologyService", ITermReadSvcDstu3.class), getFhirContext(), (ValueSetResourceProvider) provider.resolveProvider("ValueSet"));
//            provider.setTerminologyProvider(terminologyProvider);
//            resolveResourceProviders(provider, systemDao);
//            TODO: fix to match the method
//            resolveProvidersStu3(providerFactory, localSystemTerminologyProvider, registry);
//            setResourceProviders(provider.getCollectionProviders());

        } else if (fhirVersion == FhirVersionEnum.R4) {
            IFhirSystemDao<org.hl7.fhir.r4.model.Bundle, org.hl7.fhir.r4.model.Meta> systemDao = myAppCtx.getBean("mySystemDaoR4", IFhirSystemDao.class);
            HspcConformanceProviderR4 confProvider = new HspcConformanceProviderR4(this, systemDao,
                    myAppCtx.getBean(DaoConfig.class),
                    myAppCtx.getBean(MetadataRepositoryR4Impl.class));
            confProvider.setImplementationDescription(serverDescription);
            setServerConformanceProvider(confProvider);
            // CQF implementation
//            JpaDataProvider provider = new JpaDataProvider(beans);
//            // TODO JpaTerminologyProvider must be upgraded to HAPI 4.2
//            TerminologyProvider terminologyProvider = new org.opencds.cqf.r4.providers.JpaTerminologyProvider(myAppCtx.getBean("terminologyService", ITermReadSvcR4.class), getFhirContext(), ca.uhn.fhir.jpa.rp.r4.ValueSetResourceProvider.class);
//            provider.setTerminologyProvider(terminologyProvider);
//            resolveResourceProviders(provider, systemDao);
//            TODO: fix to match the method
//            resolveProvidersR4(providerFactory, localSystemTerminologyProvider, registry);
//            setResourceProviders(provider.getCollectionProviders());
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
        getFhirContext().setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

		/*
         * Default to JSON and pretty printing generally doubles the size of the JSON resources
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
//
//        if (serverAddress != null && !StringUtils.isEmpty(serverAddress) && !serverAddress.equals("default")) {
//            setServerAddressStrategy(new MultiTenantServerAddressStrategy(serverAddress + fhirServletPath, environment));
//        } else {
//            setServerAddressStrategy(new MultiTenantServerAddressStrategy(environment));
//        }

        /*
         * If you are using DSTU3+, you may want to add a terminology uploader, which allows
         * uploading of external terminologies such as Snomed CT. Note that this uploader
         * does not have any security attached (any anonymous user may use it by default)
         * so it is a potential security vulnerability. Consider using an AuthorizationInterceptor
         * with this feature.
         */
        // TODO this must be upgraded to HAPI 4.2
//        if (fhirVersion == FhirVersionEnum.DSTU3) {
//            registerProvider(myAppCtx.getBean(TerminologyUploaderProviderDstu3.class));
//        }
    }

//    @Override
//    protected String getRequestPath(String requestFullPath, String servletContextPath, String servletPath) {
//        // remove the tenant info from the servletPath, then interpret the rest as the FHIR path
//        String withoutControllerPath = servletPath.substring(this.escapedLength(fhirServletPath) + 1);
//        int indexOfFhirPathStart = withoutControllerPath.indexOf("/");
//        if (indexOfFhirPathStart == -1) {
//            return "";
//        }
//        String fhirPath = withoutControllerPath.substring(indexOfFhirPathStart);
//        return fhirPath;
//    }
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
                if (part.equals(fhirSecureServletPathPart) || part.equals(fhirOpenServletPathPart)) {
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
    public String getServerBaseForRequest(ServletRequestDetails theRequest) {
        String fhirServerBase = getServerAddressStrategy().determineServerBase(getServletContext(), theRequest.getServletRequest());

        String[] split = fhirServerBase.split("/");

        StringBuffer result = new StringBuffer();
        for (String current : split) {
            result.append(current);

            if (current.equals(fhirSecureServletPathPart) || current.equals(fhirOpenServletPathPart)) {
                // found the base for request
                fhirServerBase = result.toString();
            }

            // continue
            result.append("/");
        }
        return fhirServerBase;
//        throw new RuntimeException("Something bad happened, only matched: " + result.toString());
    }

    /**
     * account for tenant and mapping
     */
    public static String getTenantPart(String servletPath) {
        String[] split = servletPath.split("/", 3);
        for (int i = 0; i < split.length; i++) {
            if (StringUtils.isNotEmpty(split[i])) {
                return split[i];
            }
        }
        throw new NullPointerException("Tenant does not exist in path: " + servletPath);
    }

    protected org.opencds.cqf.library.stu3.NarrativeProvider getNarrativeProviderStu3() {
        return new org.opencds.cqf.library.stu3.NarrativeProvider();
    }

    protected org.opencds.cqf.library.r4.NarrativeProvider getNarrativeProviderR4() {
        return new org.opencds.cqf.library.r4.NarrativeProvider();
    }

    // Since resource provider resolution not lazy, the providers here must be resolved in the correct order of dependencies.
    private void resolveProvidersStu3(EvaluationProviderFactory providerFactory, org.opencds.cqf.dstu3.providers.JpaTerminologyProvider localSystemTerminologyProvider, DaoRegistry registry)
            throws ServletException
    {
        org.opencds.cqf.library.stu3.NarrativeProvider narrativeProvider = this.getNarrativeProviderStu3();
        org.opencds.cqf.dstu3.providers.HQMFProvider hqmfProvider = new org.opencds.cqf.dstu3.providers.HQMFProvider();

        // Code System Update
        org.opencds.cqf.dstu3.providers.CodeSystemUpdateProvider csUpdate = new org.opencds.cqf.dstu3.providers.CodeSystemUpdateProvider(
                this.getDao(org.hl7.fhir.dstu3.model.ValueSet.class),
                this.getDao(org.hl7.fhir.dstu3.model.CodeSystem.class));
        this.registerProvider(csUpdate);

        // Cache Value Sets
        org.opencds.cqf.dstu3.providers.CacheValueSetsProvider cvs = new org.opencds.cqf.dstu3.providers.CacheValueSetsProvider(this.registry.getSystemDao(), this.getDao(org.hl7.fhir.dstu3.model.Endpoint.class));
        this.registerProvider(cvs);

        //Library processing
        org.opencds.cqf.dstu3.providers.LibraryOperationsProvider libraryProvider = new org.opencds.cqf.dstu3.providers.LibraryOperationsProvider((ca.uhn.fhir.jpa.rp.dstu3.LibraryResourceProvider)this.getResourceProvider(org.hl7.fhir.dstu3.model.Library.class), narrativeProvider);
        this.registerProvider(libraryProvider);

        // CQL Execution
        org.opencds.cqf.dstu3.providers.CqlExecutionProvider cql = new org.opencds.cqf.dstu3.providers.CqlExecutionProvider(libraryProvider, providerFactory);
        this.registerProvider(cql);

        // Bundle processing
        org.opencds.cqf.dstu3.providers.ApplyCqlOperationProvider bundleProvider = new org.opencds.cqf.dstu3.providers.ApplyCqlOperationProvider(providerFactory, this.getDao(org.hl7.fhir.dstu3.model.Bundle.class));
        this.registerProvider(bundleProvider);

        // Measure processing
        org.opencds.cqf.dstu3.providers.MeasureOperationsProvider measureProvider = new org.opencds.cqf.dstu3.providers.MeasureOperationsProvider(this.registry, providerFactory, narrativeProvider, hqmfProvider,
                libraryProvider, (ca.uhn.fhir.jpa.rp.dstu3.MeasureResourceProvider)this.getResourceProvider(org.hl7.fhir.dstu3.model.Measure.class));
        this.registerProvider(measureProvider);

        // // ActivityDefinition processing
        org.opencds.cqf.dstu3.providers.ActivityDefinitionApplyProvider actDefProvider = new org.opencds.cqf.dstu3.providers.ActivityDefinitionApplyProvider(this.fhirContext, cql, this.getDao(org.hl7.fhir.dstu3.model.ActivityDefinition.class));
        this.registerProvider(actDefProvider);

        JpaFhirRetrieveProvider localSystemRetrieveProvider = new JpaFhirRetrieveProvider(registry, new SearchParameterResolver(this.fhirContext));

        // PlanDefinition processing
        org.opencds.cqf.dstu3.providers.PlanDefinitionApplyProvider planDefProvider = new org.opencds.cqf.dstu3.providers.PlanDefinitionApplyProvider(this.fhirContext, actDefProvider, this.getDao(org.hl7.fhir.dstu3.model.PlanDefinition.class), this.getDao(org.hl7.fhir.dstu3.model.ActivityDefinition.class), cql);
        this.registerProvider(planDefProvider);

        org.opencds.cqf.dstu3.servlet.CdsHooksServlet.setPlanDefinitionProvider(planDefProvider);
        org.opencds.cqf.dstu3.servlet.CdsHooksServlet.setLibraryResolutionProvider(libraryProvider);
        org.opencds.cqf.dstu3.servlet.CdsHooksServlet.setSystemTerminologyProvider(localSystemTerminologyProvider);
        org.opencds.cqf.dstu3.servlet.CdsHooksServlet.setSystemRetrieveProvider(localSystemRetrieveProvider);
    }

    // Since resource provider resolution not lazy, the providers here must be resolved in the correct order of dependencies.
    private void resolveProvidersR4(EvaluationProviderFactory providerFactory, org.opencds.cqf.r4.providers.JpaTerminologyProvider localSystemTerminologyProvider, DaoRegistry registry)
            throws ServletException
    {
        org.opencds.cqf.library.r4.NarrativeProvider narrativeProvider = this.getNarrativeProviderR4();
        org.opencds.cqf.r4.providers.HQMFProvider hqmfProvider = new org.opencds.cqf.r4.providers.HQMFProvider();

        // Code System Update
        org.opencds.cqf.r4.providers.CodeSystemUpdateProvider csUpdate = new org.opencds.cqf.r4.providers.CodeSystemUpdateProvider(
                this.getDao(org.hl7.fhir.r4.model.ValueSet.class),
                this.getDao(org.hl7.fhir.r4.model.CodeSystem.class));
        this.registerProvider(csUpdate);

        // Cache Value Sets
        org.opencds.cqf.r4.providers.CacheValueSetsProvider cvs = new org.opencds.cqf.r4.providers.CacheValueSetsProvider(this.registry.getSystemDao(), this.getDao(org.hl7.fhir.r4.model.Endpoint.class));
        this.registerProvider(cvs);

        //Library processing
        org.opencds.cqf.r4.providers.LibraryOperationsProvider libraryProvider = new org.opencds.cqf.r4.providers.LibraryOperationsProvider((ca.uhn.fhir.jpa.rp.r4.LibraryResourceProvider)this.getResourceProvider(org.hl7.fhir.r4.model.Library.class), narrativeProvider);
        this.registerProvider(libraryProvider);

        // CQL Execution
        org.opencds.cqf.r4.providers.CqlExecutionProvider cql = new org.opencds.cqf.r4.providers.CqlExecutionProvider(libraryProvider, providerFactory);
        this.registerProvider(cql);

        // Bundle processing
        org.opencds.cqf.r4.providers.ApplyCqlOperationProvider bundleProvider = new org.opencds.cqf.r4.providers.ApplyCqlOperationProvider(providerFactory, this.getDao(org.hl7.fhir.r4.model.Bundle.class));
        this.registerProvider(bundleProvider);

        // Measure processing
        org.opencds.cqf.r4.providers.MeasureOperationsProvider measureProvider = new org.opencds.cqf.r4.providers.MeasureOperationsProvider(this.registry, providerFactory, narrativeProvider, hqmfProvider,
                libraryProvider, (ca.uhn.fhir.jpa.rp.r4.MeasureResourceProvider)this.getResourceProvider(org.hl7.fhir.r4.model.Measure.class));
        this.registerProvider(measureProvider);

        // // ActivityDefinition processing
        org.opencds.cqf.r4.providers.ActivityDefinitionApplyProvider actDefProvider = new org.opencds.cqf.r4.providers.ActivityDefinitionApplyProvider(this.fhirContext, cql, this.getDao(org.hl7.fhir.r4.model.ActivityDefinition.class));
        this.registerProvider(actDefProvider);

        JpaFhirRetrieveProvider localSystemRetrieveProvider = new JpaFhirRetrieveProvider(registry, new SearchParameterResolver(this.fhirContext));

        // PlanDefinition processing
        org.opencds.cqf.r4.providers.PlanDefinitionApplyProvider planDefProvider = new org.opencds.cqf.r4.providers.PlanDefinitionApplyProvider(this.fhirContext, actDefProvider, this.getDao(org.hl7.fhir.r4.model.PlanDefinition.class), this.getDao(org.hl7.fhir.r4.model.ActivityDefinition.class), cql);
        this.registerProvider(planDefProvider);

        org.opencds.cqf.r4.servlet.CdsHooksServlet.setPlanDefinitionProvider(planDefProvider);
        org.opencds.cqf.r4.servlet.CdsHooksServlet.setLibraryResolutionProvider(libraryProvider);
        org.opencds.cqf.r4.servlet.CdsHooksServlet.setSystemTerminologyProvider(localSystemTerminologyProvider);
        org.opencds.cqf.r4.servlet.CdsHooksServlet.setSystemRetrieveProvider(localSystemRetrieveProvider);
    }



    protected <T extends IBaseResource> IFhirResourceDao<T> getDao(Class<T> clazz) {
        return this.registry.getResourceDao(clazz);
    }


    protected <T extends IBaseResource> BaseJpaResourceProvider<T> getResourceProvider(Class<T> clazz) {
        return (BaseJpaResourceProvider<T> ) this.getResourceProviders().stream()
                .filter(x -> x.getResourceType().getSimpleName().equals(clazz.getSimpleName())).findFirst().get();
    }
}



//  CQF Ruler needs to be upgraded to HAPI 4.2
//    private void resolveResourceProviders(JpaDataProvider provider, IFhirSystemDao<org.hl7.fhir.dstu3.model.Bundle, Meta> systemDao) throws ServletException {
//        NarrativeProvider narrativeProvider = new NarrativeProvider();
//        HQMFProvider hqmfProvider = new HQMFProvider();
//        // Bundle processing
////        FHIRBundleResourceProvider bundleProvider = new FHIRBundleResourceProvider(provider);
////        BundleResourceProvider jpaBundleProvider = (BundleResourceProvider) provider.resolveResourceProvider("Bundle");
////        bundleProvider.setDao(jpaBundleProvider.getDao());
////        bundleProvider.setContext(jpaBundleProvider.getContext());
////
////        try {
////            unregister(jpaBundleProvider, provider.getCollectionProviders());
////        } catch (Exception e) {
////            throw new ServletException("Unable to unregister provider: " + e.getMessage());
////        }
////
////        register(bundleProvider, provider.getCollectionProviders());
//
//        // ValueSet processing
//        FHIRValueSetResourceProvider valueSetProvider = new FHIRValueSetResourceProvider(new CodeSystemResourceProvider());
//        ValueSetResourceProvider jpaValueSetProvider = (ValueSetResourceProvider) provider.resolveResourceProvider("ValueSet");
//        valueSetProvider.setDao(jpaValueSetProvider.getDao());
//        valueSetProvider.setContext(jpaValueSetProvider.getContext());
//
//        try {
//            unregister(jpaValueSetProvider, provider.getCollectionProviders());
//        } catch (Exception e) {
//            throw new ServletException("Unable to unregister provider: " + e.getMessage());
//        }
//
//        register(valueSetProvider, provider.getCollectionProviders());
////        TransactionInterceptor transactionInterceptor = new TransactionInterceptor(valueSetProvider);
////        registerInterceptor(transactionInterceptor);
//
//        // Measure processing
//        FHIRMeasureResourceProvider measureProvider = new FHIRMeasureResourceProvider(provider, systemDao, narrativeProvider, hqmfProvider);
//        MeasureResourceProvider jpaMeasureProvider = (MeasureResourceProvider) provider.resolveResourceProvider("Measure");
//        measureProvider.setDao(jpaMeasureProvider.getDao());
//        measureProvider.setContext(jpaMeasureProvider.getContext());
//
//        try {
//            unregister(jpaMeasureProvider, provider.getCollectionProviders());
//        } catch (Exception e) {
//            throw new ServletException("Unable to unregister provider: " + e.getMessage());
//        }
//
//        register(measureProvider, provider.getCollectionProviders());
//
//        // ActivityDefinition processing
//        FHIRActivityDefinitionResourceProvider actDefProvider = new FHIRActivityDefinitionResourceProvider(provider);
//        ActivityDefinitionResourceProvider jpaActDefProvider = (ActivityDefinitionResourceProvider) provider.resolveResourceProvider("ActivityDefinition");
//        actDefProvider.setDao(jpaActDefProvider.getDao());
//        actDefProvider.setContext(jpaActDefProvider.getContext());
//
//        try {
//            unregister(jpaActDefProvider, provider.getCollectionProviders());
//        } catch (Exception e) {
//            throw new ServletException("Unable to unregister provider: " + e.getMessage());
//        }
//
//        register(actDefProvider, provider.getCollectionProviders());
//
//        // PlanDefinition processing
//        FHIRPlanDefinitionResourceProvider planDefProvider = new FHIRPlanDefinitionResourceProvider(provider);
//        PlanDefinitionResourceProvider jpaPlanDefProvider = (PlanDefinitionResourceProvider) provider.resolveResourceProvider("PlanDefinition");
//        planDefProvider.setDao(jpaPlanDefProvider.getDao());
//        planDefProvider.setContext(jpaPlanDefProvider.getContext());
//
//        try {
//            unregister(jpaPlanDefProvider, provider.getCollectionProviders());
//        } catch (Exception e) {
//            throw new ServletException("Unable to unregister provider: " + e.getMessage());
//        }
//
//        register(planDefProvider, provider.getCollectionProviders());
//
//        // Patient processing - for bulk data export
////        BulkDataPatientProvider bulkDataPatientProvider = new BulkDataPatientProvider(provider);
////        PatientResourceProvider jpaPatientProvider = (PatientResourceProvider) provider.resolveResourceProvider("Patient");
////        bulkDataPatientProvider.setDao(jpaPatientProvider.getDao());
////        bulkDataPatientProvider.setContext(jpaPatientProvider.getContext());
////
////        try {
////            unregister(jpaPatientProvider, provider.getCollectionProviders());
////        } catch (Exception e) {
////            throw new ServletException("Unable to unregister provider: " + e.getMessage());
////        }
////
////        register(bulkDataPatientProvider, provider.getCollectionProviders());
//
//        // Group processing - for bulk data export
////        BulkDataGroupProvider bulkDataGroupProvider = new BulkDataGroupProvider(provider);
////        GroupResourceProvider jpaGroupProvider = (GroupResourceProvider) provider.resolveResourceProvider("Group");
////        bulkDataGroupProvider.setDao(jpaGroupProvider.getDao());
////        bulkDataGroupProvider.setContext(jpaGroupProvider.getContext());
////
////        try {
////            unregister(jpaGroupProvider, provider.getCollectionProviders());
////        } catch (Exception e) {
////            throw new ServletException("Unable to unregister provider: " + e.getMessage());
////        }
////
////        register(bulkDataGroupProvider, provider.getCollectionProviders());
//    }

