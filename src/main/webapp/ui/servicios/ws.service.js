angular.module('trabajoIntegrador').factory('wsService',		
			function($rootScope, URL_WS, $timeout, $interval, $log, $localStorage) {

			var fnConfig = function(stomp, topic, cb) {
				$log.info("Stomp: suscribiendo a " + topic);
				stomp.subscribe(topic, function(payload, headers, res) {
					cb(payload, headers, res);
				});
			};

			return {
				initStompClient : function(topic, cb) {
					
					$rootScope.stomp.setDebug(function(args) {
						//$log.log(args);
						if($rootScope.stomp.sock.readyState > 1) {
							
							$log.info("Intentando reconexión con WSocket");
							fnConnect();
						}
					});
					var fnConnect = function() {
						if ($localStorage.logged && $localStorage.userdata) {
							$rootScope.stomp.connect(URL_WS+"?xauthtoken="+$localStorage.userdata.authtoken).then(function(frame) {
								$log.info("Stomp: conectado a " + URL_WS);
								fnConfig($rootScope.stomp, topic, cb);
							});
						} else {
							$log.log("No existen credenciales para presentar en WS")
						}
					};
					fnConnect();
				},
				stopStompClient: function() {
					if($rootScope.stomp) 
						$rootScope.stomp.disconnect();
				}
			}

		} );