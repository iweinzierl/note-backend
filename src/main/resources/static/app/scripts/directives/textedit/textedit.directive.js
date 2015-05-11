'use strict';

angular.module('noteapp.directives.textedit', []).directive('textedit', function($log, $timeout) {
    return {
        restrict: 'E',
        scope: {
            text: '@',
            uid: '@'
        },
        templateUrl: 'app/views/directives/textedit/textedit.html',
        link: function ($scope, $element) {
            $scope.mode = 'text';
            $scope.edit = $scope.text;

            $scope.toggle = function() {
                if ($scope.mode === 'text') {
                    $scope.mode = 'edit';

                    $timeout(function() {
                        document.getElementById($scope.uid).focus();
                    }, 200);
                }
                else {
                    $scope.mode = 'text';
                }
            };

            $scope.submit = function() {
                $log.debug('Change text from ' + $scope.text + ' to ' + $scope.edit);
                $scope.text = $scope.edit;
                $scope.mode = 'text';

                $scope.$emit('textedit-modify', {
                    'uid': $scope.uid,
                    'val': $scope.edit
                });
            };

            $scope.onBlur = function() {
                $scope.mode = 'text';
            }
        }
    };
});
