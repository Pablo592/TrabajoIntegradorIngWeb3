angular.module('ordenes').controller('Ordenes',function($scope, OrdenesService){
    $scope.titulo="Hola desde el controller Ordenes";
    $scope.listaOrdenes = [];//lo defino vacio al arreglo porque voy a usar este arreglo
                            //para obtener la lista de ordenes del back-end
                            //uso promesas para trabajar con el asincronismo(llamadas en 2do plano)
    OrdenesService.listaOrdenes().then(
        //va la funcion en caso de que se hizo el request y se hizo el response todo bien
        function (resp){
            if(resp.status==200){   //lo deduje del console.log
                $scope.listaOrdenes= resp.data;
            }
            console.log(resp);
        },
        function (err){
            console.log(err);
        }
        //va la funcion que se ejecuta cuando no se pudo hacer el request bien
    );


});