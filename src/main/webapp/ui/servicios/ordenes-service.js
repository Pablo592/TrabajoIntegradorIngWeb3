angular.module('ordenes').factory('OrdenesService', function ($http, URL_BASE) {
    return {
        listaOrdenes: function () {
            return $http.get(URL_BASE + '/ordenes');
        },
        add: function (p) {
            return $http.post(URL_BASE + '/ordenes', p);
        },
        edit: function (p) {
            return $http.put(URL_BASE + '/ordenes', p);
        },

        establecerTara: function (p) {
            return $http.put(URL_BASE + '/ordenes/tara', p);
        },

        frenarCarga: function (p) {
            return $http.put(URL_BASE + '/ordenes/frenar/carga', p);
        },

        pesoFinal: function (p) {
            return $http.put(URL_BASE + '/ordenes/peso/final', p);
        },

        remove: function (p) {
            return $http.delete(URL_BASE + '/ordenes/' + p.id);
        }
    };
});



