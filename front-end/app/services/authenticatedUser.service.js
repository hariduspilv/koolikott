'use strict'

{
class controller {
    setAuthenticatedUser(authenticatedUser) {
        localStorage.setItem('authenticatedUser', JSON.stringify(authenticatedUser))
    }
    getAuthenticatedUser() {
        return JSON.parse(localStorage.getItem('authenticatedUser'))
    }
    removeAuthenticatedUser() {
        localStorage.removeItem('authenticatedUser')
    }
    isAuthenticated() {
        return !!this.getAuthenticatedUser()
    }
    isAdmin() {
        const user = this.getUser()
        return user && user.role === 'ADMIN'
    }
    isPublisher() {
        const user = this.getUser()
        return user && isDefined(user.publisher)
    }
    isRestricted() {
        const user = this.getUser()
        return user && user.role === 'RESTRICTED'
    }
    isModerator() {
        const user = this.getUser()
        return user && user.role === 'MODERATOR'
    }
    getUser() {
        const authenticatedUser = this.getAuthenticatedUser()
        return authenticatedUser
            ? authenticatedUser.user
            : null
    }
    getToken() {
        const authenticatedUser = this.getAuthenticatedUser()
        return authenticatedUser
            ? authenticatedUser.token
            : null
    }
}

factory('authenticatedUserService', controller)
}
