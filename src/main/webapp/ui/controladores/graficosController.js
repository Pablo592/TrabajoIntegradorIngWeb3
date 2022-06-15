angular.module('graficos').controller('GraficosController',
	function ($scope, $log, wsService, graphService, $rootScope) {

		$scope.title = 'Demostración de gráficos por WebSockets';

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
				labels: labels
			}
		};
		$scope.iniciaWS = function () {
			$log.log("iniciandoWS");
			wsService.initStompClient('/iw3/data', function (payload,
				headers, res) {
				$log.log(payload);
				console.log(payload);
				if (payload.type == 'GRAPH_DATA') {
					$scope.procesaDatosGraph(payload.payload);
				}
				if (payload.type == 'NOTIFICA') {
					$scope.notificar(payload.payload.label, payload.payload.value);
				}
				$scope.$apply();
			});


			wsService.initStompClient('/iw3/alarma', function (payload,
				headers, res) {
				let aux = JSON.stringify($rootScope.listaAlarmas)
				if($rootScope.listaAlarmas != "")
				$rootScope.listaAlarmas = JSON.parse(aux.substring(0, aux.length - 1) + "," + JSON.stringify(payload.payload) + aux.substring(aux.length - 1,));
				else{
				$rootScope.listaAlarmas = JSON.parse("["+JSON.stringify(payload.payload)+"]");
				$rootScope.alarmas = true;
				}
			
				if (payload.type == 'GRAPH_DATA') {
					$scope.procesaDatosGraph(payload.payload);
				}
				if (payload.type == 'NOTIFICA') {
					console.log(payload);
				}
				$scope.$apply();
			});
		}

		$scope.requestRefresh = function () {
			graphService.requestPushData();
		};


		$scope.iniciaWS()


		$scope.$on("$destroy", function () {
			wsService.stopStompClient();
		});

	}
); //End GraficosController