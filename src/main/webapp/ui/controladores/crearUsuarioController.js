angular.module('crearUsuario').controller('CrearUsuario',
    function ($scope, CrearUsuarioService, SweetAlert) {

        $scope.usuarioACrear = {
            nombre: '',
            apellido: '',
            email: '',
            password: '',
            username: '',
            roles: [{
                id: 0
            }]
        }


        $scope.usuarioAdminACrear = {
            nombre: '',
            apellido: '',
            email: '',
            password: '',
            username: '',
            roles: [{
                id: 2
            }, 
            {
                id: 1
            }]
        }


        $scope.volver = function () {
            window.location.replace("https://pgsj.mooo.com/index.html#!/ordenes");
        }
        let aux = null;
        $scope.crearUsaurio = function () {
            if ($scope.usuarioACrear.roles[0].id ==='2'){
                $scope.usuarioAdminACrear.nombre    = $scope.usuarioACrear.nombre;
                $scope.usuarioAdminACrear.apellido  = $scope.usuarioACrear.apellido
                $scope.usuarioAdminACrear.email     = $scope.usuarioACrear.email
                $scope.usuarioAdminACrear.password  = $scope.usuarioACrear.password
                $scope.usuarioAdminACrear.username  = $scope.usuarioACrear.username
                aux = $scope.usuarioAdminACrear;
            }else{
                aux = $scope.usuarioACrear;
            }
            CrearUsuarioService.crearUsuario(aux).then(
                function (resp) {
                    if (resp.status == 201) {
                        $scope.notificacionAprobacion('Usuario creado con exito');
                    }
                },
                function (err) {
                    $scope.notificacionError(err.data.error);
                }
            );
            $scope.elegido = '';
        }

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
                $scope.volver();
            });
        };

    }); 