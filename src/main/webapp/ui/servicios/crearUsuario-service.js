angular.module('crearUsuario').factory('CrearUsuarioService', function ($http, URL_BASE) {
    return {
        crearUsuario: function (p) {
            return $http.post(URL_BASE + '/usuarios',p);
        }
    };
});