angular.module('trabajoIntegrador').controller('Login',
    function(
        $rootScope, $scope, $localStorage,
        $uibModalInstance, SweetAlert,
        CoreService,$log) {
            $scope.title="Ingreso Loguin";

            $scope.usuario={username:"",password:""};
            $scope.login = function() {
                CoreService.login($scope.usuario).then(
                    function(resp){
                        console.log(resp)
                        if(resp.status===200) {
                            $localStorage.userdata=resp.data;
                            $localStorage.logged=true;
                            $rootScope.loginOpen = false;       //lo uso como flag, antes de cerrar el modal del loguin indico que esta cerrado
                            $uibModalInstance.dismiss(true);  //oculto el modal
                        }else{
                            delete $localStorage.userdata;      // si me logueo mal borro los datos del usuario en el localstore
                            $localStorage.logged=false;
                            SweetAlert.swal( "Problemas autenticando",resp.data, "error");
                        }
                    },
                    function(respErr){
                        $log.log(respErr);  //me ,muestra en caso que venga un error 500
                        SweetAlert.swal( "Problemas autenticando",respErr.data, "error");
                    }
                );
            };
        }); //End LoginFormController