'use strict';

angular.module('noteapp.services', []).factory('NotesService', function ($resource) {
    return $resource('/note/:Operation/:Id',
        {
            Id: '@Id',
            Operation: '@Operation'
        },
        {
            get: {
                method: 'GET',
                isArray: true
            },
            getSolved: {
                method: 'GET',
                isArray: true
            },
            put: {
                method: 'PUT',
                isArray: false
            },
            update: {
                method: 'POST',
                isArray: false
            },
            solve: {
                method: 'POST',
                isArray: false
            },
            delete: {
                method: 'DELETE',
                isArray: false
            }
        });
});
