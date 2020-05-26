import VueServerCallService from '../services/vueServerCall.service';

export default class VueCommons {
    static isPortfolio(type) {
        return type === '.Portfolio' || type === '.PortfolioLog' || type === '.ReducedPortfolio' || type === '.AdminPortfolio'
    }
    static isMaterial(type) {
        return type === '.Material' || type === '.ReducedMaterial' || type === '.AdminMaterial'
    }
    static getPortfolioById(id) {
        return VueServerCallService.makeGet('rest/portfolio?id=' + id)
            .then((response) => {
                if (response.data) return response.data;
            })
    }
    static getMaterialById(id) {
        return VueServerCallService.makeGet('rest/material?id=' + id)
            .then((response) => {
                if (response.data) return response.data;
            })
    }
}
