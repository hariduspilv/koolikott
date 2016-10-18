define([
    'angularAMD',
    'services/serverCallService',
    'services/authenticatedUserService'
], function (angularAMD) {
    var instance;

    var IMPROPER_ITEMS;

    var brokenMaterialsCountCallbacks = [];
    var deletedMaterialsCountCallbacks = [];
    var deletedPortfoliosCountCallbacks = [];
    var improperItemsCallbacks = [];
    var userFavoritesCountCallbacks = [];
    var userMaterialsCountCallbacks = [];
    var userPortfoliosCountCallbacks = [];

    angularAMD.factory('userDataService', ['serverCallService', 'authenticatedUserService',
        function (serverCallService, authenticatedUserService) {
            init();

            function init() {
                if (authenticatedUserService.isAdmin() || authenticatedUserService.isModerator()) {
                    serverCallService.makeGet("rest/material/getBroken/count", {}, getBrokenMaterialsCountSuccess, getItemsFail);
                    serverCallService.makeGet("rest/material/getDeleted/count", {}, getDeletedMaterialsCountSuccess, getItemsFail);
                    serverCallService.makeGet("rest/portfolio/getDeleted/count", {}, getDeletedPortfoliosCountSuccess, getItemsFail);
                    serverCallService.makeGet("rest/impropers", {}, getImproperItemsSuccess, getItemsFail);
                }
                if (authenticatedUserService.isAuthenticated()) {
                    var params = {
                        'username': authenticatedUserService.getUser().username
                    };

                    serverCallService.makeGet("rest/learningObject/usersFavorite/count", {}, getFavoritesCountSuccess, getItemsFail);
                    serverCallService.makeGet("rest/material/getByCreator/count", params, getUsersMaterialsCountSuccess, getItemsFail);
                    serverCallService.makeGet("rest/portfolio/getByCreator/count", params, getUsersPortfoliosCountSuccess, getItemsFail);
                }
            }

            function getItemsFail() {
                console.log("Failed to get data");
            }


            function getBrokenMaterialsCountSuccess(data) {
                if (!isEmpty(data)) {
                    brokenMaterialsCountCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    brokenMaterialsCountCallbacks = [];
                    localStorage.setItem("brokenMaterialsCount", data);
                }
            }
            function getDeletedMaterialsCountSuccess(data) {
                if (!isEmpty(data)) {
                    deletedMaterialsCountCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    deletedMaterialsCountCallbacks = [];
                    localStorage.setItem("deletedMaterialsCount", data);
                }
            }
            function getDeletedPortfoliosCountSuccess(data) {
                if (!isEmpty(data)) {
                    deletedPortfoliosCountCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    deletedPortfoliosCountCallbacks = [];
                    localStorage.setItem("deletedPortfoliosCount", data);
                }
            }
            // Improper items
            function getImproperItemsSuccess(data) {
                if (!isEmpty(data)) {
                    IMPROPER_ITEMS = data;
                    improperItemsCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                }
            }
            function getFavoritesCountSuccess(data) {
                if (!isEmpty(data)) {
                    userFavoritesCountCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    userFavoritesCountCallbacks = [];
                    localStorage.setItem("userFavoritesCount", data);
                }
            }
            function getUsersMaterialsCountSuccess(data) {
                if (!isEmpty(data)) {
                    userMaterialsCountCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    userMaterialsCountCallbacks = [];
                    localStorage.setItem("userMaterialsCount", data);
                }
            }
            function getUsersPortfoliosCountSuccess(data) {
                if (!isEmpty(data)) {
                    userPortfoliosCountCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    userPortfoliosCountCallbacks = [];
                    localStorage.setItem("userPortfoliosCount", data);
                }
            }



            instance = {
                loadBrokenMaterialsCount: function(callback) {
                    var data = localStorage.getItem("brokenMaterialsCount");
                    if (data) {
                        callback(data);
                    }
                    brokenMaterialsCountCallbacks.push(callback);
                },
                loadDeletedMaterialsCount: function(callback) {
                    var data = localStorage.getItem("deletedMaterialsCount");
                    if (data) {
                        callback(data);
                    }
                    deletedMaterialsCountCallbacks.push(callback);
                },
                loadDeletedPortfoliosCount: function(callback) {
                    var data = localStorage.getItem("deletedPortfoliosCount");
                    if (data) {
                        callback(data);
                    }
                    deletedPortfoliosCountCallbacks.push(callback);
                },

                // Improper items
                loadImproperItems: function(callback) {
                    if (IMPROPER_ITEMS) {
                        callback(IMPROPER_ITEMS);
                    } else {
                        improperItemsCallbacks.push(callback);
                    }
                },
                loadUserFavoritesCount: function(callback) {
                    var data = localStorage.getItem("userFavoritesCount");
                    if (data) {
                        callback(data);
                    }
                    userFavoritesCountCallbacks.push(callback);
                },
                loadUserMaterialsCount: function(callback) {
                    var data = localStorage.getItem("userMaterialsCount");
                    if (data) {
                        callback(data);
                    }
                    userFavoritesCountCallbacks.push(callback);
                },
                loadUserPortfoliosCount: function(callback) {
                    var data = localStorage.getItem("userPortfoliosCount");
                    if (data) {
                        callback(data);
                    }
                    userPortfoliosCountCallbacks.push(callback);
                },

            };

            return instance;
        }
    ]);
});
