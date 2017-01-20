function createAddPatientDialog() {
    addPatientDialog = emr.setupConfirmationDialog({
        selector: '#add-patient-dialog',
        actions: {
            confirm: function() {
                var patientId = jq("#patientId").val();
                var personId = jq("#providerId").val();
                var relationshipStartDate = jq("#relationshipStartDate-display").val();
                emr.getFragmentActionWithCallback('providermanagement', 'providerEdit', 'addPatient'
                    , { provider: personId,
                        patient: patientId,
                        relationshipType: 15,
                        date: "10-01-2017"
                    }
                    , function(data) {

                        // TODO Do we need to update this to specify return url, or is this link only going  to ever be used from the old visits view?
                        //visit.reloadPageWithoutVisitId();
                        addPatientDialog.close();
                        window.location.reload();
                    },function(err){
                        emr.handleError(err);
                        addPatientDialog.close();
                    });
            },
            cancel: function() {
                addPatientDialog.close();
            }
        }
    });
}

function showAddPatientDialog(){
    addPatientDialog.show();
}