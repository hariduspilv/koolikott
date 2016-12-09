define(['angularAMD'], function (angularAMD) {
    var instance;

    angularAMD.factory('authenticatedUserService', ['$location',
        function ($location) {

            instance = {
                setAuthenticatedUser: function (authenticatedUser) {
                    // HACK: Handling stack overflow caused by taxons
                    authenticatedUser.user.userTaxons = instance.getFirstLevelTaxons(authenticatedUser.user.userTaxons);

                    localStorage.setItem("authenticatedUser", JSON.stringify(authenticatedUser));
                },

                getAuthenticatedUser: function () {
                    return JSON.parse(localStorage.getItem("authenticatedUser"));
                },

                removeAuthenticatedUser: function () {
                    localStorage.removeItem("authenticatedUser");
                },

                isAuthenticated: function () {
                    return !!instance.getAuthenticatedUser();
                },

                isAdmin: function () {
                    var user = instance.getUser();
                    return user && user.role === 'ADMIN';
                },

                isPublisher: function () {
                    var user = instance.getUser();
                    return user && isDefined(user.publisher);
                },

                isRestricted: function () {
                    var user = instance.getUser();
                    return user && user.role === 'RESTRICTED';
                },

                isModerator: function () {
                    var user = instance.getUser();
                    return user && user.role === 'MODERATOR';
                },

                getUser: function () {
                    var authenticatedUser = instance.getAuthenticatedUser();
                    if (authenticatedUser) {
                        return authenticatedUser.user;
                    }

                    return null;
                },

                getToken: function () {
                    var authenticatedUser = instance.getAuthenticatedUser();
                    if (authenticatedUser) {
                        return authenticatedUser.token;
                    }

                    return null;
                },

                getFirstLevelTaxons: function (userTaxons) {
                    var taxonList = [];
                    userTaxons.forEach(function (entry) {
                        var taxon = {'id': entry.id};
                        taxonList.push(taxon);
                    });

                    return taxonList;
                }
            };

            return instance;
        }
    ]);
});
