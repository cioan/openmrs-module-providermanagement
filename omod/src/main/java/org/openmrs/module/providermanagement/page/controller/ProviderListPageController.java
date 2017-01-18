package org.openmrs.module.providermanagement.page.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.providermanagement.Provider;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.module.providermanagement.api.ProviderManagementService;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.page.PageModel;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

public class ProviderListPageController {

    private final Log log = LogFactory.getLog(getClass());

    public void get(PageModel model,
                    @RequestParam(value="providerRoles[]", required=false) ProviderRole[] providerRoles,
                    @RequestParam(value="resultFields[]", required=false) String[] resultFields,
                    UiUtils ui) {

        List<ProviderRole> providerRoleList = null;
        if (providerRoles != null && providerRoles.length > 0) {
            providerRoleList = Arrays.asList(providerRoles);
        }
        else {
            providerRoleList = Context.getService(ProviderManagementService.class).getAllProviderRoles(true);
        }

        // now fetch the providers list
        List<Provider> providersByRoles = Context.getService(ProviderManagementService.class).getProvidersByRoles(providerRoleList);
        model.addAttribute("providersList", providersByRoles);
    }
}
