'use strict'

angular.module('koolikottApp')
.controller('userPortfoliosController',
[
    '$scope', '$route', 'authenticatedUserService', '$translate', 'serverCallService', 'searchService',
    function ($scope, $route, authenticatedUserService, $translate, serverCallService, searchService) {
        function init() {
            searchService.setQuery(null)
            $scope.cache = false;
            $scope.url = "rest/portfolio/getByCreator";
            $scope.params = {
                'maxResults': 20,
                'username': $route.current.params.username
            };

            setTitle();
        }

        function emptyTitle() {
            $translate('PROFILE_PAGE_TITLE_PORTFOLIOS').then((value) => {
                $scope.title = (value.replace('${user}', ''));
            })
        }

        function setTitle() {
            const user = authenticatedUserService.getUser();
            if (user && $route.current.params.username === user.username) {
                $scope.title = 'MYPROFILE_PAGE_TITLE_PORTFOLIOS';
            } else {
                serverCallService.makeGet("rest/user", {'username': $route.current.params.username})
                .then(({data: user}) => {
                    if (user){
                        $translate('PROFILE_PAGE_TITLE_PORTFOLIOS').then((value) =>{
                            $scope.title = value.replace('${user}', `${user.name} ${user.surname}`);
                        })
                    } else {
                        emptyTitle();
                    }
                }, ()=>{
                    emptyTitle();
                })
            }
        }

        init();
    }
]);
