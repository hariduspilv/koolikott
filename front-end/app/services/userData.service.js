'use strict';

angular.module('koolikottApp')
.factory('userDataService',
[
    'serverCallService', 'authenticatedUserService',
    function (serverCallService, authenticatedUserService) {
        var instance;

        var brokenMaterialsCountCallbacks = [];
        var deletedMaterialsCountCallbacks = [];
        var deletedPortfoliosCountCallbacks = [];
        var improperMaterialsCountCallbacks = [];
        var improperPortfoliosCountCallbacks = [];
        var userFavoritesCountCallbacks = [];
        var userMaterialsCountCallbacks = [];
        var userPortfoliosCountCallbacks = [];
        var moderatorsCountCallbacks = [];
        var restrictedUsersCountCallbacks = [];
        var changedLearningObjectCountCallbacks = [];
        var unReviewedLearningObjectCountCallbacks = [];

        function getUsername() {
            if (authenticatedUserService.isAuthenticated()) return {'username': authenticatedUserService.getUser().username};
        }

        function getItemsFail() {
            console.log("Failed to get data");
        }

        function getBrokenMaterialsCountSuccess(data) {
            if (!_.isNil(data)) {
                brokenMaterialsCountCallbacks.forEach(function (callback) {
                    callback(data);
                });
                brokenMaterialsCountCallbacks = [];
                localStorage.setItem("brokenMaterialsCount", data);
            }
        }

        function getDeletedMaterialsCountSuccess(data) {
            if (!_.isNil(data)) {
                deletedMaterialsCountCallbacks.forEach(function (callback) {
                    callback(data);
                });
                deletedMaterialsCountCallbacks = [];
                localStorage.setItem("deletedMaterialsCount", data);
            }
        }

        function getDeletedPortfoliosCountSuccess(data) {
            if (!_.isNil(data)) {
                deletedPortfoliosCountCallbacks.forEach(function (callback) {
                    callback(data);
                });
                deletedPortfoliosCountCallbacks = [];
                localStorage.setItem("deletedPortfoliosCount", data);
            }
        }

        function getImproperMaterialsCountSuccess(data) {
            if (!_.isNil(data)) {
                improperMaterialsCountCallbacks.forEach(function (callback) {
                    callback(data);
                });
                improperMaterialsCountCallbacks = [];
                localStorage.setItem("improperMaterialsCount", data);
            }
        }

        function getImproperPortfoliosCountSuccess(data) {
            if (!_.isNil(data)) {
                improperPortfoliosCountCallbacks.forEach(function (callback) {
                    callback(data);
                });
                improperPortfoliosCountCallbacks = [];
                localStorage.setItem("improperPortfoliosCount", data);
            }
        }

        function getFavoritesCountSuccess(data) {
            userFavoritesCountCallbacks.forEach(function (callback) {
                callback(data);
            });
            userFavoritesCountCallbacks = [];
            localStorage.setItem("userFavoritesCount", data);

        }

        function getUsersMaterialsCountSuccess(data) {
            userMaterialsCountCallbacks.forEach(function (callback) {
                callback(data);
            });
            userMaterialsCountCallbacks = [];
            localStorage.setItem("userMaterialsCount", data);

        }

        function getUsersPortfoliosCountSuccess(data) {
            userPortfoliosCountCallbacks.forEach(function (callback) {
                callback(data);
            });
            userPortfoliosCountCallbacks = [];
            localStorage.setItem("userPortfoliosCount", data);

        }

        function getModeratorsCountSuccess(data) {
            moderatorsCountCallbacks.forEach(function (callback) {
                callback(data);
            });
            moderatorsCountCallbacks = [];
            localStorage.setItem("moderatorsCount", data);

        }

        function getRestrictedUsersCountSuccess(data) {
            restrictedUsersCountCallbacks.forEach(function (callback) {
                callback(data);
            });
            restrictedUsersCountCallbacks = [];
            localStorage.setItem("restrictedUsersCount", data);

        }

        function getChangedLearningObjectCountSuccess(data) {
            changedLearningObjectCountCallbacks.forEach(function (callback) {
                callback(data);
            });
            changedLearningObjectCountCallbacks = [];
            localStorage.setItem("changedLearningObject", data);

        }

        function getUnReviewedLearningObjectCountSuccess(data) {
            unReviewedLearningObjectCountCallbacks.forEach(function (callback) {
                callback(data);
            });
            unReviewedLearningObjectCountCallbacks = [];
            localStorage.setItem("unReviewedLearningObject", data);
        }

        instance = {
            loadBrokenMaterialsCount: function (callback) {
                var data = localStorage.getItem("brokenMaterialsCount");
                if (data) {
                    callback(data);
                }
                brokenMaterialsCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/brokenContent/getBroken/count", {}, getBrokenMaterialsCountSuccess, getItemsFail);
            },
            loadDeletedMaterialsCount: function (callback) {
                var data = localStorage.getItem("deletedMaterialsCount");
                if (data) {
                    callback(data);
                }
                deletedMaterialsCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/deleted/material/getDeleted/count", {}, getDeletedMaterialsCountSuccess, getItemsFail);

            },
            loadDeletedPortfoliosCount: function (callback) {
                var data = localStorage.getItem("deletedPortfoliosCount");
                if (data) {
                    callback(data);
                }
                deletedPortfoliosCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/deleted/portfolio/getDeleted/count", {}, getDeletedPortfoliosCountSuccess, getItemsFail);
            },

            loadImproperMaterialsCount: function (callback) {
                var data = localStorage.getItem("improperMaterialsCount");
                if (data) {
                    callback(data);
                }
                improperMaterialsCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/improper/material/count", {}, getImproperMaterialsCountSuccess, getItemsFail);
            },

            loadImproperPortfoliosCount: function (callback) {
                var data = localStorage.getItem("improperPortfoliosCount");
                if (data) {
                    callback(data);
                }
                improperPortfoliosCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/improper/portfolio/count", {}, getImproperPortfoliosCountSuccess, getItemsFail);
            },

            loadUserFavoritesCount: function (callback) {
                var data = localStorage.getItem("userFavoritesCount");
                if (data) {
                    callback(data);
                }
                userFavoritesCountCallbacks.push(callback);
                serverCallService.makeGet("rest/learningObject/usersFavorite/count", {}, getFavoritesCountSuccess, getItemsFail);
            },
            loadUserMaterialsCount: function (callback) {
                var data = localStorage.getItem("userMaterialsCount");
                if (data) {
                    callback(data);
                }
                userMaterialsCountCallbacks.push(callback);
                serverCallService.makeGet("rest/material/getByCreator/count", getUsername(), getUsersMaterialsCountSuccess, getItemsFail);
            },
            loadUserPortfoliosCount: function (callback) {
                var data = localStorage.getItem("userPortfoliosCount");
                if (data) {
                    callback(data);
                }
                userPortfoliosCountCallbacks.push(callback);
                serverCallService.makeGet("rest/portfolio/getByCreator/count", getUsername(), getUsersPortfoliosCountSuccess, getItemsFail);
            },
            loadModeratorsCount: function (callback) {
                var data = localStorage.getItem("moderatorsCount");
                if (data) {
                    callback(data);
                }
                moderatorsCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/moderator/count", {}, getModeratorsCountSuccess, getItemsFail);
            },
            loadRestrictedUsersCount: function (callback) {
                var data = localStorage.getItem("restrictedUsersCount");
                if (data) {
                    callback(data);
                }
                restrictedUsersCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/restrictedUser/count", {}, getRestrictedUsersCountSuccess, getItemsFail);
            },
            loadChangedLearningObjectCount: function (callback) {
                var data = localStorage.getItem("changedLearningObject");
                if (data) {
                    callback(data);
                }
                changedLearningObjectCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/changed/count", {}, getChangedLearningObjectCountSuccess, getItemsFail);
            },
            loadUnReviewedLearningObjectCount: function (callback) {
                var data = localStorage.getItem("unReviewedLearningObject");
                if (data) {
                    callback(data);
                }
                unReviewedLearningObjectCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/firstReview/unReviewed/count", {}, getUnReviewedLearningObjectCountSuccess, getItemsFail);
            }
        };

        return instance;
    }
]);
