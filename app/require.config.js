require.config({
  paths: {
    authenticatedUserService: 'services/authenticatedUserService',
    serverCallService: 'services/serverCallService',
    authenticationService: 'services/authenticationService',
    searchService: 'services/searchService',
    dopHeader: 'directives/header/header',
    portfolioHeader: 'directives/portfolioHeader/portfolioHeader',
    dopFooter: 'directives/footer/footer',
    dopSidebar: 'directives/sidebar/sidebar',
    translationService: 'services/translationService',
    dopAlert: 'directives/alert/alert',
    alertService: 'services/alertService',
    inputValueControl: 'directives/input-value-control',
    dopDetailedSearch: 'directives/detailedSearch/detailedSearch',
    addPortfolioFabButton: 'directives/addPortfolioFabButton/addPortfolioFabButton',
    addPortfolioDialogController: 'views/addPortfolioDialog/addPortfolioDialog',
    loginDialogController: 'views/loginDialog/loginDialog',
    angular: '../bower_components/angular/angular',
    'angular-animate': '../bower_components/angular-animate/angular-animate',
    'angular-aria': '../bower_components/angular-aria/angular-aria',
    'angular-click-outside': '../bower_components/angular-click-outside/clickoutside.directive',
    'angular-material': '../bower_components/angular-material/angular-material',
    'angular-material-icons': '../bower_components/angular-material-icons/angular-material-icons.min',
    'angular-resource': '../bower_components/angular-resource/angular-resource',
    'angular-route': '../bower_components/angular-route/angular-route',
    'angular-translate': '../bower_components/angular-translate/angular-translate',
    'angular-translate-loader-url': '../bower_components/angular-translate-loader-url/angular-translate-loader-url',
    'angular-youtube-mb': '../bower_components/angular-youtube-mb/src/angular-youtube-embed',
    jquery: '../bower_components/jquery/dist/jquery',
    requirejs: '../bower_components/requirejs/require',
    dopTaxonSelector: 'directives/taxonSelector/taxonSelector'
  },
  shim: {
    angular: {
      exports: 'angular'
    },
    'angular-animate': [
      'angular'
    ],
    'angular-aria': [
      'angular'
    ],
    'angular-material': [
      'angular-animate',
      'angular-aria'
    ],
    'angular-translate': [
      'angular'
    ],
    'angular-translate-loader-url': [
      'angular-translate'
    ],
    'angular-route': [
      'angular'
    ],
    'angular-youtube-mb': [
      'angular'
    ],
    'angular-resource': [
      'angular'
    ],
    'angular-material-icons': [
      'angular'
    ],
    app: [
      'angular',
      'angular-translate',
      'angular-translate-loader-url',
      'angular-youtube-mb',
      'jquery'
    ],
    priority: [
      'angular'
    ]
  },
  packages: [

  ]
});

define([
  'angular',
  'app',
  'angular-translate',
  'angular-translate-loader-url',
  'angular-youtube-mb',
  'jquery',
  'angular-route',
  'angular-material',
  'angular-material-icons',
  'translationService',
  'authenticatedUserService',
  'serverCallService',
  'authenticationService',
  'searchService',
  'dopHeader',
  'portfolioHeader',
  'dopFooter',
  'dopSidebar',
  'dopAlert',
  'alertService',
  'inputValueControl',
  'dopDetailedSearch',
  'addPortfolioFabButton',
  'addPortfolioDialogController',
  'loginDialogController',
  'dopTaxonSelector'
], function (angular, app) {
  'use strict';
  var $html = angular.element(document.getElementsByTagName('html')[0]);

  angular.element().ready(function() {
    angular.bootstrap($html, [app.name]);
  });
});
