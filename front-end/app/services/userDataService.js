define([
    'angularAMD',
    'services/serverCallService',
    'services/authenticatedUserService'
], function (angularAMD) {
    var instance;

    var brokenMaterialsCallbacks = [];
    var brokenMaterialsCountCallbacks = [];

    var deletedMaterialsCallbacks = [];
    var deletedMaterialsCountCallbacks = [];

    var deletedPortfoliosCallbacks = [];
    var deletedPortfoliosCountCallbacks = [];

    var improperItemsCallbacks = [];

    var userFavoritesCallbacks = [];
    var userFavoritesCountCallbacks = [];

    var userMaterialsCallbacks = [];
    var userMaterialsCountCallbacks = [];

    var userPortfoliosCallbacks = [];
    var userPortfoliosCountCallbacks = [];

    angularAMD.factory('userDataService', ['serverCallService', 'authenticatedUserService',
        function (serverCallService, authenticatedUserService) {
            init();

            function init() {
                if (authenticatedUserService.isAdmin() && authenticatedUserService.isModerator()) {
                    serverCallService.makeGet("rest/material/getBroken", {}, getBrokenMaterialsSuccess, getItemsFail);
                    serverCallService.makeGet("/rest/material/getBroken?count=true", {}, getBrokenMaterialsCountSuccess, getItemsFail);

                    serverCallService.makeGet("rest/material/getDeleted", {}, getDeletedMaterialsSuccess, getItemsFail);
                    serverCallService.makeGet("rest/material/getDeleted?count=true", {}, getDeletedMaterialsCountSuccess, getItemsFail);

                    serverCallService.makeGet("rest/portfolio/getDeleted", {}, getDeletedPortfoliosSuccess, getItemsFail);
                    serverCallService.makeGet("rest/portfolio/getDeleted?count=true", {}, getDeletedPortfoliosCountSuccess, getItemsFail);

                    serverCallService.makeGet("rest/impropers", {}, getImproperItemsSuccess, getItemsFail);
                    //serverCallService.makeGet("rest/impropers?count=true", {}, getImproperItemsCountSuccess, getItemsFail);
                }
                if (authenticatedUserService.isAuthenticated()) {
                    var params = {
                        'username': authenticatedUserService.getUser().username
                    };
                    var usercountparam = "?username=" + authenticatedUserService.getUser().username + "&count=true";

                    serverCallService.makeGet("rest/learningObject/usersFavorite", {}, getFavoritesSuccess, getItemsFail);
                    serverCallService.makeGet("rest/learningObject/usersFavorite?count=true", {}, getFavoritesCountSuccess, getItemsFail);

                    serverCallService.makeGet("rest/material/getByCreator", params, getUsersMaterialsSuccess, getItemsFail);
                    serverCallService.makeGet("rest/material/getByCreator" + usercountparam, {}, getUsersMaterialsCountSuccess, getItemsFail);

                    serverCallService.makeGet("rest/portfolio/getByCreator", params, getUsersPortfoliosSuccess, getItemsFail);
                    serverCallService.makeGet("rest/portfolio/getByCreator" + usercountparam, {}, getUsersPortfoliosCountSuccess, getItemsFail);
                }
            }

            function getItemsFail() {
                console.log("Failed to get data");
            }

            // Broken materials
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
            function getBrokenMaterialsCountSuccess(data) {
                if (!isEmpty(data)) {
                    brokenMaterialsCountCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    brokenMaterialsCountCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("brokenMaterialsCount", string);
                }
            }

            // Deleted materials
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
            function getDeletedMaterialsCountSuccess(data) {
                if (!isEmpty(data)) {
                    deletedMaterialsCountCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    deletedMaterialsCountCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("deletedMaterialsCount", string);
                }
            }

            // Deleted portfolios
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
            function getDeletedPortfoliosCountSuccess(data) {
                if (!isEmpty(data)) {
                    deletedPortfoliosCountCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    deletedPortfoliosCountCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("deletedPortfoliosCount", string);
                }
            }

            // Improper items
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

            // User favorites
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
            function getFavoritesCountSuccess(data) {
                if (!isEmpty(data)) {
                    userFavoritesCountCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    userFavoritesCountCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("userFavoritesCount", string);
                }
            }

            // User materials
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
            function getUsersMaterialsCountSuccess(data) {
                if (!isEmpty(data)) {
                    userMaterialsCountCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    userMaterialsCountCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("userMaterialsCount", string);
                }
            }

            // User portfolios
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
            function getUsersPortfoliosCountSuccess(data) {
                if (!isEmpty(data)) {
                    userPortfoliosCountCallbacks.forEach(function(callback) {
                        callback(data);
                    });
                    userPortfoliosCountCallbacks = [];
                    var string = JSOG.stringify(data);
                    localStorage.setItem("userPortfoliosCount", string);
                }
            }



            instance = {
                // Broken materials
                loadBrokenMaterials: function(callback) {
                    var string = localStorage.getItem("brokenMaterials");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    brokenMaterialsCallbacks.push(callback);
                    serverCallService.makeGet("rest/material/getBroken", {}, getBrokenMaterialsSuccess, getItemsFail);
                },
                loadBrokenMaterialsCount: function(callback) {
                    var string = localStorage.getItem("brokenMaterialsCount");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    brokenMaterialsCountCallbacks.push(callback);
                    serverCallService.makeGet("/rest/material/getBroken?count=true", {}, getBrokenMaterialsCountSuccess, getItemsFail);
                },

                // Deleted materials
                loadDeletedMaterials: function(callback) {
                    var string = localStorage.getItem("deletedMaterials");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    deletedMaterialsCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/material/getDeleted", {}, getDeletedMaterialsSuccess, getItemsFail);
                },
                loadDeletedMaterialsCount: function(callback) {
                    var string = localStorage.getItem("deletedMaterialsCount");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    deletedMaterialsCallbacks.push(callback);
                    serverCallService.makeGet("rest/material/getDeleted?count=true", {}, getDeletedMaterialsCountSuccess, getItemsFail);
                },

                // Deleted portfolios
                loadDeletedPortfolios: function(callback) {
                    var string = localStorage.getItem("deletedPortfolios");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    deletedPortfoliosCallbacks.push(callback);
                    serverCallService.makeGet("rest/portfolio/getDeleted", {}, getDeletedPortfoliosSuccess, getItemsFail);
                },
                loadDeletedPortfoliosCount: function(callback) {
                    var string = localStorage.getItem("deletedPortfoliosCount");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    deletedPortfoliosCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/portfolio/getDeleted?count=true", {}, getDeletedPortfoliosCountSuccess, getItemsFail);
                },

                // Improper items
                loadImproperItems: function(callback) {
                    var string = localStorage.getItem("improperItems");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    improperItemsCallbacks.push(callback);
                    serverCallService.makeGet("rest/impropers", {}, getImproperItemsSuccess, getItemsFail);
                },

                // User favorites
                loadUserFavorites: function(callback) {
                    var string = localStorage.getItem("userFavorites");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    userFavoritesCallbacks.push(callback);
                    serverCallService.makeGet("rest/learningObject/usersFavorite", {}, getFavoritesSuccess, getItemsFail);
                },
                loadUserFavoritesCount: function(callback) {
                    var string = localStorage.getItem("userFavoritesCount");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    userFavoritesCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/learningObject/usersFavorite?count=true", {}, getFavoritesSuccess, getItemsFail);
                },

                // User materials
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
                loadUserMaterialsCount: function(callback) {
                    var usercountparam = "?username=" + authenticatedUserService.getUser().username + "&count=true";
                    var string = localStorage.getItem("userMaterialsCount");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    userFavoritesCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/material/getByCreator" + usercountparam, {}, getUsersMaterialsCountSuccess, getItemsFail);
                },

                // User portfolios
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
                loadUserPortfoliosCount: function(callback) {
                    var usercountparam = "?username=" + authenticatedUserService.getUser().username + "&count=true";
                    var string = localStorage.getItem("userPortfoliosCount");
                    data = JSOG.parse(string);
                    if (data) {
                        callback(data);
                    }
                    userPortfoliosCountCallbacks.push(callback);
                    serverCallService.makeGet("rest/portfolio/getByCreator" + usercountparam, {}, getUsersPortfoliosCountSuccess, getItemsFail);
                },

            };

            return instance;
        }
    ]);
});
