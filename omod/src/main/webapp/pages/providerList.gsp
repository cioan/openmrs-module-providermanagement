<%
    ui.decorateWith("appui", "standardEmrPage")
%>

<script type="text/javascript">
    var breadcrumbs = [
        { icon: "icon-home", link: '/' + OPENMRS_CONTEXT_PATH + '/index.htm' },
        { label: "${ ui.message("Provider Management/Providers List")}"}
    ];

</script>

<h2>${ ui.message("Manage Providers") }</h2>

<a href="${ ui.pageLink("providermanagement", "editProvider") }">
    <button id="create-account-button">${ ui.message("Add Provider") }</button>
</a>
<hr>

<table id="providers-list" width="100%" border="1" cellspacing="0" cellpadding="2">
    <thead>
        <tr>
            <th>${ ui.message("Identifier") }</th>
            <th>${ ui.message("coreapps.person.name") }</th>
            <th>${ ui.message("Role") }</th>
        </tr>
    </thead>

    <tbody>
        <% if ((providersList == null) ||
                (providersList != null && providersList.size() == 0)) { %>
        <tr>
            <td colspan="2">${ ui.message("coreapps.none") }</td>
        </tr>
        <% } %>
        <% providersList.each { provider ->
            def personId = provider.person.personId
            def personName = provider.name
        %>
        <tr id="provider-${ provider.person.personId}">
                <td>${ ui.format(provider.identifier) }</td>
                <td>${ ui.format(provider.name) }</td>
                <td>${ ui.format(provider.providerRole) }</td>

        </tr>
        <% } %>
    </tbody>
</table>

<% if ( (providersList != null) && (providersList.size() > 0) ) { %>
                    ${ ui.includeFragment("uicommons", "widget/dataTable", [ object: "#providers-list",
                                             options: [
                                                     bFilter: true,
                                                     bJQueryUI: true,
                                                     bLengthChange: false,
                                                     iDisplayLength: 10,
                                                     sPaginationType: '\"full_numbers\"',
                                                     bSort: false,
                                                     sDom: '\'ft<\"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg \"ip>\''
                                             ]
                    ]) }
<% } %>