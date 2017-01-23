package org.openmrs.module.providermanagement.page.controller;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.ProviderAttribute;
import org.openmrs.ProviderAttributeType;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emrapi.account.AccountDomainWrapper;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.emrapi.account.AccountValidator;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.providermanagement.exception.InvalidRelationshipTypeException;
import org.openmrs.module.providermanagement.exception.PersonIsNotProviderException;
import org.openmrs.module.providermanagement.exception.SuggestionEvaluationException;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditProviderPageController {
    protected final Log log = LogFactory.getLog(getClass());

    class ProviderPatientRelationship {
        Patient patient = null;
        Relationship relationship= null;
        RelationshipType relationshipType = null;

        public ProviderPatientRelationship() {
        }

        public ProviderPatientRelationship(Patient patient, Relationship relationship, RelationshipType relationshipType) {
            this.patient = patient;
            this.relationship = relationship;
            this.relationshipType = relationshipType;
        }
    }

    public AccountDomainWrapper getAccount(@RequestParam(value = "personId", required = false) Person person,
                                           @SpringBean("accountService") AccountService accountService) {

        AccountDomainWrapper account;

        if (person == null) {
            account = accountService.getAccountByPerson(new Person());
        } else {
            account = accountService.getAccountByPerson(person);
            if (account == null)
                throw new APIException("Failed to find user account matching person with id:" + person.getPersonId());
        }

        return account;
    }

    public void get(PageModel model,
                    @MethodParam("getAccount") AccountDomainWrapper account,
                    @ModelAttribute("patientId") @BindParams Patient patient,
                    @SpringBean("patientService") PatientService patientService,
                    @SpringBean("accountService") AccountService accountService,
                    @SpringBean("adminService") AdministrationService administrationService,
                    @SpringBean("providerManagementService") ProviderManagementService providerManagementService) throws PersonIsNotProviderException, InvalidRelationshipTypeException, SuggestionEvaluationException {

        model.addAttribute("account", account);
        model.addAttribute("providerRoles", providerManagementService.getAllProviderRoles(false));

        List<ProviderPatientRelationship> patientsList = new ArrayList<ProviderPatientRelationship>();
        List<ProviderPatientRelationship> patientsHistoryList = new ArrayList<ProviderPatientRelationship>();
        List<RelationshipType> relationshipTypes = new ArrayList<RelationshipType>();
        Set<ProviderAttributeType> providerAttributeTypes = new HashSet<ProviderAttributeType>();

        Provider provider = account.getProvider();
        if (provider != null ) {
            ProviderRole providerRole = provider.getProviderRole();
            if (providerRole != null && providerRole.getRelationshipTypes() != null) {
                providerAttributeTypes = providerRole.getProviderAttributeTypes();
                for (RelationshipType relationshipType : providerRole.getRelationshipTypes() ) {
                    if (!relationshipType.isRetired()) {
                        relationshipTypes.add(relationshipType);
                        for (Relationship relationship : providerManagementService.getPatientRelationshipsForProvider(provider.getPerson(), relationshipType, null)) {
                            if (relationship.getEndDate() == null) {
                                patientsList.add(new ProviderPatientRelationship(patientService.getPatient(relationship.getPersonB().getId()), relationship, relationshipType));
                            } else {
                                patientsHistoryList.add(new ProviderPatientRelationship(patientService.getPatient(relationship.getPersonB().getId()), relationship, relationshipType));
                            }
                        }
                    }
                }
            }
        }
        model.addAttribute("relationshipTypes", relationshipTypes);
        model.addAttribute("patientsList", patientsList);
        model.addAttribute("patientsHistoryList", patientsHistoryList);
        model.addAttribute("providerAttributeTypes", providerAttributeTypes);
    }

    public String post(@MethodParam("getAccount") @BindParams AccountDomainWrapper account, BindingResult errors,
                       @RequestParam(value = "userEnabled", defaultValue = "false") boolean userEnabled,
                       @RequestParam(value = "providerIdentifier", required = false) String providerIdentifier,
                       @SpringBean("providerService") ProviderService providerService,
                       @SpringBean("messageSource") MessageSource messageSource,
                       @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                       @SpringBean("accountService") AccountService accountService,
                       @SpringBean("adminService") AdministrationService administrationService,
                       @SpringBean("providerManagementService") ProviderManagementService providerManagementService,
                       @SpringBean("accountValidator") AccountValidator accountValidator, PageModel model,
                       HttpServletRequest request) {

        accountValidator.validate(account, errors);

        Map<Integer, String> attributesMap = getAttributeMap("providerAttributeId_", request);
        Map<Integer, String> attributeTypesMap = getAttributeMap("attributeTypeId_", request);

        if (!errors.hasErrors()) {
            try {
                Provider provider = account.getProvider();

                if (StringUtils.isNotBlank(providerIdentifier)) {
                    provider.setIdentifier(providerIdentifier);
                }
                if ( attributesMap.size() > 0 ) {
                    for (Integer id : attributesMap.keySet()) {
                        ProviderAttribute providerAttribute = providerService.getProviderAttribute(id);
                        if (providerAttribute != null) {
                            providerAttribute.setValueReferenceInternal(attributesMap.get(id));
                        }
                    }
                } else if (attributeTypesMap.size() > 0 ) {
                    for (Integer typeId : attributeTypesMap.keySet()) {
                        ProviderAttributeType providerAttributeType = providerService.getProviderAttributeType(typeId);
                        if ( providerAttributeType != null ) {
                            ProviderAttribute attr = new ProviderAttribute();
                            attr.setAttributeType(providerAttributeType);
                            attr.setValueReferenceInternal(attributeTypesMap.get(typeId));
                            provider.addAttribute(attr);
                        }
                    }
                }
                accountService.saveAccount(account);
                request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_INFO_MESSAGE,
                        messageSourceService.getMessage("Provider saved"));
                request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");

                return "redirect:/providermanagement/editProvider.page?personId=" + account.getPerson().getId();
            } catch (Exception e) {
                log.warn("Some error occurred while saving account details:", e);
                request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                        messageSourceService.getMessage("Failed to save provider", new Object[]{e.getMessage()}, Context.getLocale()));
            }
        } else {
            sendErrorMessage(errors, messageSource, request);
        }

        model.addAttribute("errors", errors);
        model.addAttribute("account", account);
        model.addAttribute("providerRoles", providerManagementService.getAllProviderRoles(false));

        return "redirect:/providermanagement/editProvider.page";
    }


    private Map<Integer, String> getAttributeMap(String parameterPrefix, HttpServletRequest request) {
        Map<Integer, String> attributesMap = new HashMap<Integer, String>();
        Set<String> paramKeys = request.getParameterMap().keySet();
        for (String param : paramKeys) {
            if (param.startsWith(parameterPrefix)) {
                Integer providerAttributeId = Integer.valueOf(param.substring(parameterPrefix.length()));
                String providerAttributeValue = request.getParameter(param);
                if ((providerAttributeId != null) &&
                        (providerAttributeId.intValue() > 0) &&
                        StringUtils.isNotBlank(providerAttributeValue)) {
                    attributesMap.put(providerAttributeId, providerAttributeValue);
                }
            }
        }
        return attributesMap;
    }

    private void sendErrorMessage(BindingResult errors, MessageSource messageSource, HttpServletRequest request) {
        List<ObjectError> allErrors = errors.getAllErrors();
        String message = getMessageErrors(messageSource, allErrors);
        request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                message);
    }

    private String getMessageErrors(MessageSource messageSource, List<ObjectError> allErrors) {
        String message = "";
        for (ObjectError error : allErrors) {
            Object[] arguments = error.getArguments();
            String errorMessage = messageSource.getMessage(error.getCode(), arguments, Context.getLocale());
            message = message.concat(replaceArguments(errorMessage, arguments).concat("<br>"));
        }
        return message;
    }

    private String replaceArguments(String message, Object[] arguments) {
        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                String argument = (String) arguments[i];
                message = message.replaceAll("\\{" + i + "\\}", argument);
            }
        }
        return message;
    }
}
