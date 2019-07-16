/**
 *  * #%L
 *  *
 *  * %%
 *  * Copyright (C) 2014-2019 Healthcare Services Platform Consortium
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

package org.hspconsortium.platform.api.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.jpa.dao.DaoConfig;
import ca.uhn.fhir.jpa.dao.IFhirSystemDao;
import ca.uhn.fhir.jpa.provider.JpaSystemProviderDstu2;
import ca.uhn.fhir.jpa.provider.dstu3.JpaSystemProviderDstu3;
import ca.uhn.fhir.jpa.provider.dstu3.TerminologyUploaderProviderDstu3;
import ca.uhn.fhir.jpa.provider.r4.JpaSystemProviderR4;
import ca.uhn.fhir.jpa.rp.dstu3.*;
import ca.uhn.fhir.jpa.search.DatabaseBackedPagingProvider;
import ca.uhn.fhir.jpa.term.IHapiTerminologySvcDstu3;
import ca.uhn.fhir.jpa.util.ResourceProviderFactory;
import ca.uhn.fhir.model.dstu2.composite.MetaDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.server.ETagSupportEnum;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Meta;
import org.hspconsortium.platform.api.conformance.HspcConformanceProviderDstu2;
import org.hspconsortium.platform.api.conformance.HspcConformanceProviderR4;
import org.hspconsortium.platform.api.conformance.HspcConformanceProviderStu3;
import org.hspconsortium.platform.api.fhir.repository.MetadataRepositoryDstu2Impl;
import org.hspconsortium.platform.api.fhir.repository.MetadataRepositoryR4;
import org.hspconsortium.platform.api.fhir.repository.MetadataRepositoryStu3;
import org.opencds.cqf.cql.terminology.TerminologyProvider;
import org.opencds.cqf.interceptors.TransactionInterceptor;
import org.opencds.cqf.providers.*;
//import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletException;
import java.util.ArrayList;
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

        // The FhirContext is created in the "BaseJavaConfig<fhirVersion>" class. So for STU3, it is "BaseJavaConfigDstu3"
        FhirContext fhirContext = myAppCtx.getBean(FhirContext.class);
        setFhirContext(fhirContext);

        /*
         * The system provider implements non-resource-type methods, such as
         * transaction, and global history.
         */

        /*
         * The BaseJavaConfigDstu3.java class is a spring configuration
         * file which is automatically generated as a part of hapi-fhir-jpaserver-base and
         * contains bean definitions for a resource provider for each resource type
         */
        ResourceProviderFactory resourceProviders;

        Object systemProvider;
        if (fhirVersionEnum == FhirVersionEnum.DSTU2) {
            resourceProviders = myAppCtx.getBean("myResourceProvidersDstu2", ResourceProviderFactory.class);
            systemProvider = myAppCtx.getBean("mySystemProviderDstu2", JpaSystemProviderDstu2.class);
        } else if (fhirVersionEnum == FhirVersionEnum.DSTU3) {
            resourceProviders = myAppCtx.getBean("myResourceProvidersDstu3", ResourceProviderFactory.class);
            systemProvider = myAppCtx.getBean("mySystemProviderDstu3", JpaSystemProviderDstu3.class);
        } else if (fhirVersionEnum == FhirVersionEnum.R4) {
            resourceProviders = myAppCtx.getBean("myResourceProvidersR4", ResourceProviderFactory.class);
            systemProvider = myAppCtx.getBean("mySystemProviderR4", JpaSystemProviderR4.class);
        } else {
            throw new IllegalStateException();
        }
        setPlainProviders(systemProvider);

        setFhirContext(myAppCtx.getBean(FhirContext.class));

        registerProviders(resourceProviders.createProviders());
        registerProvider(systemProvider);

        List<IResourceProvider> beans = new ArrayList<>();
        for (int i = 0; i < resourceProviders.createProviders().size(); i++) {
            beans.add((IResourceProvider) resourceProviders.createProviders().get(i));
        }
        setResourceProviders(beans);

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
            // CQF implementation
            JpaDataProvider provider = new JpaDataProvider(beans);
            TerminologyProvider terminologyProvider = new JpaTerminologyProvider(myAppCtx.getBean("terminologyService", IHapiTerminologySvcDstu3.class), getFhirContext(), (ValueSetResourceProvider) provider.resolveResourceProvider("ValueSet"));
            provider.setTerminologyProvider(terminologyProvider);
            resolveResourceProviders(provider, systemDao);
            setResourceProviders(provider.getCollectionProviders());
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
        if (fhirVersionEnum == FhirVersionEnum.DSTU3) {
            registerProvider(myAppCtx.getBean(TerminologyUploaderProviderDstu3.class));
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
                if (part.equals(HapiFhirServletContextHolder.getInstance().getFhirMappingPath()) || part.equals(HapiFhirServletContextHolder.getInstance().getOpenMappingPath())) {
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

            if (current.equals(fhirMappingPath) || current.equals(openMappingPath)) {
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

    private void resolveResourceProviders(JpaDataProvider provider, IFhirSystemDao<org.hl7.fhir.dstu3.model.Bundle, Meta> systemDao) throws ServletException {
        NarrativeProvider narrativeProvider = new NarrativeProvider();
        HQMFProvider hqmfProvider = new HQMFProvider();
        // Bundle processing
//        FHIRBundleResourceProvider bundleProvider = new FHIRBundleResourceProvider(provider);
//        BundleResourceProvider jpaBundleProvider = (BundleResourceProvider) provider.resolveResourceProvider("Bundle");
//        bundleProvider.setDao(jpaBundleProvider.getDao());
//        bundleProvider.setContext(jpaBundleProvider.getContext());
//
//        try {
//            unregister(jpaBundleProvider, provider.getCollectionProviders());
//        } catch (Exception e) {
//            throw new ServletException("Unable to unregister provider: " + e.getMessage());
//        }
//
//        register(bundleProvider, provider.getCollectionProviders());

        // ValueSet processing
        FHIRValueSetResourceProvider valueSetProvider = new FHIRValueSetResourceProvider(new CodeSystemResourceProvider());
        ValueSetResourceProvider jpaValueSetProvider = (ValueSetResourceProvider) provider.resolveResourceProvider("ValueSet");
        valueSetProvider.setDao(jpaValueSetProvider.getDao());
        valueSetProvider.setContext(jpaValueSetProvider.getContext());

        try {
            unregister(jpaValueSetProvider, provider.getCollectionProviders());
        } catch (Exception e) {
            throw new ServletException("Unable to unregister provider: " + e.getMessage());
        }

        register(valueSetProvider, provider.getCollectionProviders());
        TransactionInterceptor transactionInterceptor = new TransactionInterceptor(valueSetProvider);
        registerInterceptor(transactionInterceptor);

        // Measure processing
        FHIRMeasureResourceProvider measureProvider = new FHIRMeasureResourceProvider(provider, systemDao, narrativeProvider, hqmfProvider);
        MeasureResourceProvider jpaMeasureProvider = (MeasureResourceProvider) provider.resolveResourceProvider("Measure");
        measureProvider.setDao(jpaMeasureProvider.getDao());
        measureProvider.setContext(jpaMeasureProvider.getContext());

        try {
            unregister(jpaMeasureProvider, provider.getCollectionProviders());
        } catch (Exception e) {
            throw new ServletException("Unable to unregister provider: " + e.getMessage());
        }

        register(measureProvider, provider.getCollectionProviders());

        // ActivityDefinition processing
        FHIRActivityDefinitionResourceProvider actDefProvider = new FHIRActivityDefinitionResourceProvider(provider);
        ActivityDefinitionResourceProvider jpaActDefProvider = (ActivityDefinitionResourceProvider) provider.resolveResourceProvider("ActivityDefinition");
        actDefProvider.setDao(jpaActDefProvider.getDao());
        actDefProvider.setContext(jpaActDefProvider.getContext());

        try {
            unregister(jpaActDefProvider, provider.getCollectionProviders());
        } catch (Exception e) {
            throw new ServletException("Unable to unregister provider: " + e.getMessage());
        }

        register(actDefProvider, provider.getCollectionProviders());

        // PlanDefinition processing
        FHIRPlanDefinitionResourceProvider planDefProvider = new FHIRPlanDefinitionResourceProvider(provider);
        PlanDefinitionResourceProvider jpaPlanDefProvider = (PlanDefinitionResourceProvider) provider.resolveResourceProvider("PlanDefinition");
        planDefProvider.setDao(jpaPlanDefProvider.getDao());
        planDefProvider.setContext(jpaPlanDefProvider.getContext());

        try {
            unregister(jpaPlanDefProvider, provider.getCollectionProviders());
        } catch (Exception e) {
            throw new ServletException("Unable to unregister provider: " + e.getMessage());
        }

        register(planDefProvider, provider.getCollectionProviders());

        // Patient processing - for bulk data export
//        BulkDataPatientProvider bulkDataPatientProvider = new BulkDataPatientProvider(provider);
//        PatientResourceProvider jpaPatientProvider = (PatientResourceProvider) provider.resolveResourceProvider("Patient");
//        bulkDataPatientProvider.setDao(jpaPatientProvider.getDao());
//        bulkDataPatientProvider.setContext(jpaPatientProvider.getContext());
//
//        try {
//            unregister(jpaPatientProvider, provider.getCollectionProviders());
//        } catch (Exception e) {
//            throw new ServletException("Unable to unregister provider: " + e.getMessage());
//        }
//
//        register(bulkDataPatientProvider, provider.getCollectionProviders());

        // Group processing - for bulk data export
//        BulkDataGroupProvider bulkDataGroupProvider = new BulkDataGroupProvider(provider);
//        GroupResourceProvider jpaGroupProvider = (GroupResourceProvider) provider.resolveResourceProvider("Group");
//        bulkDataGroupProvider.setDao(jpaGroupProvider.getDao());
//        bulkDataGroupProvider.setContext(jpaGroupProvider.getContext());
//
//        try {
//            unregister(jpaGroupProvider, provider.getCollectionProviders());
//        } catch (Exception e) {
//            throw new ServletException("Unable to unregister provider: " + e.getMessage());
//        }
//
//        register(bulkDataGroupProvider, provider.getCollectionProviders());
    }

    private void register(IResourceProvider provider, Collection<IResourceProvider> providers) {
        providers.add(provider);
    }

    private void unregister(IResourceProvider provider, Collection<IResourceProvider> providers) {
        providers.remove(provider);
    }
}
