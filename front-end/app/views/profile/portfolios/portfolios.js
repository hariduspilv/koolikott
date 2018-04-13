'use strict'

angular.module('koolikottApp')
.controller('userPortfoliosController',
[
    '$scope', '$route', 'authenticatedUserService', '$translate', 'serverCallService',
    function ($scope, $route, authenticatedUserService, $translate, serverCallService) {
        function init() {
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
                const userParams = {
                    'username': $route.current.params.username
                };
                serverCallService.makeGet("rest/user", userParams)
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
