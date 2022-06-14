angular.module('ordenes').factory('OrdenesService',function($http, URL_BASE){
    return {
        listaOrdenes:function() {
            return $http.get(URL_BASE+'/ordenes');  //el http es una interfaz de angular
        },
        add:function(p) {
            return $http.post(URL_BASE+'/ordenes/primer-envio',p);
        },
        edit:function(p) {
            return $http.put(URL_BASE+'/ordenes',p);
        },

        establecerTara:function(p) {
            return $http.put(URL_BASE+'/ordenes/tara',p);  //Segundo envio
        },

        frenarCarga:function(p) {
            return $http.put(URL_BASE+'/ordenes/frenar-carga',p);  //Tercer envio
        },

        pesoFinal:function(p) {
            return $http.put(URL_BASE+'/ordenes/peso-final',p);  //Cuarto envio
        },

        remove:function(p) {
            return $http.delete(URL_BASE+'/ordenes/'+p.id);
        }
    };
});



