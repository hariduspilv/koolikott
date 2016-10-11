define([
    'angularAMD',
    'services/serverCallService'
], function(angularAMD) {
    var instance;

    var brokenMaterialsCallbacks = [];

    var BROKEN_MATERIALS;

    angularAMD.factory('learningObjectHelper', ['serverCallService',
        function(serverCallService) {

            init();

            function init() {
                serverCallService.makeGet("rest/material/getBroken", {}, getBrokenItemsSuccess, getItemsFail);
            }

            function getBrokenItemsSuccess(data) {
                console.log("GOT BROKEN ITEMS");
                console.log(items);
                if (!isEmpty(data)) {
                    BROKEN_MATERIALS = data;
                    brokenMaterialsCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                }
            }

            function getItemsFail() {
                console.log('Getting data failed.')
            }

            instance = {
                loadBrokenMaterials: function(callback) {
                    if (BROKEN_MATERIALS) {
                        callback(BROKEN_MATERIALS);
                    } else {
                        // Save callback, call it when data arrives
                        brokenMaterialsCallbacks.push(callback);
                    }
                }
            };

            return instance;
        }
    ]);
});
