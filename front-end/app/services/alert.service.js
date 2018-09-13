'use strict'

factory('alertService', class {
    constructor() {
        this.alert = {}
    }
    clearMessage() {
        this.alert = {}
    }
    getAlert(message) {
        return this.alert
    }
    setErrorAlert(message) {
        this.alert.message = message
        this.alert.type = 'alert-danger'
    }
})
