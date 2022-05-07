angular.module('trabajoIntegrador').factory('PerfilService',function($http, URL_BASE){
    return {
        listaOrdenes:function() {
            return $http.get(URL_BASE+'/ordenes');  //el http es una interfaz de angular
        }
    };
});
