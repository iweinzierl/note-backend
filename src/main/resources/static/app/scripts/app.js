'use strict';

angular.module('noteapp', [
    'noteapp.Overview',
    'noteapp.Trash',
    'noteapp.directives',
    'ngResource',
    'ngSanitize',
    'ngRoute',
    'ngAnimate',
    'ngTouch'
  ]).config(function ($routeProvider) {
      $routeProvider
        .when('/', {
          templateUrl: 'app/views/overview/main.html',
          controller: 'OverviewCtrl'
        })
        .when('/trash', {
          templateUrl: 'app/views/trash/main.html',
          controller: 'TrashCtrl'
        })
        .otherwise({
          redirectTo: '/'
        });
    });
