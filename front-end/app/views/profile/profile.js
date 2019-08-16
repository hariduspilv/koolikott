'use strict'

{
    class controller extends Controller {
        constructor(...args) {
            super(...args)
            if (!this.$scope.user) this.getUser();

            this.$scope.cache = false;
            this.$scope.url = 'rest/learningObject/getByCreatorAllLearningObjects';
            this.$scope.params = {
                'username': this.$route.current.params.username,
                'maxResults': 20
            };
        }

    getUser() {
        const userParams = {
            'username': this.$route.current.params.username
        };
        this.serverCallService.makeGet("rest/user", userParams)
            .then(({data: user}) => {
                if (!isEmpty(user)) {
                    this.$scope.user = user;
                    this.$translate('PROFILE_PAGE_TITLE_PORTFOLIOS').then((value) => {
                        this.$scope.title = value.replace('${user}', `${user.name} ${user.surname}`);
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
