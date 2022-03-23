angular.module('ordenes').controller('Ordenes', function($scope, OrdenesService, SweetAlert) {
    $scope.titulo = "Hola desde el controller Ordenes";
    $scope.orden = {
        codigoExterno: '',
        fechaTurno: '',
        fechaPesajeInicial: '',
        fechaRecepcionPesajeFinal: '',
        frecuencia: '',
        camion: { patente: '', cisternadoLitros: '', preset: '', tara: '', pesoFinalCamion: '' },
        cliente: { razonSocial: '', contacto: '' },
        chofer: { nombre: '', apellido: '', documento: '' },
        producto: { nombre: '' }
    };
    var ordenVacia = $scope.orden;
    $scope.situacion = 0;
    $scope.elegido = '';

    $scope.hacerNada = function() {}


    $scope.necesitoCrear = function() {
        console.log("check elegido necesitoCrear")
        $scope.elegido = '';
        $scope.situacion = 0;
        $scope.orden = ordenVacia;
        //$scope.notificacion("mensaje");
    }

    $scope.necesitoActualizar = function() {
        console.log("check elegido necesitoActualizar")
        $scope.situacion = 10;
        $scope.buscarOrdenes();
    }

    $scope.ordenParaActualizar = function() {

        if ($scope.elegido.estado == undefined || $scope.elegido.estado == null)
            return;
        $scope.situacion = parseInt($scope.elegido.estado);
        $scope.orden.codigoExterno = parseInt($scope.elegido.codigoExterno);
        $scope.orden = $scope.elegido;
    }

    $scope.crear = function() {
        console.log($scope.orden);

        OrdenesService.add($scope.orden).then(
            //va la funcion en caso de que se hizo el request y se hizo el response todo bien
            function(resp) {
                if (resp.status == 201) { //lo deduje del console.log
                    $scope.ordenAgregada = resp.data;
                    $scope.notificacionAprobacion(resp.xhrStatus);
                }
                console.log(resp);
            },
            function(err) {
                console.log(err);
                $scope.notificacionError(err.data.mensaje);
            }
            //va la funcion que se ejecuta cuando no se pudo hacer el request bien
        );
    }

    $scope.segundoEnvio = function() {
        console.log($scope.orden);

        OrdenesService.establecerTara($scope.orden).then(
            //va la funcion en caso de que se hizo el request y se hizo el response todo bien
            function(resp) {
                if (resp.status == 200) { //lo deduje del console.log
                    $scope.ordenAgregada = resp.data;
                    $scope.notificacionAprobacion(resp.xhrStatus);
                    $scope.buscarOrdenes();
                }
                console.log(resp);
            },
            function(err) {
                console.log(err);
                $scope.notificacionError(err.data.mensaje);
            }
            //va la funcion que se ejecuta cuando no se pudo hacer el request bien
        );
    }


    $scope.tercerEnvio = function() {
        console.log($scope.orden);

        OrdenesService.frenarCarga($scope.orden).then(
            //va la funcion en caso de que se hizo el request y se hizo el response todo bien
            function(resp) {
                if (resp.status == 200) { //lo deduje del console.log
                    $scope.ordenAgregada = resp.data;
                    $scope.notificacionAprobacion(resp.xhrStatus);
                    $scope.buscarOrdenes();
                }
                console.log(resp);
            },
            function(err) {
                console.log(err);
                $scope.notificacionError(err.data.mensaje);
            }
            //va la funcion que se ejecuta cuando no se pudo hacer el request bien
        );
    }

    $scope.cuartoEnvio = function() {
        console.log($scope.orden);
        OrdenesService.pesoFinal($scope.orden).then(
            //va la funcion en caso de que se hizo el request y se hizo el response todo bien
            function(resp) {
                if (resp.status == 200) { //lo deduje del console.log
                    $scope.ordenAgregada = resp.data;
                    $scope.notificacionAprobacion(resp.xhrStatus);
                    $scope.buscarOrdenes();
                }
                console.log(resp);
            },
            function(err) {
                console.log(err);
                $scope.notificacionError(err.data.mensaje);
            }
            //va la funcion que se ejecuta cuando no se pudo hacer el request bien
        );
    }



    $scope.buscarOrdenes = function() {
        OrdenesService.listaOrdenes($scope.orden).then(
            //va la funcion en caso de que se hizo el request y se hizo el response todo bien
            function(resp) {
                if (resp.status == 200) { //lo deduje del console.log
                    $scope.listaOrdenes = resp.data;

                }
                console.log(resp);
            },
            function(err) {
                console.log(err);
            }
            //va la funcion que se ejecuta cuando no se pudo hacer el request bien
        );
    }


    $scope.notificacionError = function(mensaje) {
        SweetAlert.swal({
            title: mensaje,
            type: "warning",
            showCancelButton: false,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "Aceptar",
            closeOnConfirm: true,
            html: true
        }, function(confirm) {});
    };

    $scope.notificacionAprobacion = function(mensaje) {
        SweetAlert.swal({
            title: mensaje,
            type: "success",
            showCancelButton: false,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "Aceptar",
            closeOnConfirm: true,
            html: true
        }, function(confirm) {});
    };
});