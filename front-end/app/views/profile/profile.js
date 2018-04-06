'use strict'

angular.module('koolikottApp')
.controller('profileController',
[
    '$scope', '$route', 'authenticatedUserService', 'serverCallService', '$location', 'alertService',
    function ($scope, $route, authenticatedUserService, serverCallService, $location, alertService) {

        function init() {
            if (!$scope.user) getUser();

            if ($route.current.params.username) {
                getUsersMaterials();
                getUsersPortfolios();
            }
        }

        function getUser() {
            const userParams = {
                'username': $route.current.params.username
            };
            serverCallService.makeGet("rest/user", userParams)
                .then((result)=>{
                    const {data} = result;
                    if (!isEmpty(data)) {
                        $scope.user = data;
                    } else {
                        getUserFail();
                    }
                }, ()=>{getUserFail()})
        }

        function getUserFail() {
            console.log('Getting user failed.');
            $location.url('/');
        }

        function getUsersMaterials() {
            serverCallService.makeGet("rest/material/getByCreator", params())
                .then((result)=>{
                    const {data} = result;
                    if (!isEmpty(data)) {
                        $scope.materials = data.items;
                    } else {
                        getUsersMaterialsFail();
                    }
                }, ()=>{getUsersMaterialsFail()});
        }

        function getUsersMaterialsFail() {
            console.log('Failed to get materials.');
        }

        function getUsersPortfolios() {
            serverCallService.makeGet("rest/portfolio/getByCreator", params())
                .then((result)=>{
                    const {data} = result;
                    if (!isEmpty(data)) {
                        $scope.portfolios = data.items;
                    } else {
                        getUsersPortfoliosFail();
                    }
                }, ()=>{
                    getUsersPortfoliosFail()
                });
        }

        function getUsersPortfoliosFail() {
            console.log('Failed to get portfolios.');
        }

        function params() {
            return {
                'username': $route.current.params.username,
                'maxResults': 1000
            };
        }

        init();
    }
]);
