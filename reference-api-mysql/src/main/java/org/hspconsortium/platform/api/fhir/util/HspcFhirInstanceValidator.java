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

//package org.hspconsortium.platform.api.fhir.util;
//
//import ca.uhn.fhir.context.ConfigurationException;
//import ca.uhn.fhir.context.FhirContext;
//import ca.uhn.fhir.rest.api.EncodingEnum;
//import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
//import ca.uhn.fhir.validation.IValidationContext;
//import ca.uhn.fhir.validation.IValidatorModule;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import java.io.StringReader;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import org.apache.commons.lang3.Validate;
//import org.hl7.fhir.dstu3.hapi.ctx.HapiWorkerContext;
//import org.hl7.fhir.dstu3.hapi.ctx.IValidationSupport;
//import org.hl7.fhir.dstu3.model.Base;
//import org.hl7.fhir.dstu3.model.StructureDefinition;
//import org.hl7.fhir.dstu3.model.TypeDetails;
//import org.hl7.fhir.dstu3.utils.FHIRPathEngine.IEvaluationContext;
//import org.hl7.fhir.dstu3.utils.FHIRPathEngine.IEvaluationContext.FunctionDetails;
//import org.hl7.fhir.dstu3.utils.IResourceValidator.BestPracticeWarningLevel;
//import org.hl7.fhir.dstu3.utils.IResourceValidator.IdStatus;
//import org.hl7.fhir.dstu3.validation.InstanceValidator;
//import org.hl7.fhir.exceptions.PathEngineException;
//import org.hl7.fhir.utilities.validation.ValidationMessage;
//import org.hl7.fhir.utilities.validation.ValidationMessage.IssueSeverity;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//import ca.uhn.fhir.context.ConfigurationException;
//import ca.uhn.fhir.context.FhirContext;
//import ca.uhn.fhir.rest.api.EncodingEnum;
//import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
//import ca.uhn.fhir.validation.IValidationContext;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import org.hl7.fhir.dstu3.hapi.ctx.HapiWorkerContext;
//import org.hl7.fhir.dstu3.hapi.ctx.IValidationSupport;
//import org.hl7.fhir.dstu3.hapi.validation.DefaultProfileValidationSupport;
//import org.hl7.fhir.dstu3.hapi.validation.FhirInstanceValidator;
//import org.hl7.fhir.dstu3.model.StructureDefinition;
//import org.hl7.fhir.dstu3.utils.IResourceValidator;
//import org.hl7.fhir.dstu3.validation.InstanceValidator;
//import org.hl7.fhir.utilities.validation.ValidationMessage;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//import org.hl7.fhir.dstu3.utils.IResourceValidator.BestPracticeWarningLevel;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import java.io.StringReader;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
////return super.validate(theCtx, theInput, theEncoding);
//
//public class HspcFhirInstanceValidator extends FhirInstanceValidator {
//
//    private static final Logger ourLog = LoggerFactory.getLogger(FhirInstanceValidator.class);
//    private boolean myAnyExtensionsAllowed;
//    private BestPracticeWarningLevel myBestPracticeWarningLevel;
//    private DocumentBuilderFactory myDocBuilderFactory;
//    private StructureDefinition myStructureDefintion;
//    private IValidationSupport myValidationSupport;
//    private boolean noTerminologyChecks;
//
//    public HspcFhirInstanceValidator() {
//        this(new DefaultProfileValidationSupport());
//    }
//
//    public HspcFhirInstanceValidator(IValidationSupport theValidationSupport) {
//        this.myAnyExtensionsAllowed = true;
//        this.noTerminologyChecks = false;
//        this.myDocBuilderFactory = DocumentBuilderFactory.newInstance();
//        this.myDocBuilderFactory.setNamespaceAware(true);
//        this.myValidationSupport = theValidationSupport;
//    }
//
//    private String determineResourceName(Document theDocument) {
//        Element root = null;
//        NodeList list = theDocument.getChildNodes();
//
//        for(int i = 0; i < list.getLength(); ++i) {
//            if (list.item(i) instanceof Element) {
//                root = (Element)list.item(i);
//                break;
//            }
//        }
//
//        root = theDocument.getDocumentElement();
//        return root.getLocalName();
//    }
//
//    private StructureDefinition findStructureDefinitionForResourceName(FhirContext theCtx, String resourceName) {
//        String sdName = "http://hl7.org/fhir/StructureDefinition/" + resourceName;
//        StructureDefinition profile = this.myStructureDefintion != null ? this.myStructureDefintion : this.myValidationSupport.fetchStructureDefinition(theCtx, sdName);
//        return profile;
//    }
//
//    public BestPracticeWarningLevel getBestPracticeWarningLevel() {
//        return this.myBestPracticeWarningLevel;
//    }
//
//    public IValidationSupport getValidationSupport() {
//        return this.myValidationSupport;
//    }
//
//    public boolean isAnyExtensionsAllowed() {
//        return this.myAnyExtensionsAllowed;
//    }
//
//    public void setAnyExtensionsAllowed(boolean theAnyExtensionsAllowed) {
//        this.myAnyExtensionsAllowed = theAnyExtensionsAllowed;
//    }
//
//    public boolean isNoTerminologyChecks() {
//        return this.noTerminologyChecks;
//    }
//
//    public void setNoTerminologyChecks(boolean theNoTerminologyChecks) {
//        this.noTerminologyChecks = theNoTerminologyChecks;
//    }
//
//    public void setBestPracticeWarningLevel(BestPracticeWarningLevel theBestPracticeWarningLevel) {
//        Validate.notNull(theBestPracticeWarningLevel);
//        this.myBestPracticeWarningLevel = theBestPracticeWarningLevel;
//    }
//
//    public void setStructureDefintion(StructureDefinition theStructureDefintion) {
//        this.myStructureDefintion = theStructureDefintion;
//    }
//
//    public void setValidationSupport(IValidationSupport theValidationSupport) {
//        this.myValidationSupport = theValidationSupport;
//    }
//
//    protected List<ValidationMessage> validate(FhirContext theCtx, String theInput, EncodingEnum theEncoding) {
//        HapiWorkerContext workerContext = new HapiWorkerContext(theCtx, this.myValidationSupport);
//        FhirInstanceValidator.NullEvaluationContext evaluationCtx = new FhirInstanceValidator.NullEvaluationContext();
//
//        InstanceValidator v;
//        try {
//            v = new InstanceValidator(workerContext, evaluationCtx);
//        } catch (Exception var16) {
//            throw new ConfigurationException(var16);
//        }
//
//        v.setBestPracticeWarningLevel(this.getBestPracticeWarningLevel());
//        v.setAnyExtensionsAllowed(this.isAnyExtensionsAllowed());
//        v.setResourceIdRule(IdStatus.OPTIONAL);
//        v.setNoTerminologyChecks(this.isNoTerminologyChecks());
//        List<ValidationMessage> messages = new ArrayList();
//        if (theEncoding == EncodingEnum.XML) {
//            Document document;
//            try {
//                DocumentBuilder builder = this.myDocBuilderFactory.newDocumentBuilder();
//                InputSource src = new InputSource(new StringReader(theInput));
//                document = builder.parse(src);
//            } catch (Exception var15) {
//                ourLog.error("Failure to parse XML input", var15);
//                ValidationMessage m = new ValidationMessage();
//                m.setLevel(IssueSeverity.FATAL);
//                m.setMessage("Failed to parse input, it does not appear to be valid XML:" + var15.getMessage());
//                return Collections.singletonList(m);
//            }
//
//            String resourceName = this.determineResourceName(document);
//            StructureDefinition profile = this.findStructureDefinitionForResourceName(theCtx, resourceName);
//            if (profile != null) {
//                try {
//                    v.validate((Object)null, messages, document, profile);
//                } catch (Exception var14) {
//                    if(messages.size()==0) {
//                        throw new InternalErrorException("Unexpected failure while validating resource", var14);
//                    }
//                    String mess = messages.get(0).getMessage();
//                    throw new InternalErrorException(mess, var14);
//                }
//            }
//        } else {
//            if (theEncoding != EncodingEnum.JSON) {
//                throw new IllegalArgumentException("Unknown encoding: " + theEncoding);
//            }
//
//            Gson gson = (new GsonBuilder()).create();
//            JsonObject json = (JsonObject)gson.fromJson(theInput, JsonObject.class);
//            String resourceName = json.get("resourceType").getAsString();
//            StructureDefinition profile = this.findStructureDefinitionForResourceName(theCtx, resourceName);
//            if (profile != null) {
//                try {
//                    v.validate((Object)null, messages, json, profile);
//                } catch (Exception var13) {
//                    if(messages.size()==0) {
//                        throw new InternalErrorException("Unexpected failure while validating resource", var13);
//                    }
//                    String mess = messages.get(0).getMessage();
//                    throw new InternalErrorException(mess, var13);
//                }
//            }
//        }
//
//        for(int i = 0; i < messages.size(); ++i) {
//            ValidationMessage next = (ValidationMessage)messages.get(i);
//            if ("Binding has no source, so can't be checked".equals(next.getMessage())) {
//                messages.remove(i);
//                --i;
//            }
//        }
//
//        return messages;
//    }
//
//    protected List<ValidationMessage> validate(IValidationContext<?> theCtx) {
//        return this.validate(theCtx.getFhirContext(), theCtx.getResourceAsString(), theCtx.getResourceAsStringEncoding());
//    }
//
//    public class NullEvaluationContext implements IEvaluationContext {
//        public NullEvaluationContext() {
//        }
//
//        public TypeDetails checkFunction(Object theAppContext, String theFunctionName, List<TypeDetails> theParameters) throws PathEngineException {
//            return null;
//        }
//
//        public List<Base> executeFunction(Object theAppContext, String theFunctionName, List<List<Base>> theParameters) {
//            return null;
//        }
//
//        public boolean log(String theArgument, List<Base> theFocus) {
//            return false;
//        }
//
//        public Base resolveConstant(Object theAppContext, String theName) throws PathEngineException {
//            return null;
//        }
//
//        public TypeDetails resolveConstantType(Object theAppContext, String theName) throws PathEngineException {
//            return null;
//        }
//
//        public FunctionDetails resolveFunction(String theFunctionName) {
//            return null;
//        }
//
//        public Base resolveReference(Object theAppContext, String theUrl) {
//            return null;
//        }
//    }
//}
