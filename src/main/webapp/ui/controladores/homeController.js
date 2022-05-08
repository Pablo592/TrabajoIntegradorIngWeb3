angular.module('trabajoIntegrador').controller('Home',function($scope, HomeService,SweetAlert){
    $scope.titulo="Hola desde el controller Home";

    $scope.listaOrdenesPorUsuario = [];
    $scope.calculoTiempoRestante = function(caudal,densidad,preset,masa) {

        if(densidad === 0)
        return "";

    let cargaSegundo  =  Math.pow(caudal,-3)*densidad
    return ((preset - masa)/(cargaSegundo*60)).toFixed(2);
    };

    $scope.calculoTiempoTranscurrido = function(fechaI,fechaF) {

        if(fechaF === null)
        return "";

        var fechaInicio = new Date(fechaI).getTime();
        var fechaFin    = new Date(fechaF).getTime();
        return  Math.abs(fechaFin - fechaInicio)/(1000*60);
    };


    HomeService.listaOrdenes().then(
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