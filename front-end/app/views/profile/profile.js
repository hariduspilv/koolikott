'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            this.getUserNameAndLearningObjectsCount();

            this.$scope.cache = false;
            this.$scope.url = 'rest/learningObject/getByCreatorAllLearningObjects';
            this.$scope.params = {
                'username': this.$route.current.params.username,
                'maxResults': 20
            };
            this.$rootScope.userView = true
            this.$rootScope.$on('$locationChangeSuccess', () => {
                this.$rootScope.userView = false;
            })
        }

        getUserNameAndLearningObjectsCount() {
            const userParams = {
                'username': this.$route.current.params.username
            };
            this.serverCallService.makeGet('rest/learningObject/getByCreatorAllLearningObjectsCount', userParams)
                .then((value) => {
                    this.$scope.count = value.data;
                    this.getUser(userParams);
                });
        }

        getUser(userParams) {
            this.serverCallService.makeGet("rest/user", userParams)
                .then(({data: user}) => {
                    if (!isEmpty(user)) {
                        this.$scope.user = user;
                        this.$translate('PROFILE_PAGE_TITLE_LEARNINGOBJECTS')
                            .then((value) => {
                                this.$scope.title = value
                                    .replace('${user}', `${user.name} ${user.surname}`)
                                    .replace('${count}', this.$scope.count);
                            })
                    } else {
                        this.getUserFail();
                    }
                }, () => {
                    this.getUserFail()
                })
        }

        getUserFail() {
            console.log('Getting user failed.');
        }
    }

    controller.$inject = [
        '$rootScope',
        '$scope',
        '$route',
        'authenticatedUserService',
        'serverCallService',
        '$location',
        '$translate',
        'searchService'
    ]
    angular.module('koolikottApp').controller('profileController', controller)
}
