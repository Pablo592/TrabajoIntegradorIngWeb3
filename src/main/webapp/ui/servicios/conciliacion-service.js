angular.module('conciliacion').factory('ConciliacionService', function ($http, URL_BASE) {
    return {
        pedirConciliacion: function (id) {
            return $http.get(URL_BASE + '/ordenes/conciliacion/' + id);
        }
    };
});
