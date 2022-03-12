angular.module('ordenes').controller('Ordenes',function($scope, OrdenesService){
    $scope.titulo="Hola desde el controller Ordenes";
    $scope.orden = {
        codigoExterno :'',
        fechaTurno :'',
        fechaPesajeInicial:'',
        fechaRecepcionPesajeFinal:'',
        frecuencia: '',
        camion: {patente:'',cisternadoLitros:'',preset:'',tara:'',pesoFinalCamion:''},
        cliente: {razonSocial:'',contacto:''},
        chofer: {nombre:'',apellido:'',documento:''},
        producto: {nombre:''}
    };

    $scope.crear = function (){
        console.log($scope.orden );

        OrdenesService.add( $scope.orden).then(
            //va la funcion en caso de que se hizo el request y se hizo el response todo bien
            function (resp){
                if(resp.status==201){   //lo deduje del console.log
                    $scope.listaOrdenes= resp.data;
                }
                console.log(resp);
            },
            function (err){
                console.log(err);
            }
            //va la funcion que se ejecuta cuando no se pudo hacer el request bien
        );
    }
});
