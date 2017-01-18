<%
    ui.includeCss("providermanagement", "providerManagement.css")

    ui.includeJavascript("uicommons", "jquery-1.12.4.min.js", Integer.MAX_VALUE)
    ui.includeJavascript("uicommons", "jquery-ui-1.9.2.custom.min.js", Integer.MAX_VALUE - 10)
    ui.includeJavascript("uicommons", "underscore-min.js", Integer.MAX_VALUE - 10)
    ui.includeJavascript("uicommons", "knockout-2.2.1.js", Integer.MAX_VALUE - 15)
    ui.includeJavascript("uicommons", "emr.js", Integer.MAX_VALUE - 15)

    ui.includeCss("uicommons", "styleguide/jquery-ui-1.9.2.custom.min.css", Integer.MAX_VALUE - 10)

    // toastmessage plugin: https://github.com/akquinet/jquery-toastmessage-plugin/wiki
    ui.includeJavascript("uicommons", "jquery.toastmessage.js", Integer.MAX_VALUE - 20)
    ui.includeCss("uicommons", "styleguide/jquery.toastmessage.css", Integer.MAX_VALUE - 20)

    // simplemodal plugin: http://www.ericmmartin.com/projects/simplemodal/
    ui.includeJavascript("uicommons", "jquery.simplemodal.1.4.4.min.js", Integer.MAX_VALUE - 20)
%>


<!DOCTYPE html>
<html>
    <head>
        <title>${ "OpenMRS" }</title>
        <link rel="shortcut icon" type="image/ico" href="/${ ui.contextPath() }/images/openmrs-favicon.ico"/>
        <link rel="icon" type="image/png\" href="/${ ui.contextPath() }/images/openmrs-favicon.png"/>
        ${ ui.resourceLinks() }
    </head>
    <body>

        <script>
            // var jq = jQuery;
        </script>


        <div id="providerHeader">
            <!-- banner -->
            ${ ui.includeFragment("providermanagement", "providerManagementBanner") }

            <!-- include the menu -->
            ${ ui.includeFragment("providermanagement","providerManagementMenu") }
        </div>

        <!--  show any generic error messages -->
        ${ ui.includeFragment("uilibrary", "flashMessage") }

        <div id="content">
            ${ config.content }
        </div>
    </body>
</html>

