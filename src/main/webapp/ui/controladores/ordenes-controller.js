angular.module('ordenes').controller('Ordenes', function ($scope, OrdenesService, SweetAlert, $scope, $uibModal, $rootScope) {

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
    $scope.parametroOrdenamiento = 'codigoExterno';
    $scope.elegido = '';
    $scope.soyAdmin = false;
    $scope.listaOrdenes = [];
    $scope.eliminar = 0;


    OrdenesService.listaOrdenes().then(
        function (resp) {
            if (resp.status == 200) {
                $scope.listaOrdenes = resp.data;
                console.log($scope.listaOrdenes);
            }
            console.log(resp);
        },
        function (err) {
            console.log(err);
        }
    );

    $scope.ordenarOrdenes = function (parametro) {
        $scope.parametroOrdenamiento = parametro;
    }


    $scope.necesitoCrear = function () {
        console.log("check elegido necesitoCrear")
        $scope.elegido = '';
        $scope.situacion = 0;
        $scope.orden = ordenVacia;
    }

    $scope.necesitoActualizar = function () {
        console.log("check elegido necesitoActualizar")
        $scope.situacion = 10;
        $scope.buscarOrdenes();
        $scope.eliminar = 0;
    }

    $scope.necesitoEliminar = function () {
        console.log("check elegido necesitoEliminar")
        $scope.situacion = 11;
        $scope.buscarOrdenes();
        $scope.eliminar = 1;
    }

    $scope.ordenParaActualizar = function () {

        if ($scope.elegido.estado == undefined || $scope.elegido.estado == null)
            return;
        $scope.situacion = parseInt($scope.elegido.estado);
        $scope.orden.codigoExterno = parseInt($scope.elegido.codigoExterno);
        $scope.orden = $scope.elegido;
    }

    $scope.crear = function () {
        console.log($scope.orden);
        if ($scope.elegido = '') {
            $scope.notificacionError("Se debe de seleccionar una orden");
            return;
        }
        OrdenesService.add($scope.orden).then(
            function (resp) {
                if (resp.status == 201) {
                    $scope.ordenAgregada = resp.data;
                    $scope.notificacionAprobacion(resp.xhrStatus);
                }
                console.log(resp);
            },
            function (err) {
                console.log(err);
                $scope.notificacionError(err.data.mensaje);
            }
        );
        $scope.elegido = '';
    }


    $scope.segundoEnvio = function () {
        console.log($scope.orden);
        if ($scope.elegido = '') {
            $scope.notificacionError("Se debe de seleccionar una orden");
            return;
        }
        OrdenesService.establecerTara($scope.orden).then(
            function (resp) {
                if (resp.status == 200) {
                    $scope.ordenAgregada = resp.data;
                    $scope.notificacionAprobacion(resp.xhrStatus);
                    $scope.buscarOrdenes();
                }
                console.log(resp);
            },
            function (err) {
                console.log(err);
                $scope.notificacionError(err.data.mensaje);
            }
        );
        $scope.elegido = '';
    }


    $scope.tercerEnvio = function () {
        console.log($scope.orden);
        if ($scope.elegido = '') {
            $scope.notificacionError("Se debe de seleccionar una orden");
            return;
        }
        OrdenesService.frenarCarga($scope.orden).then(
            function (resp) {
                if (resp.status == 200) {
                    $scope.ordenAgregada = resp.data;
                    $scope.notificacionAprobacion(resp.xhrStatus);
                    $scope.buscarOrdenes();
                }
                console.log(resp);
            },
            function (err) {
                console.log(err);
                $scope.notificacionError(err.data.mensaje);
            }
        );
        $scope.elegido = '';
    }

    $scope.cuartoEnvio = function () {
        console.log($scope.orden);
        if ($scope.elegido = '') {
            $scope.notificacionError("Se debe de seleccionar una orden");
            return;
        }
        OrdenesService.pesoFinal($scope.orden).then(
            function (resp) {
                if (resp.status == 200) {
                    $scope.ordenAgregada = resp.data;
                    $scope.notificacionAprobacion(resp.xhrStatus);
                    $scope.buscarOrdenes();
                }
                console.log(resp);
            },
            function (err) {
                console.log(err);
                $scope.notificacionError(err.data.mensaje);
            }
        );
        $scope.elegido = '';
    }

    $scope.eliminarOrden = function () {
        console.log($scope.orden);
        if ($scope.elegido = '') {
            $scope.notificacionError("Se debe de seleccionar una orden");
            return;
        }
        OrdenesService.remove($scope.orden).then(
            function (resp) {
                if (resp.status == 200) {
                    $scope.ordenAgregada = resp.data;
                    $scope.notificacionAprobacion(resp.xhrStatus);
                    $scope.buscarOrdenes();
                }
                console.log(resp);
            },
            function (err) {
                console.log(err);
                $scope.notificacionError(err.data.mensaje);
            }
        );
        $scope.elegido = '';
    }

    $scope.buscarOrdenes = function () {
        OrdenesService.listaOrdenes($scope.orden).then(
            function (resp) {
                if (resp.status == 200) {
                    $scope.listaOrdenes = resp.data;
                }
                console.log(resp);
            },
            function (err) {
                console.log(err);
            }
        );
    }

    $scope.calculoTiempoRestante = function (caudal, densidad, preset, masa, estado) {

        if (densidad === 0)
            return "";
        let cargaSegundo = caudal * densidad
        let tiempoRestante = ((preset - masa) / (cargaSegundo * 60)).toFixed(2);
        return estado <= 2 ? tiempoRestante : "Finalizado";
    };

    $scope.calculoTiempoTranscurrido = function (fechaI) {
        var fechaInicio = new Date(fechaI).getTime();
        var fechaFin = new Date()
        return parseFloat(Math.abs(fechaFin - fechaInicio) / (1000 * 60)).toFixed(2);
    };



    $rootScope.openConciliacion = function (orden) {
        if (orden.estado != 4)
            return;
        $rootScope.OrdenParaConciliacion = orden;
        if (!$rootScope.conciliacionOpen) {

            $rootScope.conciliacionOpen = true;
            $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'ui/vistas/conciliacion.html',
                controller: 'Conciliacion',
            });
        }
    };

    $rootScope.openGrafica = function (orden) {
        if (orden.estado != 2)
            return;
        $rootScope.OrdenParaGrafica = orden;
        if (!$rootScope.graficaOpen) {
            $rootScope.graficaOpen = true;
            $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'ui/vistas/graficos.html',
                controller: 'GraficosController',
            });
        }
    };


    $scope.notificacionError = function (mensaje) {
        SweetAlert.swal({
            title: mensaje,
            type: "warning",
            showCancelButton: false,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "Aceptar",
            closeOnConfirm: true,
            html: true
        }, function (confirm) { });
    };

    $scope.notificacionAprobacion = function (mensaje) {
        SweetAlert.swal({
            title: mensaje,
            type: "success",
            showCancelButton: false,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "Aceptar",
            closeOnConfirm: true,
            html: true
        }, function (confirm) {
            console.log(mensaje)
        });
    };
});