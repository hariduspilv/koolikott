'use strict';

angular.module('koolikottApp')
    .factory('youtubeService', ['serverCallService', 'YOUTUBE_API_KEY', youtubeService]);

function youtubeService(serverCallService, YOUTUBE_API_KEY) {

    function getYoutubeData(url) {
        const queryUrl = `https://www.googleapis.com/youtube/v3/videos?id=${getVideoId(url)}&key=${YOUTUBE_API_KEY}%20&part=snippet,status`;
        return serverCallService.makeGet(queryUrl, {})
            .then((response) => {
                return getVideoDetailsFromResponse(response.data);
            });
    }

    function getVideoDetailsFromResponse(data) {
        if (data && data.items && !_.isEmpty(data.items)) {
            return data.items[0];
        } else {
            return null;
        }
    }

    function getVideoId(url) {
        let regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
        let match = url.match(regExp);
        return (match&&match[7].length==11)? match[7] : false;
    }

    return {
        getYoutubeData: getYoutubeData
    }
}
