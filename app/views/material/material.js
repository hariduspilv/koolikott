define(['app'], function(app)
{
    app.controller('materialController', ['$scope', 'serverCallService', '$route', 'translationService', '$rootScope', 'searchService', '$location', 'alertService', 'authenticatedUserService',
      function($scope, serverCallService, $route, translationService, $rootScope, searchService, $location, alertService, authenticatedUserService) {
          $scope.showMaterialContent = false;
          $scope.newComment = {};
  
          $scope.absUrl = $location.absUrl();
    
          $rootScope.$on('fullscreenchange', function() {
              $scope.$apply(function() {
                  $scope.showMaterialContent = !$scope.showMaterialContent;
              });
          });
  
          if ($rootScope.savedMaterial){
              $scope.material = $rootScope.savedMaterial;
              init();
          } else {
              getMaterial(getMaterialSuccess, getMaterialFail);
          }
          
          function getMaterial(success, fail) {
              var materialId = $route.current.params.materialId;
              var params = {
                  'materialId' : materialId
              };
              serverCallService.makeGet("rest/material", params, success, fail);
          }
  
          function getMaterialSuccess(material) {
              if (isEmpty(material)) {
                log('No data returned by getting material. Redirecting to landing page');
                  alertService.setErrorAlert('ERROR_MATERIAL_NOT_FOUND');
                  $location.url("/");
              } else {
                  $scope.material = material;
                  init();
              }
          }
  
          function getMaterialFail(material, status) {
                log('Getting materials failed. Redirecting to landing page');
                alertService.setErrorAlert('ERROR_MATERIAL_NOT_FOUND');
                $location.url("/");
          }
  
          function init() {
                setSourceType();
    
                if ($scope.material.taxons) {
                  preprocessMaterialSubjects();
                  preprocessMaterialEducationalContexts();
                }
    
                if($scope.material.embeddable && $scope.sourceType === 'LINK') {
                    if (authenticatedUserService.isAuthenticated()) {
                        getSignedUserData()
                    } else {
                        $scope.material.iframeSource = $scope.material.source;
                    }
                }
    
                var params = {
                    'type' : '.Material',
                    'id': $scope.material.id
                };
                serverCallService.makePost("rest/material/increaseViewCount", params, countViewSuccess, countViewFail);
          }
  
          function preprocessMaterialSubjects() {
              $scope.material.subjects = [];
      
              for (var i = 0, j = 0; i < $scope.material.taxons.length; i++) {
                  var taxon = $scope.material.taxons[i];
                  var subject = $rootScope.taxonUtils.getSubject(taxon);
        
                  if (subject) {
                    $scope.material.subjects[j++] = subject;
                  }
              }
          }
  
          function preprocessMaterialEducationalContexts() {
              $scope.material.educationalContexts = [];
      
              for (var i = 0, j = 0; i < $scope.material.taxons.length; i++) {
                  var taxon = $scope.material.taxons[i];
                  var educationalContext = $rootScope.taxonUtils.getEducationalContext(taxon);
        
                  if (educationalContext) {
                    $scope.material.educationalContexts[j++] = educationalContext;
                  }
              }
          }
  
          function countViewSuccess(data) { }
  
          function countViewFail(data, status) { }
  
          $scope.getCorrectLanguageString = function(languageStringList) {
              if (languageStringList) {
                return getUserDefinedLanguageString(languageStringList, translationService.getLanguage(), $scope.material.language);
              }
          }
  
          function isYoutubeVideo(url) {
            // regex taken from http://stackoverflow.com/questions/2964678/jquery-youtube-url-validation-with-regex #ULTIMATE YOUTUBE REGEX
            var youtubeUrlRegex = /^(?:https?:\/\/)?(?:www\.)?(?:youtu\.be\/|youtube\.com\/(?:embed\/|v\/|watch\?v=|watch\?.+&v=))((\w|-){11})(?:\S+)?$/;
              return url && url.match(youtubeUrlRegex);
          }
  
          function isSlideshareLink(url) {
              var slideshareUrlRegex = /^https?\:\/\/www\.slideshare\.net\/[a-zA-Z0-9\-]+\/[a-zA-Z0-9\-]+$/;
              return url && url.match(slideshareUrlRegex);
          }
  
          function setSourceType() {
              if (isYoutubeVideo($scope.material.source)) {
                  $scope.sourceType = 'YOUTUBE';
              } else if (isSlideshareLink($scope.material.source)) {
                  $scope.sourceType = 'SLIDESHARE';
              } else {
              $scope.sourceType = 'LINK';
              }
          }
  
          $scope.formatMaterialIssueDate = function(issueDate) {
              return formatIssueDate(issueDate);
  
          }
  
          $scope.formatMaterialUpdatedDate = function(updatedDate) {
              return formatDateToDayMonthYear(updatedDate);
          }
  
          $scope.isNullOrZeroLength = function (arg) {
            return !arg || !arg.length;
          }
  
          $scope.getAuthorSearchURL = function (firstName, surName) {
              searchService.setSearch('author:"' + firstName + " " + surName + '"');
              $location.url(searchService.getURL());
          }
  
          $scope.showSourceFullscreen = function($event){
              $event.preventDefault()
  
              $scope.fullscreenCtrl.toggleFullscreen();
          };
  
          $scope.slideshareFail = function() {
              $scope.sourceType = 'LINK';
          };
  
          function getSignedUserData() {
              serverCallService.makeGet("rest/user/getSignedUserData", {}, getSignedUserDataSuccess, getSignedUserDataFail);
          }
  
          function getSignedUserDataSuccess(data) {
              var url = $scope.material.source;
              var v = encodeURIComponent(data);
              url += (url.split('?')[1] ? '&':'?') + "dop_token=" + v;
  
              $scope.material.iframeSource = url;
          }
  
          function getSignedUserDataFail(data, status) {
              console.log("Failed to get signed user data.")
          }
          
          $scope.addComment = function() {
              var url = "rest/comment/material";
              var params = {
                  'comment': $scope.newComment,
                  'material': {
                    'type' : '.Material',
                    'id': $scope.material.id
                  }
              };
              serverCallService.makePost(url, params, addCommentSuccess, addCommentFailed);
          };
              
          function addCommentSuccess() {
              $scope.newComment.text = "";
              
              getMaterial(function(material) {
                  $scope.material = material;
              }, function() {
                  log("Comment success, but failed to reload material.");
              });
          }
        
          function addCommentFailed(){
              log('Adding comment failed.');
          }
    }]);
});
