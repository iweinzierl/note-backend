'use strict';

angular.module('noteapp.directives.confirm', []).directive('ngConfirm', function() {
        return {
            link: function(scope, element, attr) {
                var msg = "Are you sure?";
                element.bind('click', function(event) {
                    if (window.confirm(msg)) {
                        scope.$eval(attr.ngConfirm)
                    }
                });
            }
        };
    }
);