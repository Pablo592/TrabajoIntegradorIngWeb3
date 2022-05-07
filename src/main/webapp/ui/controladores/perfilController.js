angular.module('trabajoIntegrador').controller('Perfil',function($scope, PerfilService){
    $scope.titulo="Hola desde el controller Perfil";

    $scope.listaOrdenesPorUsuario = [];


    PerfilService.listaOrdenes().then(
            //va la funcion en caso de que se hizo el request y se hizo el response todo bien
            function(resp) {
                if (resp.status == 200) { //lo deduje del console.log
                    $scope.listaOrdenesPorUsuario = resp.data;
                }
                console.log(resp);
            },
            function(err) {
                console.log(err);
            }
            //va la funcion que se ejecuta cuando no se pudo hacer el request bien
        );

});