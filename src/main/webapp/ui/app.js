var app = angular.module('trabajoIntegrador',
    ['ngRoute', 'ordenes', 'conciliacion', 'ui.bootstrap', 'ngStorage', 'oitozero.ngSweetAlert', 'chart.js', 'ngStomp', 'graficos']);

/*declaramos el modulo que tiene que estar escrito igual que en el tag del HTML
y las dependencias y nombres de los modulos creados dentro del arreglo
Cosas a tener en cuenta:
angular.module('trabajoIntegrador',['ngRoute']); ---> Crea
angular.module('trabajoIntegrador'); --> Solo obtenemos
*/
app.constant('URL_BASE', 'http://localhost:8080/test/api/v1');
app.constant('URL_TOKEN', 'http://localhost:8080/test/api/v1/auth');
app.constant('URL_WS', 'http://localhost:8080/test/api/v1/socket');


app.config(function ($localStorageProvider) {
    $localStorageProvider.setKeyPrefix('iw3/');
});


app.run(['$rootScope', '$uibModal', 'CoreService', '$location', '$log', '$localStorage', '$stomp',
    function ($rootScope, $uibModal, CoreService, $location, $log, $localStorage, $stomp, $scope, ) {

        $rootScope.alarma = {
            id: '',
            usuarioAceptador: { "username": '' },
        };

        $rootScope.listaRoles = [];

        $rootScope.listaAlarmas = [];

        $rootScope.alarmaDescripcionSeleccionada = "";

        let logueado = localStorage.getItem('iw3/userdata');
        let logueadoJson = JSON.parse(logueado);
        $rootScope.listaRoles = logueadoJson.roles;
        $rootScope.idLogueado = logueadoJson.idUser;
        $rootScope.alarmas = false;
        $rootScope.stomp = $stomp;

        $rootScope.relocate = function (loc) {   //manejar el direccionamiento en el cliente
            $rootScope.oldLoc = $location.$$path;
            $location.path(loc);
        };

        $rootScope.userData = function () {        //guardar el token en el localstorage
            return $localStorage.userdata;
        };

        $rootScope.logout = function () {
            CoreService.logout();
        };


        $rootScope.pedirAlarmas = function () {
            CoreService.alarmas($rootScope.idLogueado).then(
                //va la funcion en caso de que se hizo el request y se hizo el response todo bien
                function (resp) {
                    if (resp.status == 200) { //lo deduje del console.log
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
                //va la funcion que se ejecuta cuando no se pudo hacer el request bien
            );
        }

        $rootScope.existeAlarma = function () {
            return $rootScope.alarmas;
        }

        $rootScope.openLoginForm = function (size) {     // funcion para llamar al formulario de nuestro loguien desde cualquier lugar de nuestra app
            if (!$rootScope.loginOpen) {
                //$rootScope.cleanLoginData();
                $rootScope.loginOpen = true;            //antes de abrir el modal del loguin indico que esta abierto
                $uibModal.open({
                    animation: true,
                    backdrop: 'static',                //no se me cierra el modal sin importar que me haga click en la pantalla de atras
                    keyboard: false,
                    templateUrl: 'ui/vistas/login.html',//tengo que tener si o si este html
                    controller: 'Login', //tengo que crear este controlador
                    size: size
                });
            }
        };

        $rootScope.cons = function (alarm) {
            console.log(JSON.parse(alarm))
            /* console.log(JSON.parse(alarm).descripcion)*/

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
                    //va la funcion en caso de que se hizo el request y se hizo el response todo bien
                    function (resp) {
                        if (resp.status == 200) { //lo deduje del console.log
                        }
                        console.log(resp.data);
                    },
                    function (err) {
                        console.log(err.data);
                    }
                    //va la funcion que se ejecuta cuando no se pudo hacer el request bien
                );
                $rootScope.listaAlarmas = [];
            });
        }
        //$rootScope.openLoginForm();

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

