'use strict'

{
class controller {
    getMaterialIcon(resourceTypes) {
        if (!Array.isArray(resourceTypes) || !resourceTypes.length)
            return 'description'

        for (let { name } of resourceTypes)
            switch(name.trim()) {
                case 'AUDIO':
                    return 'audiotrack'
                case 'IMAGE':
                    return 'image'
                case 'VIDEO':
                case 'BROADCAST':
                    return 'ondemand_video'
                case 'DEMONSTRATION':
                case 'PRESENTATION':
                    return 'dvr'
                case 'ASSESSMENT':
                case 'DRILLANDPRACTICE':
                    return 'assessment'
                case 'TOOL':
                case 'APPLICATION':
                    return 'web_asset'
                case 'EDUCATIONALGAME':
                case 'ROLEPLAY':
                case 'SIMULATION':
                    return 'casino'
                case 'CASESTUDY':
                case 'ENQUIRYORIENTEDACTIVITY':
                case 'EXPERIMENT':
                case 'EXPLORATION':
                case 'OPENACTIVITY':
                case 'PROJECT':
                    return 'assignment'
                case 'WEBSITE':
                case 'WEBLOG':
                case 'WIKI':
                case 'GLOSSARY':
                case 'REFERENCE':
                case 'BOOKMARKSHARINGPLATFORM':
                case 'IMAGESHARINGPLATFORM':
                case 'REFERENCESHARINGPLATFORM':
                case 'SOUNDSHARINGPLATFORM':
                case 'VIDEOSHARINGPLATFORM':
                case 'OTHER':
                case 'DATA':
                    return 'web'
                default:
                    return 'description'
            }
    }
}
factory('iconService', controller)
}
