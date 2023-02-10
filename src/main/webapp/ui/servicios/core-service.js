angular.module('trabajoIntegrador').factory('CoreService', function ($http, URL_BASE, $log, $localStorage, URL_TOKEN, URL_WS, $rootScope) {
	var fnConfig = function (stomp, topic, cb) {
		$log.info("Stomp: suscribiendo a " + topic);
		stomp.subscribe(topic, function (payload, headers, res) {
			cb(payload, headers, res);
		});
	};
	return {
		login: function (usuario) {
			const config = {
				method: 'POST',
				url: URL_TOKEN + '/login-json',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
				data: `username=${usuario.username}&password=${usuario.password}`
			};
			return $http(config);
		},
		authInfo: function () {
			return $http.get(URL_TOKEN + "/auth-info");
		},
		logout: function () {
			delete $localStorage.userdata;
			$localStorage.logged = false;
			$http.get(URL_BASE + "/auth-info");
		},
		alarmas: function (p) {
			return $http.get(URL_BASE + '/alarmas/author/' + p);
		},
		alarmaAceptar: function (alarma) {
			return $http.put(URL_BASE + '/alarmas/aceptar', alarma);
		},
		initStompClient: function (topic, cb) {

			$rootScope.stomp.setDebug(function (args) {
				if ($rootScope.stomp.sock.readyState > 1) {

					$log.info("Intentando reconexión con WSocket");
					fnConnect();
				}
			});
			var fnConnect = function () {
				if ($localStorage.logged && $localStorage.userdata) {
					$rootScope.stomp.connect(URL_WS + "?xauthtoken=" + $localStorage.userdata.authtoken).then(function (frame) {
						$log.info("Stomp: conectado a " + URL_WS);
						fnConfig($rootScope.stomp, topic, cb);
					});
				} else {
					$log.log("No existen credenciales para presentar en WS")
				}
			};
			fnConnect();
		},
		stopStompClient: function () {
			if ($rootScope.stomp)
				$rootScope.stomp.disconnect();
		}
	};
});