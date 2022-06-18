angular.module('trabajoIntegrador').controller('Login',
    function (
        $rootScope, $scope, $localStorage,
        $uibModalInstance, SweetAlert,
        CoreService, $log) {
        $scope.title = "Ingreso Login";

        $scope.usuario = { username: "", password: "" };
        $scope.login = function () {
            CoreService.login($scope.usuario).then(
                function (resp) {
                    console.log(resp)
                    if (resp.status === 200) {
                        $localStorage.userdata = resp.data;
                        $rootScope.listaRoles = resp.data.roles;
                        console.log("la lista de los roles del usuario es: " + resp.data.roles)
                        $localStorage.logged = true;
                        $rootScope.loginOpen = false;
                        $uibModalInstance.dismiss(true);
                    } else {
                        delete $localStorage.userdata;
                        $localStorage.logged = false;
                        SweetAlert.swal("Problemas autenticando", resp.data, "error");
                    }
                },
                function (respErr) {
                    $log.log(respErr);
                    SweetAlert.swal("Problemas autenticando", respErr.data, "error");
                }
            );
        };
    }); 