var app = angular.module('trabajoIntegrador',
    ['ngRoute', 'ordenes', 'conciliacion', 'ui.bootstrap', 'ngStorage', 'oitozero.ngSweetAlert', 'chart.js', 'ngStomp', 'graficos','crearUsuario']);

app.constant('URL_BASE','http://pgsj.mooo.com/produccion/api/v1');
app.constant('URL_TOKEN','http://pgsj.mooo.com/produccion/api/v1/auth');
app.constant('URL_WS','http://pgsj.mooo.com/produccion/api/v1/socket');

app.config(function ($localStorageProvider) {
    $localStorageProvider.setKeyPrefix('iw3/');
});


app.run(['$rootScope', '$uibModal', 'CoreService', '$location', '$log', '$localStorage', '$stomp',
    function ($rootScope, $uibModal, CoreService, $location, $window, $localStorage, $stomp) {

        $rootScope.alarma = {
            id: '',
            usuarioAceptador: { "username": '' },
        };


        $rootScope.alarmaDescripcionSeleccionada = "";

        let logueado = localStorage.getItem('iw3/userdata');
        let logueadoJson = JSON.parse(logueado);
        $rootScope.listaRoles = [];
        if( logueadoJson != null){
            $rootScope.listaRoles = logueadoJson.roles;
            $rootScope.idLogueado = logueadoJson.idUser;
        }
        $rootScope.alarmas = false;
        $rootScope.stomp = $stomp;

        $rootScope.listaAlarmas = [];

        $rootScope.relocate = function (loc) {
            $rootScope.oldLoc = $location.$$path;
            $location.path(loc);
        };

        $rootScope.userData = function () { 
            return $localStorage.userdata;
        };

        $rootScope.logout = function () {
            CoreService.logout();
        };


        $rootScope.pedirAlarmas = function () {
            CoreService.alarmas($rootScope.idLogueado).then(
               
                function (resp) {
                    if (resp.status == 200) {
                        $rootScope.listaAlarmas = resp.data;
                        if ($rootScope.listaAlarmas.length > 0) {
                            $rootScope.alarmas = true;
                        } else {
                            $rootScope.alarmas = false;
                        }
                        console.log($rootScope.alarmas)
                    }
                    console.log(resp.data);
                },
                function (err) {
                    console.log(resp.data);
                }
            );
        }

        $rootScope.existeAlarma = function () {
            return $rootScope.alarmas;
        }

        $rootScope.openLoginForm = function (size) {
            if (!$rootScope.loginOpen) {
                $localStorage.logged = false;
                $rootScope.loginOpen = true;
                $uibModal.open({
                    animation: true,
                    backdrop: 'static',
                    keyboard: false,
                    templateUrl: 'ui/vistas/login.html',
                    controller: 'Login',
                    size: size
                });
            }
        };

        $rootScope.cons = function (alarm) {
            console.log(JSON.parse(alarm))

            let alarmaJson = JSON.parse(alarm);
            let mensajeAcotadoJson = alarmaJson.descripcion;
            let mensajeAcotado = JSON.stringify(mensajeAcotadoJson);
            let mensajeAcotado1 = mensajeAcotado.split("(codigo externo)")[1];
            let mensajeAcotado2 = "La Orden" + mensajeAcotado1.split("con una temperatura de")[0] + "ha superado los" +
                mensajeAcotado1.split("con una temperatura de")[1].substring(0, mensajeAcotado1.split("con una temperatura de")[1].length - 1)
                + "°C de temperatura";

            $rootScope.alarma.id = alarmaJson.id;
            $rootScope.alarma.usuarioAceptador.username = alarmaJson.autor.username;

            console.log(JSON.parse(JSON.stringify($rootScope.alarma)))

            swal({
                title: mensajeAcotado2,
                type: "warning",
                showCancelButton: false,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Aceptar",
                closeOnConfirm: true,
                html: true
            }, function (confirm) {
                $rootScope.alarmas = false;
                CoreService.alarmaAceptar(JSON.stringify($rootScope.alarma)).then(
                    function (resp) {
                        if (resp.status == 200) {
                        }
                        console.log(resp.data);
                    },
                    function (err) {
                        console.log(err.data);
                    }
                );
                $rootScope.listaAlarmas = [];
            });
        }
        $rootScope.botonCrearUsuario = function () {
            for (let i = 0; i < $rootScope.listaRoles.length; i++) {
                if ($rootScope.listaRoles[i] === 'ROLE_ADMIN')
                    return true;
            }
            return false;
        }


        $rootScope.paginaCrearUsuario = function () {
            window.location.replace("http://pgsj.mooo.com/index.html#!/crearuser");
        }

        $rootScope.getRole = function () {

            for (let i = 0; i < $rootScope.listaRoles.length; i++) {
                if ($rootScope.listaRoles[i] === 'ROLE_ADMIN')
                    return 'ROLE_ADMIN';
            }
            return 'ROLE_USER'
        }
        $rootScope.iniciaWS = function () {
            CoreService.initStompClient('/iw3/alarma', function (payload, headers, res) {
                let aux = JSON.stringify($rootScope.listaAlarmas)
                if ($rootScope.listaAlarmas != "")
                    $rootScope.listaAlarmas = JSON.parse(aux.substring(0, aux.length - 1) + "," + JSON.stringify(payload.payload) + aux.substring(aux.length - 1,));
                else
                    $rootScope.listaAlarmas = JSON.parse("[" + JSON.stringify(payload.payload) + "]");
                $rootScope.alarmas = true;
                $rootScope.$apply();
            });
        }
      
       $rootScope.iniciaWS()

        CoreService.authInfo();
    }
]);

