angular.module('trabajoIntegrador').controller('Login',
    function(
        $rootScope, $scope, $localStorage,
            $uibModalInstance, SweetAlert,
            CoreService,$log) {
            $scope.title="Ingreso";

            $scope.user={name:"",password:""};
            $scope.login = function() {
                CoreService.login($scope.user).then(
                    function(resp){
                        if(resp.status===200) {
                            $localStorage.userdata=resp.data;
                            $localStorage.logged=true;
                            $rootScope.loginOpen = false;
                            $uibModalInstance.dismiss(true);
                        }else{
                            delete $localStorage.userdata;
                            $localStorage.logged=false;
                            SweetAlert.swal( "Problemas autenticando",resp.data, "error");
                        }
                    },
                    function(respErr){
                        $log.log(respErr);
                        SweetAlert.swal( "Problemas autenticando",respErr.data, "error");
                    }
                );
            };
        }); //End LoginFormController