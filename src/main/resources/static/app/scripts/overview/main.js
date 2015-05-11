'use strict';

angular.module('noteapp.Overview', [
    'noteapp.services',
    'noteapp.directives'
]).controller('OverviewCtrl', function ($scope, $log, NotesService) {
    var REGEX_PEOPLE = new RegExp('@(\\w+)');
    var REGEX_TAGS = new RegExp('#(\\w+)');

    $scope.notes = [];
    $scope.visibleNotes = [];
    $scope.people = {};
    $scope.tags = {};
    $scope.severities = [];

    NotesService.get().$promise.then(function(data) {
        angular.forEach(data, function(value) {
            addNote(value);

            if (value.people !== null && value.people.length > 0) {
                $log.debug('Note object contains people: ' + value.people);
                forEach(value.people, addPeople);
            }

            if (value.tags !== null && value.tags.length > 0) {
                $log.debug('Note object contains tags: ' + value.tags);
                forEach(value.tags, addTags);
            }
        });

        $scope.visibleNotes = $scope.notes;
    });

    deactivateCreateButton();

    $scope.$watch('description', function(val) {
        if (val === null || val === undefined || val.length <= 0) {
            deactivateCreateButton();
        }
        else {
            activateCreateButton();
        }
    });

    $scope.$on('selection', function(e, args) {
        $log.info('selection changed: ' + args.type);

        var people = getSelectedPeople();
        var tags = getSelectedTags();

        filterNotes(people, tags);
    });

    $scope.onModify = function(args) {
        $log.info('Modified note: ' + args);
    };

    $scope.$on('textedit-modify', function(e, args) {
        $log.info('Modified note ->');
        $log.info(args);

        angular.forEach($scope.notes, function(note) {
            if (note.id === args.uid) {
                note.description = args.val;
                note.people = extractPeople(note.description);
                note.tags = extractTags(note.description);
                NotesService.update(note).$promise.then(function(updatedNote) {
                    if (updatedNote.people !== null && updatedNote.people.length > 0) {
                        $log.debug('Note object contains people: ' + updatedNote.people);
                        forEach(updatedNote.people, addPeople);
                    }

                    if (updatedNote.tags !== null && updatedNote.tags.length > 0) {
                        $log.debug('Note object contains tags: ' + updatedNote.tags);
                        forEach(updatedNote.tags, addTags);
                    }
                });
            }
        });
    });

    /**
     * API: createNote
     *
     * Call this method to create a new note. The description of a note is takes from text field. People and
     * tags are extracted from description. Finally, the note is pushed to the backend.
     */
    $scope.createNote = function() {
        deactivateCreateButton();

        if ($scope.description === undefined || $scope.description === null || $scope.description.length == 0) {
            return;
        }

        var note = {
            description: $scope.description
        };

        note.people = extractPeople($scope.description);
        note.tags = extractTags($scope.description);
        NotesService.put(note).$promise.then(function(newNote) {
            addNote(newNote);

            if (newNote.people !== null && newNote.people.length > 0) {
                $log.debug('Note object contains people: ' + newNote.people);
                forEach(newNote.people, addPeople);
            }

            if (newNote.tags !== null && newNote.tags.length > 0) {
                $log.debug('Note object contains tags: ' + newNote.tags);
                forEach(newNote.tags, addTags);
            }

            $scope.description = '';
            activateCreateButton();
        });
    };

    /**
     * API: markSolved
     *
     * Call this method to mark a note as solved. People and tags might be removed from scope, when there are no
     * other notes referencing the people and tags owned by the solved note.
     *
     * @param {object} note the note that is solved.
     */
    $scope.markSolved = function(note) {
        $scope.notes = _.without($scope.notes, note);
        $scope.visibleNotes = _.without($scope.visibleNotes, note);
        NotesService.solve({'Id': note.id, 'Operation': 'solve'});

        forEach(note.people, removePerson);
        forEach(note.tags, removeTag);
    };

    /**
     * API: togglePerson
     *
     * Activate or deactivate a person for filtering notes.
     *
     * @param {object} person the person that shall be added or removed from filter.
     */
    $scope.togglePerson = function(person) {
        $scope.people[person.name].selected = !$scope.people[person.name].selected;
        $scope.$broadcast('selection', {'type': 'people'});
    };

    /**
     * API: toggleTag
     *
     * Activate or deactivate a tag for filtering notes.
     *
     * @param {object} tag the tag that shall be added or removed from filter.
     */
    $scope.toggleTag = function(tag) {
        $scope.tags[tag.name].selected = !$scope.tags[tag.name].selected;
        $scope.$broadcast('selection', {'type': 'tags'});
    };

    function activateCreateButton() {
        var button = angular.element(document.querySelector('#create_button'));
        button.removeAttr('disabled');
    }

    function deactivateCreateButton() {
        var button = angular.element(document.querySelector('#create_button'));
        button.attr('disabled', 'disabled');
    }

    function forEach(source, func) {
        angular.forEach(source, function(value) {
            func(value);
        });
    }

    function addNote(note) {
        $scope.notes.push(note);
        filterNotes(getSelectedPeople(), getSelectedTags());
    }

    function addPeople(person) {
        if ($scope.people[person.name]) {
            $scope.people[person.name].count += 1;
        }
        else {
            $scope.people[person.name] = {
                'name': person.name,
                'count': 1,
                'selected': false
            };
        }
    }

    function removePerson(person) {
        if (person && $scope.people[person.name]) {
            if ($scope.people[person.name].count > 1) {
                $scope.people[person.name].count -= 1;
            }
            else {
                delete $scope.people[person.name];
            }
        }
    }

    function addTags(tagName) {
        if ($scope.tags[tagName.name]) {
            $scope.tags[tagName.name].count += 1;
        }
        else {
            $scope.tags[tagName.name] = {
                'name': tagName.name,
                'count': 1,
                'selected': false
            };
        }
    }

    function removeTag(tag) {
        if (tag && $scope.tags[tag.name]) {
            if ($scope.tags[tag.name].count > 1) {
                $scope.tags[tag.name].count -= 1;
            }
            else {
                delete $scope.tags[tag.name];
            }
        }
    }

    function extractPeople(text) {
        return extract(text, REGEX_PEOPLE);
    }

    function extractTags(text) {
        return extract(text, REGEX_TAGS);
    }

    function extract(text, regex) {
        var matches = [];
        var start;
        var tmp = text;

        while (tmp != null && (start = tmp.search(regex)) > - 1) {
            var end = tmp.indexOf(' ', start);

            if (end > 0) {
                matches.push(tmp.substring(start + 1, end));
                tmp = tmp.substring(end, tmp.length);
            }
            else {
                matches.push(tmp.substring(start + 1, tmp.length));
                tmp = null;
            }
        }

        return matches;
    }

    function getSelectedTags() {
        var tags = [];

        angular.forEach($scope.tags, function(value) {
            if (value.selected) {
                tags.push(value.name);
            }
        });

        return tags;
    }

    function getSelectedPeople() {
        var people = [];

        angular.forEach($scope.people, function(value) {
            if (value.selected) {
                people.push(value.name);
            }
        });

        return people;
    }

    function containsAllItems(items, must) {
        var containsAll = true;

        angular.forEach(must, function(item) {
            if (items.indexOf(item) < 0) {
                containsAll = false;
            }
        });

        return containsAll;
    }

    function toFlatArray(arr) {
        var flat = [];

        angular.forEach(arr, function(item) {
            flat.push(item.name);
        });

        return flat;
    }

    function filterNotes(people, tags) {
        if (people.length == 0 && tags.length == 0) {
            $log.info('Filter disabled');
            $scope.visibleNotes = $scope.notes;
            return;
        }

        var visibleNotes = [];

        angular.forEach($scope.notes, function(note) {
            if (containsAllItems(toFlatArray(note.people), people) && containsAllItems(toFlatArray(note.tags), tags)) {
                visibleNotes.push(note);
            }
        });

        $scope.visibleNotes = visibleNotes;
    }
});
