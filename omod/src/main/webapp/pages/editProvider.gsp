<%
    ui.decorateWith("appui", "standardEmrPage")

    def genderOptions = [ [label: ui.message("emr.gender.M"), value: 'M'],
                          [label: ui.message("emr.gender.F"), value: 'F'] ]

    def createAccount = (account.person.personId == null ? true : false);

    def providerRolesOptions = []
    providerRoles. each {
        providerRolesOptions.push([ label: ui.format(it), value: it.id ])
    }
    providerRolesOptions = providerRolesOptions.sort { it.label };

%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("Provider List")}", link: '${ui.pageLink("providermanagement", "providerList")}' },
        { label: "${ ui.message("Edit Provider")}" }

    ];
</script>

<style type="text/css">
#unlock-button {
    margin-top: 1em;
}
</style>

<h3>${ (createAccount) ? ui.message("Create Provider") : ui.message("Edit Provider") }</h3>

<form method="post" id="accountForm" autocomplete="off">

    <!-- dummy fields so that Chrome doesn't autocomplete the real username/password fields with the users own password -->
    <input style="display:none" type="text" name="wrong-username-from-autocomplete"/>
    <input style="display:none" type="password" name="wrong-username-from-autocomplete"/>

    <fieldset>
        <legend>${ ui.message("emr.person.details") }</legend>

        ${ ui.includeFragment("uicommons", "field/text", [
                label: ui.message("emr.person.familyName"),
                formFieldName: "familyName",
                initialValue: (account.familyName ?: '')
        ])}

        ${ ui.includeFragment("uicommons", "field/text", [
                label: ui.message("emr.person.givenName"),
                formFieldName: "givenName",
                initialValue: (account.givenName ?: '')
        ])}

        ${ ui.includeFragment("uicommons", "field/radioButtons", [
                label: ui.message("emr.gender"),
                formFieldName: "gender",
                initialValue: (account.gender ?: 'M'),
                options: genderOptions
        ])}
    </fieldset>

    <fieldset>
        <legend>${ ui.message("emr.provider.details") }</legend>
        <div class="emr_providerDetails">
            ${ ui.includeFragment("uicommons", "field/text", [
                    label: ui.message("Identifier"),
                    formFieldName: "providerIdentifier",
                    initialValue: (account.provider ? account.provider.identifier: '')
            ])}
            <p>
                ${ ui.includeFragment("uicommons", "field/dropDown", [
                        label: ui.message("Provider Role"),
                        emptyOptionLabel: ui.message("emr.chooseOne"),
                        formFieldName: "providerRole",
                        initialValue: (account.providerRole?.id ?: ''),
                        options: providerRolesOptions
                ])}
            </p>

        </div>

    </fieldset>

    <div>
        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="javascript:window.location='/${ contextPath }/providermanagement/providerList.page'" />
        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
    </div>


</form>