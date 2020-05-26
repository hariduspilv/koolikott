export default class VueAuthenticatedUserService {
    static setAuthenticatedUser(authenticatedUser) {
        localStorage.setItem('authenticatedUser', JSON.stringify(authenticatedUser))
    }
    static getAuthenticatedUser() {
        return JSON.parse(localStorage.getItem('authenticatedUser'))
    }
    static removeAuthenticatedUser() {
        localStorage.removeItem('authenticatedUser');
    }

    static isAuthenticated() {
        return !!this.getAuthenticatedUser()
    }
    static isAdmin() {
        return this.hasRole('ADMIN');
    }

    static isOwner(learningObject) {
        return !this.isAuthenticated() ?
            false : learningObject && learningObject.creator ?
                learningObject.creator.id === this.getUser().id : false
    }

    static isDefined(value) {
        return angular.isDefined(value) && value !== null;
    }

    static isPublisher() {
        const user = this.getUser()
        return user && this.isDefined(user.publisher)
    }
    static isRestricted() {
        return this.hasRole('RESTRICTED');
    }
    static isModerator() {
        return this.hasRole('MODERATOR');
    }
    static isUser(){
        return this.hasRole('USER');
    }
    static isUserOrModerator(){
        return this.isUser() || this.isModerator()
    }
    static isModeratorOrAdmin() {
        return this.isModerator() || this.isAdmin()
    }
    static isModeratorOrAdminOrCreator(learningObject) {
        if (this.isModeratorOrAdmin()){
            return true;
        }
        if (this.getUser() && learningObject && learningObject.creator) {
            return this.isUser() && this.getUser().id === learningObject.creator.id
        }
        return false;
    }
    static getUser() {
        const authenticatedUser = this.getAuthenticatedUser()
        return authenticatedUser ? authenticatedUser.user
            : null
    }
    static getToken() {
        const authenticatedUser = this.getAuthenticatedUser()
        return authenticatedUser ? authenticatedUser.token
            : null
    }

    static hasRole(role) {
        const user = this.getUser()
        return user && user.role === role
    }
}
