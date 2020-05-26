import VueAuthenticatedUserService from './vueAuthenticatedUser.service';
import axios from 'axios'

export default class VueServerCallService {

    static createAuthHeaders() {
        const user = VueAuthenticatedUserService.getUser();
        return  {
            Authentication: VueAuthenticatedUserService.getToken(),
            Username: user.username
        };
    }

    static makeGet(url) {
        return axios.get(url, {headers: this.createAuthHeaders()})
    }

    static makePost(url, data) {
        return axios.post(url, data, {headers: this.createAuthHeaders()})
    }

    static makeDelete(url) {
        return axios.delete(url, {headers: this.createAuthHeaders()})
    }

}
