define(['app'], function(app) {
    app.factory('iconService', 
        function() {
            var getMaterialIcon = function(resourceTypes) {
                if (resourceTypes == undefined || resourceTypes == null) return '';
                if (resourceTypes.length == 0) return 'description';

                for (var i = 0; i < resourceTypes.length; i++) {
                    switch(resourceTypes[i].name.trim()) {
                        case 'AUDIO':
                            return 'audiotrack';
                        case 'IMAGE':
                            return 'image';
                        case 'VIDEO':
                        case 'BROADCAST':
                            return 'ondemand_video';
                        case 'DEMONSTRATION':
                        case 'PRESENTATION':
                            return 'dvr';
                        case 'ASSESSMENT':
                        case 'DRILLANDPRACTICE':
                            return 'assessment';
                        case 'TOOL':
                        case 'APPLICATION':
                            return 'web_asset';
                        case 'EDUCATIONALGAME':
                        case 'ROLEPLAY':
                        case 'SIMULATION':
                            return 'casino';
                        case 'CASESTUDY':
                        case 'ENQUIRYORIENTEDACTIVITY':
                        case 'EXPERIMENT':
                        case 'EXPLORATION':
                        case 'OPENACTIVITY':
                        case 'PROJECT':
                            return 'assignment';
                    }
                }

                return 'description';
            };
          
            return {
                getMaterialIcon: getMaterialIcon
            };
        }
    );
    
    return app;
});
