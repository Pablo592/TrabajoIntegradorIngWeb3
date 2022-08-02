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

        $scope.volver = function () {
            window.location.replace("https://pgsj.mooo.com/index.html#!/ordenes");
        }

        $scope.crearUsaurio = function () {
            console.log($scope.usuarioACrear)
            CrearUsuarioService.crearUsuario($scope.usuarioACrear).then(
                function (resp) {
                    if (resp.status == 201) {
                        $scope.notificacionAprobacion('Usuario creado con exito');
                    }
                    console.log(resp);
                },
                function (err) {
                    console.log(err);
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