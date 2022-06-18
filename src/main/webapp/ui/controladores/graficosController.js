angular.module('graficos').controller('GraficosController',
	function ($scope, $log, CoreService,$rootScope, $uibModalInstance) {

		let orden = $rootScope.OrdenParaGrafica

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
				if (o.label === "Carga Acumulada")
					$scope.kgAcumuladoGrafico = o.value
			});
			$scope.graphOptions.demo.data = {
				data: data,
				labels: labels,
			}
		};
		$scope.iniciaWS = function () {
			$log.log("iniciandoWS");
			CoreService.initStompClient('/iw3/data/' + orden.codigoExterno, function (payload,
				headers, res) {
				$log.log(payload);
				console.log(payload);
				if (payload.type == 'GRAPH_DATA') {
					$scope.procesaDatosGraph(payload.payload);
				}
				$scope.$apply();
			});
		}
		$scope.iniciaWS()

		$scope.$on("$destroy", function () {
			CoreService.stopStompClient();
		});

		$scope.cerrarModal = function () {
			$rootScope.graficaOpen = false;
			$uibModalInstance.dismiss(true);
		}

		$scope.calculoTiempoRestante = function () {

			let caudal = orden.ultimoCaudalLitroSegundo;
			let densidad = orden.ultimaDensidadProductoKilogramoMetroCub;
			let preset = orden.camion.preset;
			let masa = $scope.kgAcumuladoGrafico;
			let estado = orden.estado;

			if (densidad === 0)
				return "";

			let cargaSegundo = Math.pow(caudal, -3) * densidad
			let tiempoRestante = ((preset - masa) / (cargaSegundo * 60)).toFixed(2);
			return estado <= 2 ? tiempoRestante : "Finalizado";
		};


	}
); 