function createAddPatientDialog() {
    addPatientDialog = emr.setupConfirmationDialog({
        selector: '#add-patient-dialog',
        actions: {
            confirm: function() {
                var patientId = jq("#patientId").val();
                var personId = jq("#providerId").val();
                var relationshipStartDateField = jq("#relationshipStartDate-field").val();
                emr.getFragmentActionWithCallback('providermanagement', 'providerEdit', 'addPatient'
                    , { provider: personId,
                        patient: patientId,
                        relationshipType: 15,
                        date: relationshipStartDateField
                    }
                    , function(data) {
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

function removePatientFromList(providerId, relationshipTypeId, relationshipId, endDate) {
    
    emr.getFragmentActionWithCallback('providermanagement', 'providerEdit', 'removePatient'
        , { provider: providerId,
            relationshipType: relationshipTypeId,
            patientRelationship: relationshipId,
            date: endDate
        }
        , function(data) {
            window.location.reload();
        },function(err){
            emr.handleError(err);
        });
}