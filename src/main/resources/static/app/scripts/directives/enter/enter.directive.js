'use strict';

angular.module('noteapp.directives.enter', []).directive('ngEnter', function ($log) {
    return function (scope, element, attrs) {
        element.bind('keypress', function (event) {
            if(event.which === 13) {
                $log.debug("received enter event");
                scope.$apply(function (){
                    
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
        });
    };
});
