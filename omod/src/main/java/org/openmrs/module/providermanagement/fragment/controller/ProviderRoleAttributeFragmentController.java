package org.openmrs.module.providermanagement.fragment.controller;


import org.openmrs.ProviderAttributeType;
import org.openmrs.module.providermanagement.ProviderRole;
import org.openmrs.ui.framework.SimpleObject;
import org.openmrs.ui.framework.UiUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProviderRoleAttributeFragmentController {

    public List<SimpleObject> getAttributeTypes(@RequestParam(value="roleId", required=true) ProviderRole role ,
                                           UiUtils ui) {


        List<SimpleObject> items = new ArrayList<SimpleObject>();
        for (ProviderAttributeType providerAttributeType : role.getProviderAttributeTypes()) {
            SimpleObject item = new SimpleObject();
            item.put("id", providerAttributeType.getId());
            item.put("name", providerAttributeType.getName());
            item.put("providerAttributeTypeId", providerAttributeType.getProviderAttributeTypeId());
            item.put("datatypeClassname", providerAttributeType.getDatatypeClassname());
            items.add(item);
        }
        return items;
    }

}
