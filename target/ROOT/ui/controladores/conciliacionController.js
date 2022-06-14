angular.module('conciliacion').controller('Conciliacion',
    function($scope,ConciliacionService,$rootScope,$uibModalInstance) {
          //  $scope.title="Ingreso Login";


          $scope.cerrarModal = function() {
            $rootScope.conciliacionOpen = false;      
            $uibModalInstance.dismiss(true);
          }

          $scope.buscarConciliacion = function() {
            ConciliacionService.pedirConciliacion($rootScope.OrdenParaConciliacion.codigoExterno).then(
                function(resp) {
                    if (resp.status == 200) {
                        console.log(resp.data)
                        $scope.conciliacionOrden = resp.data
                    }
                },
                function(err) {
                    console.log(err);
                }
            );
        }
    

        }); //End LoginFormController