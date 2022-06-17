angular.module('graficos').controller('GraficosController',
	function ($scope, $log, CoreService, graphService, $rootScope,$uibModalInstance) {

		
	let orden =	$rootScope.OrdenParaGrafica

		$scope.graphOptions = {
			demo: {
				options: {},
				data: {}
			}
		};
		
		$scope.procesaDatosGraph = function (datos) {
			var labels = [];
			var data = [];
			datos.forEach(function (o, i) {
				labels.push(o.label);
				data.push(o.value);
			});
			$scope.graphOptions.demo.data = {
				data: data,
				labels: labels,
			}
		};
		$scope.iniciaWS = function () {
			$log.log("iniciandoWS");
			CoreService.initStompClient('/iw3/data/'+orden.codigoExterno, function (payload,
				headers, res) {
				$log.log(payload);
				console.log(payload);
				if (payload.type == 'GRAPH_DATA') {
					$scope.procesaDatosGraph(payload.payload);
				}
				$scope.$apply();
			});
		}

		$scope.requestRefresh = function () {
			graphService.requestPushData();
		};

		$scope.iniciaWS()

		$scope.$on("$destroy", function () {
			CoreService.stopStompClient();
		});

		$scope.cerrarModal = function() {
            $rootScope.graficaOpen = false;      
            $uibModalInstance.dismiss(true);
          }



	}
); //End GraficosController