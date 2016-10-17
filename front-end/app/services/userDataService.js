define([
    'angularAMD',
    'services/serverCallService',
    'services/authenticatedUserService'
], function (angularAMD) {
    var instance;

    var brokenMaterialsCallbacks = [];
    var deletedMaterialsCallbacks = [];
    var deletedPortfoliosCallbacks = [];
    var improperItemsCallbacks = [];
    var userFavoritesCallbacks = [];
    var userMaterialsCallbacks = [];
    var userPortfoliosCallbacks = [];

    angularAMD.factory('userDataService', ['serverCallService', 'authenticatedUserService',
        function (serverCallService, authenticatedUserService) {
            init();

            function init() {
                if (authenticatedUserService.isAdmin() && authenticatedUserService.isModerator()) {
                    serverCallService.makeGet("rest/material/getBroken", {}, getBrokenMaterialsSuccess, getItemsFail);
                    serverCallService.makeGet("rest/material/getDeleted", {}, getDeletedMaterialsSuccess, getItemsFail);
                    serverCallService.makeGet("rest/portfolio/getDeleted", {}, getDeletedPortfoliosSuccess, getItemsFail);
                    serverCallService.makeGet("rest/impropers", {}, getImproperItemsSuccess, getItemsFail);
                }
                if (authenticatedUserService.isAuthenticated()) {
                    var params = {
                        'username': authenticatedUserService.getUser().username
                    };
                    serverCallService.makeGet("rest/learningObject/usersFavorite", {}, getFavoritesSuccess, getItemsFail);
                    serverCallService.makeGet("rest/material/getByCreator", params, getUsersMaterialsSuccess, getItemsFail);
                    serverCallService.makeGet("rest/portfolio/getByCreator", params, getUsersPortfoliosSuccess, getItemsFail);
                }
            }

            function getItemsFail() {
                console.log("Failed to get data");
            }

            function getBrokenMaterialsSuccess(data) {
                if (!isEmpty(data)) {
                    brokenMaterialsCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    brokenMaterialsCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("brokenMaterials", string);
                }
            }
            function getDeletedMaterialsSuccess(data) {
                if (!isEmpty(data)) {
                    deletedMaterialsCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    deletedMaterialsCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("deletedMaterials", string);
                }
            }
            function getDeletedPortfoliosSuccess(data) {
                if (!isEmpty(data)) {
                    deletedPortfoliosCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    deletedPortfoliosCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("deletedPortfolios", string);
                }
            }
            function getImproperItemsSuccess(data) {
                if (!isEmpty(data)) {
                    improperItemsCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    improperItemsCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("improperItems", string);
                }
            }
            function getFavoritesSuccess(data) {
                if (!isEmpty(data)) {
                    userFavoritesCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    userFavoritesCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("userFavorites", string);
                }
            }
            function getUsersMaterialsSuccess(data) {
                if (!isEmpty(data)) {
                    userMaterialsCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    userMaterialsCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("userMaterials", string);
                }
            }
            function getUsersPortfoliosSuccess(data) {
                if (!isEmpty(data)) {
                    userPortfoliosCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    userPortfoliosCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("userPortfolios", string);
                }
            }



            instance = {
                loadBrokenMaterials: function(callback) {
                    var string = localStorage.getItem("brokenMaterials");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    brokenMaterialsCallbacks.push(callback);
                    serverCallService.makeGet("rest/material/getBroken", {}, getBrokenMaterialsSuccess, getItemsFail);
                },
                loadDeletedMaterials: function(callback) {
                    var string = localStorage.getItem("deletedMaterials");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    deletedMaterialsCallbacks.push(callback);
                    serverCallService.makeGet("rest/material/getDeleted", {}, getDeletedMaterialsSuccess, getItemsFail);
                },
                loadDeletedPortfolios: function(callback) {
                    var string = localStorage.getItem("deletedPortfolios");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    deletedPortfoliosCallbacks.push(callback);
                    serverCallService.makeGet("rest/portfolio/getDeleted", {}, getDeletedPortfoliosSuccess, getItemsFail);
                },
                loadImproperItems: function(callback) {
                    var string = localStorage.getItem("improperItems");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    improperItemsCallbacks.push(callback);
                    serverCallService.makeGet("rest/impropers", {}, getImproperItemsSuccess, getItemsFail);
                },
                loadUserFavorites: function(callback) {
                    var string = localStorage.getItem("userFavorites");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    userFavoritesCallbacks.push(callback);
                    serverCallService.makeGet("rest/learningObject/usersFavorite", {}, getFavoritesSuccess, getItemsFail);
                },
                loadUserMaterials: function(callback) {
                    var params = {
                        'username': authenticatedUserService.getUser().username
                    };

                    var string = localStorage.getItem("userMaterials");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    userFavoritesCallbacks.push(callback);
                    serverCallService.makeGet("rest/material/getByCreator", params, getUsersMaterialsSuccess, getItemsFail);
                },
                loadUserPortfolios: function(callback) {
                    var params = {
                        'username': authenticatedUserService.getUser().username
                    };

                    var string = localStorage.getItem("userPortfolios");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    userPortfoliosCallbacks.push(callback);
                    serverCallService.makeGet("rest/portfolio/getByCreator", params, getUsersPortfoliosSuccess, getItemsFail);
                },

            };

            return instance;
        }
    ]);
});
