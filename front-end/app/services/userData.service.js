'use strict';

angular.module('koolikottApp')
.factory('userDataService',
[
    'serverCallService', 'authenticatedUserService',
    function (serverCallService, authenticatedUserService) {
        var instance;

        var deletedCountCallbacks = [];
        var improperCountCallbacks = [];
        var userFavoritesCountCallbacks = [];
        var userMaterialsCountCallbacks = [];
        var userPortfoliosCountCallbacks = [];
        var moderatorsCountCallbacks = [];
        var allUsersCountCallbacks = [];
        var restrictedUsersCountCallbacks = [];
        var changedLearningObjectCountCallbacks = [];
        var unReviewedLearningObjectCountCallbacks = [];
        var sentEmailsCountCallbacks = [];

        function getUsername() {
            if (authenticatedUserService.isAuthenticated()) return {'username': authenticatedUserService.getUser().username};
        }

        function getItemsFail() {
            console.log("Failed to get data");
        }

        function getDeletedCountSuccess(data) {
            if (!_.isNil(data)) {
                deletedCountCallbacks.forEach(function (callback) {
                    callback(data);
                });
                deletedCountCallbacks = [];
                localStorage.setItem("deletedCount", data);
            }
        }

        function getSentEmailsCountSuccess(data) {
            if (!_.isNil(data)) {
                sentEmailsCountCallbacks.forEach(function (callback) {
                    callback(data);
                });
                sentEmailsCountCallbacks = [];
                localStorage.setItem("sentEmailsCount", data);
            }
        }

        function getImproperCountSuccess(data) {
            if (!_.isNil(data)) {
                improperCountCallbacks.forEach(function (callback) {
                    callback(data);
                });
                improperCountCallbacks = [];
                localStorage.setItem("improperCount", data);
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

        function getAllUsersCountSuccess(data) {
            allUsersCountCallbacks.forEach(function (callback) {
                callback(data);
            });
            allUsersCountCallbacks = [];
            localStorage.setItem("allUsersCount", data);

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
            loadDeletedCount: function (callback) {
                var data = localStorage.getItem("deletedCount");
                if (data) {
                    callback(data);
                }
                deletedCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/deleted/count", {}, getDeletedCountSuccess, getItemsFail);

            },
            loadSentEmailsCount: function (callback) {
                var data = localStorage.getItem("sentEmailsCount");
                if (data) {
                    callback(data);
                }
                sentEmailsCountCallbacks.push(callback);
                serverCallService.makeGet("rest/userEmail/count", {}, getSentEmailsCountSuccess, getItemsFail);
            },
            loadImproperCount: function (callback) {
                var data = localStorage.getItem("improperCount");
                if (data) {
                    callback(data);
                }
                improperCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/improper/count", {}, getImproperCountSuccess, getItemsFail);
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
            loadAllUsersCount: function (callback) {
                var data = localStorage.getItem("allUsersCount");
                if (data) {
                    callback(data);
                }
                allUsersCountCallbacks.push(callback);
                serverCallService.makeGet("rest/user/allUsers/count", {}, getAllUsersCountSuccess, getItemsFail);
            },
            loadRestrictedUsersCount: function (callback) {
                var data = localStorage.getItem("restrictedUsersCount");
                if (data) {
                    callback(data);
                }
                restrictedUsersCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/restrictedUser/count", {}, getRestrictedUsersCountSuccess, getItemsFail);
            },
            loadChangesCount: function (callback) {
                var data = localStorage.getItem("changedLearningObject");
                if (data) {
                    callback(data);
                }
                changedLearningObjectCountCallbacks.push(callback);
                serverCallService.makeGet("rest/admin/changed/count", {}, getChangedLearningObjectCountSuccess, getItemsFail);
            },
            loadUnReviewedLearningObjectsCount: function (callback) {
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
