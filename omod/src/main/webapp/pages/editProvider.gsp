<%
    ui.decorateWith("appui", "standardEmrPage")

    ui.includeCss("providermanagement", "bootstrap.css")

    ui.includeJavascript("providermanagement", "editProvider.js")

    def genderOptions = [ [label: ui.message("emr.gender.M"), value: 'M'],
                          [label: ui.message("emr.gender.F"), value: 'F'] ]

    def createAccount = (account.person.personId == null ? true : false);

    def afterSelectedUrl = '/providermanagement/editProvider.page?patientId={{patientId}}&personId=' + account.person.personId

    def providerRolesOptions = []
    providerRoles. each {
        providerRolesOptions.push([ label: ui.format(it), value: it.id ])
    }
    providerRolesOptions = providerRolesOptions.sort { it.label };

    def editDateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy")

%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("Provider List")}", link: '${ui.pageLink("providermanagement", "providerList")}' },
        { label: "${ ui.message("Edit Provider")}" }

    ];

    var selectPatientHandler = {
        handle: function (row, widgetData) {
            var query = widgetData.lastQuery;
            jq('#patientId').val(row.uuid);

            jq('#patient-search').val(
                    row.patientIdentifier.identifier + ", "
                    + row.person.personName.display + ", "
                    + row.person.gender + ", "
                    + row.person.age);

            jq('#patient-search-results').fadeOut();
            jq('#addPatientToList').show();
        }
    }

    jq(function() {

        var addPatientDialog = null;

        jq('#patient-search').attr("size", "40");

        jq("#add-patient-button").click(function(event) {
            createAddPatientDialog();
            showAddPatientDialog();
            event.preventDefault();
        });
    });


</script>

<style type="text/css">
#unlock-button {
    margin-top: 1em;
}
</style>


<div id="add-patient-dialog" class="dialog" style="display: none">
    <div class="dialog-header">
        <h3>${ ui.message("Create Relationship") }</h3>
    </div>
    <div class="dialog-content">
        <input type="hidden" id="providerId" value="${account.person.personId}"/>
        <input type="hidden" id="patientId" value=""/>
        <ul>
            <li class="info">
                <span>${ ui.message("Assign Patient to Provider") }</span>
            </li>
        </ul>

        ${ ui.message("Find Patient:") }
        ${ ui.includeFragment("coreapps", "patientsearch/patientSearchWidget",
                [ afterSelectedUrl: afterSelectedUrl,
                  rowSelectionHandler: "selectPatientHandler",
                  showLastViewedPatients: 'false' ])}

        ${ ui.includeFragment("uicommons", "field/datetimepicker", [
                id: "relationshipStartDate",
                formFieldName: "relationshipStartDate",
                label:"",
                defaultDate: new Date(),
                endDate: editDateFormat.format(new Date()),
                useTime: false,
        ])}

        <button class="confirm right">${ ui.message("coreapps.yes") }</button>
        <button class="cancel">${ ui.message("coreapps.no") }</button>
    </div>
</div>

<h3>${ (createAccount) ? ui.message("Create Provider") : ui.message("Edit Provider") }</h3>

<div class="row">
    <div class="col-sm-4">
        <div class="panel panel-info">
            <div class="panel-heading">
                <h3 class="panel-title">${ui.message("Provider")}</h3>
            </div>
            <div class="panel-body ">

                <form method="post" id="accountForm" autocomplete="off">
                    <!-- dummy fields so that Chrome doesn't autocomplete the real username/password fields with the users own password -->
                    <input style="display:none" type="text" name="wrong-username-from-autocomplete"/>
                    <input style="display:none" type="password" name="wrong-username-from-autocomplete"/>

                        ${ ui.includeFragment("uicommons", "field/text", [
                                label: ui.message("Family Name"),
                                formFieldName: "familyName",
                                initialValue: (account.familyName ?: '')
                        ])}

                        ${ ui.includeFragment("uicommons", "field/text", [
                                label: ui.message("Given Name"),
                                formFieldName: "givenName",
                                initialValue: (account.givenName ?: '')
                        ])}

                        ${ ui.includeFragment("uicommons", "field/radioButtons", [
                                label: ui.message("emr.gender"),
                                formFieldName: "gender",
                                initialValue: (account.gender ?: 'M'),
                                options: genderOptions
                        ])}


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

                    <div>
                        <input type="button" class="cancel" value="${ ui.message("emr.cancel") }" onclick="javascript:window.location='/${ contextPath }/providermanagement/providerList.page'" />
                        <input type="submit" class="confirm" id="save-button" value="${ ui.message("emr.save") }"  />
                    </div>


                </form>

            </div>
        </div>
    </div>

    <div class="col-sm-8">
        <div class="panel panel-info">
            <div class="panel-heading">
                <h3 class="panel-title">${ui.message("Patients")}</h3>
            </div>

            <div class="panel-body ">
                <table class="table table-condensed borderless">
                    <thead>

                    </thead>
                    <tbody>
                    <tr>
                        <div id="addPatientToList">
                            <a href="">
                                <button id="add-patient-button">${ ui.message("Add Patient") }
                                &nbsp; <i class="icon-plus"></i>
                                </button>
                            </a>
                        </div>
                    </tr>

                    <tr>
                        <table id="patients-list" width="100%" border="1" cellspacing="0" cellpadding="2">
                            <thead>
                            <tr>
                                <th>${ ui.message("Identifier") }</th>
                                <th>${ ui.message("coreapps.person.name") }</th>
                                <th>${ ui.message("coreapps.gender") }</th>
                                <th>${ ui.message("Birthdate") }</th>
                                <th>${ ui.message("Address") }</th>
                            </tr>
                            </thead>

                            <tbody>
                            <% if ((patientsList == null) ||
                                    (patientsList != null && patientsList.size() == 0)) { %>
                            <tr>
                                <td colspan="4">${ ui.message("coreapps.none") }</td>
                            </tr>
                            <% } %>
                            <% patientsList.each { patient ->

                            %>
                            <tr id="patient-${ patient.patientId}">
                                <td>${ ui.format(patient.patientIdentifier.identifier) }</td>
                                <td>${ ui.format(patient.personName) }</td>
                                <td>${ ui.format(patient.gender) }</td>
                                <td>${ ui.format(patient.birthdate) }</td>
                                <td>${ ui.format(patient.personAddress) }</td>
                            </tr>
                            <% } %>
                            </tbody>
                        </table>
                    </tr>

                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>