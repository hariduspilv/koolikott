require.config({
  baseUrl: './',
  paths: {
    'requirejs': '../bower_components/requirejs/require',
    'angular': '../bower_components/angular/angular.min',
    'angularAMD': '../bower_components/angularAMD/angularAMD.min',
    'ngload': '../bower_components/angularAMD/ngload.min',
    'angular-animate': '../bower_components/angular-animate/angular-animate.min',
    'angular-aria': '../bower_components/angular-aria/angular-aria.min',
    'angular-click-outside': '../bower_components/angular-click-outside/clickoutside.directive',
    'angular-material': '../bower_components/angular-material/angular-material.min',
    'angular-resource': '../bower_components/angular-resource/angular-resource.min',
    'angular-route': '../bower_components/angular-route/angular-route.min',
    'angular-translate': '../bower_components/angular-translate/angular-translate.min',
    'angular-translate-loader-url': '../bower_components/angular-translate-loader-url/angular-translate-loader-url.min',
    'angular-youtube-mb': '../bower_components/angular-youtube-mb/dist/angular-youtube-embed.min',
    'angular-screenfull': '../bower_components/angular-screenfull/dist/angular-screenfull.min',
    'angular-scroll': '../bower_components/angular-scroll/angular-scroll',
    'angular-material-data-table': '../bower_components/angular-material-data-table/dist/md-data-table',
    'screenfull': '../bower_components/screenfull/dist/screenfull',
    'jsog': '../bower_components/jsog/lib/JSOG',
    'ngInfiniteScroll': '../bower_components/ngInfiniteScroll/build/ng-infinite-scroll.min',
    'jquery': '../bower_components/jquery/dist/jquery.min',
    'ng-file-upload': '../bower_components/ng-file-upload/ng-file-upload.min',
    'clipboard': '../bower_components/clipboard/dist/clipboard.min'
  },
  shim: {
    'angularAMD': ['angular'],
    'ngload': ['angularAMD'],
    'angular-animate': ['angular'],
    'angular-aria': ['angular'],
    'angular-material': [
      'angular-animate',
      'angular-aria'
    ],
    'angular-translate': ['angular'],
    'angular-translate-loader-url': ['angular-translate'],
    'angular-route': ['angular'],
    'angular-youtube-mb': ['angular'],
    'angular-resource': ['angular'],
    'angular-material-icons': ['angular'],
    'angular-screenfull': [
      'angular',
      'screenfull'
    ],
    'angular-scroll': ['angular'],
    'ng-file-upload': ['angular'],
    'angular-click-outside': ['angular'],
    'angular-material-data-table': [
      'angular-material',
      'angular'
    ],
    'ngInfiniteScroll': [
      'angular',
      'jquery'
    ]
  },
  deps: ['app']
});