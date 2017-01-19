package org.openmrs.module.providermanagement.page.controller;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.account.AccountDomainWrapper;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.emrapi.account.AccountValidator;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.module.uicommons.UiCommonsConstants;
import org.openmrs.ui.framework.annotation.BindParams;
import org.openmrs.ui.framework.annotation.MethodParam;
import org.openmrs.ui.framework.annotation.SpringBean;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class EditProviderPageController {
    protected final Log log = LogFactory.getLog(getClass());

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

    public void get(PageModel model, @MethodParam("getAccount") AccountDomainWrapper account,
                    @SpringBean("accountService") AccountService accountService,
                    @SpringBean("adminService") AdministrationService administrationService,
                    @SpringBean("providerManagementService") ProviderManagementService providerManagementService) {

        model.addAttribute("account", account);
        model.addAttribute("providerRoles", providerManagementService.getAllProviderRoles(false));
    }

    public String post(@MethodParam("getAccount") @BindParams AccountDomainWrapper account, BindingResult errors,
                       @RequestParam(value = "userEnabled", defaultValue = "false") boolean userEnabled,
                       @RequestParam(value = "providerIdentifier", required = false) String providerIdentifier,
                       @SpringBean("messageSource") MessageSource messageSource,
                       @SpringBean("messageSourceService") MessageSourceService messageSourceService,
                       @SpringBean("accountService") AccountService accountService,
                       @SpringBean("adminService") AdministrationService administrationService,
                       @SpringBean("providerManagementService") ProviderManagementService providerManagementService,
                       @SpringBean("accountValidator") AccountValidator accountValidator, PageModel model,
                       HttpServletRequest request) {

        // manually bind userEnabled (since checkboxes don't submit anything if unchecked));
        //account.setUserEnabled(false);

        accountValidator.validate(account, errors);

        if (!errors.hasErrors()) {
            try {
                if (StringUtils.isNotBlank(providerIdentifier)) {
                    account.getProvider().setIdentifier(providerIdentifier);
                }
                accountService.saveAccount(account);
                request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_INFO_MESSAGE,
                        messageSourceService.getMessage("emr.account.saved"));
                request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_TOAST_MESSAGE, "true");

                return "redirect:/providermanagement/providerList.page";
            } catch (Exception e) {
                log.warn("Some error occurred while saving account details:", e);
                request.getSession().setAttribute(UiCommonsConstants.SESSION_ATTRIBUTE_ERROR_MESSAGE,
                        messageSourceService.getMessage("emr.account.error.save.fail", new Object[]{e.getMessage()}, Context.getLocale()));
            }
        } else {
            sendErrorMessage(errors, messageSource, request);
        }

        // reload page on error
        // TODO: show password fields toggle should work better

        model.addAttribute("errors", errors);
        model.addAttribute("account", account);
        model.addAttribute("providerRoles", providerManagementService.getAllProviderRoles(false));

        return "providerList";
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
