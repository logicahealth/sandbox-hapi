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
import ca.uhn.fhir.jpa.interceptor.CascadingDeleteInterceptor;
import ca.uhn.fhir.jpa.provider.BaseJpaResourceProvider;
import ca.uhn.fhir.jpa.provider.JpaSystemProviderDstu2;
import ca.uhn.fhir.jpa.provider.dstu3.JpaSystemProviderDstu3;
import ca.uhn.fhir.jpa.provider.r4.JpaSystemProviderR4;
import ca.uhn.fhir.jpa.rp.r4.ValueSetResourceProvider;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.jpa.term.api.ITermReadSvcDstu3;
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
import org.hl7.fhir.r4.model.ValueSet;
import org.hspconsortium.platform.api.smart.fhir.HspcConformanceProviderDstu2;
import org.hspconsortium.platform.api.smart.fhir.HspcConformanceProviderR4;
import org.hspconsortium.platform.api.smart.fhir.HspcConformanceProviderStu3;
import org.hspconsortium.platform.api.smart.fhir.MetadataRepositoryDstu2Impl;
import org.hspconsortium.platform.api.smart.fhir.MetadataRepositoryR4Impl;
import org.hspconsortium.platform.api.smart.fhir.MetadataRepositoryStu3Impl;
import org.opencds.cqf.common.evaluation.EvaluationProviderFactory;
import org.opencds.cqf.common.retrieve.JpaFhirRetrieveProvider;
import org.opencds.cqf.cql.engine.fhir.searchparam.SearchParameterResolver;
import org.opencds.cqf.r4.evaluation.ProviderFactory;
import org.opencds.cqf.r4.providers.JpaTerminologyProvider;
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
            org.opencds.cqf.dstu3.providers.JpaTerminologyProvider localSystemTerminologyProvider = new org.opencds.cqf.dstu3.providers.JpaTerminologyProvider(myAppCtx.getBean("terminologyService", ITermReadSvcDstu3.class), getFhirContext(), (ca.uhn.fhir.jpa.rp.dstu3.ValueSetResourceProvider)this.getResourceProvider(org.hl7.fhir.dstu3.model.ValueSet.class));
            EvaluationProviderFactory providerFactory = new ProviderFactory(this.fhirContext, this.registry, localSystemTerminologyProvider);

            resolveProvidersStu3(providerFactory, localSystemTerminologyProvider, this.registry);
        } else if (fhirVersion == FhirVersionEnum.R4) {
            IFhirSystemDao<org.hl7.fhir.r4.model.Bundle, org.hl7.fhir.r4.model.Meta> systemDao = myAppCtx.getBean("mySystemDaoR4", IFhirSystemDao.class);
            HspcConformanceProviderR4 confProvider = new HspcConformanceProviderR4(this, systemDao,
                    myAppCtx.getBean(DaoConfig.class),
                    myAppCtx.getBean(MetadataRepositoryR4Impl.class));
            confProvider.setImplementationDescription(serverDescription);
            setServerConformanceProvider(confProvider);
            // CQF implementation
            JpaTerminologyProvider localSystemTerminologyProvider = new JpaTerminologyProvider(myAppCtx.getBean("terminologyService",  ITermReadSvcR4.class), getFhirContext(), (ValueSetResourceProvider)this.getResourceProvider(ValueSet.class));
            EvaluationProviderFactory providerFactory = new ProviderFactory(this.fhirContext, this.registry, localSystemTerminologyProvider);

            resolveProvidersR4(providerFactory, localSystemTerminologyProvider, this.registry);
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

        CascadingDeleteInterceptor cascadingDeleteInterceptor = (CascadingDeleteInterceptor) myAppCtx.getBean("cascadingDeleteInterceptor");
        this.registerInterceptor(cascadingDeleteInterceptor);

        /*
         * If you are using DSTU3+, you may want to add a terminology uploader, which allows
         * uploading of external terminologies such as Snomed CT. Note that this uploader
         * does not have any security attached (any anonymous user may use it by default)
         * so it is a potential security vulnerability. Consider using an AuthorizationInterceptor
         * with this feature.
         */
        if (fhirVersion == FhirVersionEnum.DSTU3) {
            registerProvider(myAppCtx.getBean(ca.uhn.fhir.jpa.provider.TerminologyUploaderProvider.class));
        }
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
    {
        org.opencds.cqf.library.stu3.NarrativeProvider narrativeProviderStu3 = this.getNarrativeProviderStu3();
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
        org.opencds.cqf.dstu3.providers.LibraryOperationsProvider libraryProvider = new org.opencds.cqf.dstu3.providers.LibraryOperationsProvider((ca.uhn.fhir.jpa.rp.dstu3.LibraryResourceProvider)this.getResourceProvider(org.hl7.fhir.dstu3.model.Library.class), narrativeProviderStu3);
        this.registerProvider(libraryProvider);

        // CQL Execution
        org.opencds.cqf.dstu3.providers.CqlExecutionProvider cql = new org.opencds.cqf.dstu3.providers.CqlExecutionProvider(libraryProvider, providerFactory, this.fhirContext);
        this.registerProvider(cql);

        // Bundle processing
        org.opencds.cqf.dstu3.providers.ApplyCqlOperationProvider bundleProvider = new org.opencds.cqf.dstu3.providers.ApplyCqlOperationProvider(providerFactory, this.getDao(org.hl7.fhir.dstu3.model.Bundle.class), this.fhirContext);
        this.registerProvider(bundleProvider);

        // Measure processing
        org.opencds.cqf.dstu3.providers.MeasureOperationsProvider measureProvider = new org.opencds.cqf.dstu3.providers.MeasureOperationsProvider(this.registry, providerFactory, narrativeProviderStu3, hqmfProvider,
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
    {
        org.opencds.cqf.library.r4.NarrativeProvider narrativeProviderR4 = this.getNarrativeProviderR4();
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
        org.opencds.cqf.r4.providers.LibraryOperationsProvider libraryProvider = new org.opencds.cqf.r4.providers.LibraryOperationsProvider((ca.uhn.fhir.jpa.rp.r4.LibraryResourceProvider)this.getResourceProvider(org.hl7.fhir.r4.model.Library.class), narrativeProviderR4);
        this.registerProvider(libraryProvider);

        // CQL Execution
        org.opencds.cqf.r4.providers.CqlExecutionProvider cql = new org.opencds.cqf.r4.providers.CqlExecutionProvider(libraryProvider, providerFactory, this.fhirContext);
        this.registerProvider(cql);

        // Bundle processing
        org.opencds.cqf.r4.providers.ApplyCqlOperationProvider bundleProvider = new org.opencds.cqf.r4.providers.ApplyCqlOperationProvider(providerFactory, this.getDao(org.hl7.fhir.r4.model.Bundle.class), this.fhirContext);
        this.registerProvider(bundleProvider);

        // Measure processing
        org.opencds.cqf.r4.providers.MeasureOperationsProvider measureProvider = new org.opencds.cqf.r4.providers.MeasureOperationsProvider(this.registry, providerFactory, narrativeProviderR4, hqmfProvider,
                libraryProvider, (ca.uhn.fhir.jpa.rp.r4.MeasureResourceProvider)this.getResourceProvider(org.hl7.fhir.r4.model.Measure.class));
        this.registerProvider(measureProvider);

         // ActivityDefinition processing
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
