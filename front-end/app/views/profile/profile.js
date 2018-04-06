'use strict'

{
class controller extends Controller {
    constructor(...args){
        super(...args)
        if (!this.$scope.user) this.getUser();

        if (this.$route.current.params.username) {
            this.getUsersMaterials();
            this.getUsersPortfolios();
        }
    }

    getUser() {
        const userParams = {
            'username': this.$route.current.params.username
        };
        this.serverCallService.makeGet("rest/user", userParams)
            .then(({data}) => {
                if (!isEmpty(data)) {
                    this.$scope.user = data;
                } else {
                    this.getUserFail();
                }
            }, () => {
                this.getUserFail()
            })
    }

    getUserFail() {
        console.log('Getting user failed.');
        this.$location.url('/');
    }

    getUsersMaterials() {
        this.serverCallService.makeGet("rest/material/getByCreator", this.params())
            .then(({data}) => {
                if (!isEmpty(data)) {
                    this.$scope.materials = data.items;
                } else {
                    this.getUsersMaterialsFail();
                }
            }, () => {
                this.getUsersMaterialsFail()
            });
    }

    getUsersMaterialsFail() {
        console.log('Failed to get materials.');
    }

    getUsersPortfolios() {
        this.serverCallService.makeGet("rest/portfolio/getByCreator", this.params())
            .then(({data}) => {
                if (!isEmpty(data)) {
                    this.$scope.portfolios = data.items;
                } else {
                    this.getUsersPortfoliosFail();
                }
            }, () => {
                this.getUsersPortfoliosFail()
            });
    }

    getUsersPortfoliosFail() {
        console.log('Failed to get portfolios.');
    }

    params() {
        return {
            'username': this.$route.current.params.username,
            'maxResults': 1000
        };
    }
}

controller.$inject = [
    '$scope',
    '$route',
    'authenticatedUserService',
    'serverCallService',
    '$location'
]
angular.module('koolikottApp').controller('profileController', controller)
}
