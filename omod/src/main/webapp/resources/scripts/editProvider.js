function createAddPatientDialog() {
    addPatientDialog = emr.setupConfirmationDialog({
        selector: '#add-patient-dialog',
        actions: {
            confirm: function() {
                var patientId = jq("#patientId").val();
                var personId = jq("#providerId").val();
                var relationshipStartDateField = jq("#relationshipStartDate-field").val();
                var relationshipType = jq("select[name='relationshipType']").val();
                emr.getFragmentActionWithCallback('providermanagement', 'providerEdit', 'addPatient'
                    , { provider: personId,
                        patient: patientId,
                        relationshipType: relationshipType,
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

function createRemovePatientDialog(providerId, relationshipTypeId, relationshipId) {
    removePatientDialog = emr.setupConfirmationDialog({
        selector: '#remove-patient-dialog',
        actions: {
            confirm: function() {
                var relationshipEndDateField = jq("#relationshipEndDate-field").val();
                emr.getFragmentActionWithCallback('providermanagement', 'providerEdit', 'removePatient'
                    , { provider: providerId,
                        relationshipType: relationshipTypeId,
                        patientRelationship: relationshipId,
                        date: relationshipEndDateField
                    }
                    , function(data) {
                        removePatientDialog.close();
                        window.location.reload();
                    }, function(err){
                        emr.handleError(err);
                        removePatientDialog.close();
                    });
            },
            cancel: function() {
                removePatientDialog.close();
            }
        }
    });
}

function showRemovePatientDialog(){
    removePatientDialog.show();
}

function getProviderRoleAttributeTypes(roleId) {
    emr.getFragmentActionWithCallback('providermanagement', 'providerRoleAttribute', 'getAttributeTypes'
        , { roleId: roleId
        }
        , function(data) {
            console.log("SUCCESS! data=" + data);
            jq.each(data, function(index, value) {
               console.log("index= "  +index);
                console.log("value=" + value);
                console.log("id=" + value.id);
                console.log("name=" + value.name);
                return data;

            });
        }, function(err){
            emr.handleError(err);
            console.log("failed to get Attribute Types");
        });
}