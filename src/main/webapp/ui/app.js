var app = angular.module('trabajoIntegrador',
    ['ngRoute','ordenes','ui.bootstrap','ngStorage','oitozero.ngSweetAlert','ngStomp']);
/*declaramos el modulo que tiene que estar escrito igual que en el tag del HTML
y las dependencias y nombres de los modulos creados dentro del arreglo
Cosas a tener en cuenta:
angular.module('trabajoIntegrador',['ngRoute']); ---> Crea
angular.module('trabajoIntegrador'); --> Solo obtenemos
*/
app.constant('URL_BASE','http://localhost:8080/test/api/v1');



app.run(['$rootScope','$uibModal','CoreService','$location','$log','$localStorage', '$stomp',
    function($rootScope, $uibModal, CoreService, $location, $log, $localStorage, $stomp) {

        $rootScope.stomp=$stomp;

        $rootScope.relocate = function(loc) {
            $rootScope.oldLoc=$location.$$path;
            $location.path(loc);
        };

        $rootScope.userData=function() {
            return $localStorage.userdata;
        };

        $rootScope.logout=function() {
            CoreService.logout();
        };

        $rootScope.openLoginForm = function(size) {     // funcion para llamar al formulario de nuestro loguien desde cualquier lugar de nuestra app
            if (!$rootScope.loginOpen) {
                //$rootScope.cleanLoginData();
                $rootScope.loginOpen = true;            //antes de abrir el modal del loguin indico que esta abierto
                $uibModal.open({
                    animation : true,
                    backdrop : 'static',                //no se me cierra el modal sin importar que me haga click en la pantalla de atras
                    keyboard : false,
                    templateUrl : 'ui/vistas/loguin.html',//tengo que tener si o si este html
                    controller: 'Login', //tengo que crear este controlador
                    size : size
                });
            }
        };

        $rootScope.openLoginForm();

        //CoreService.authInfo();

    }
]);
