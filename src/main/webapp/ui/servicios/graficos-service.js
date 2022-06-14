angular.module('graficos').moduloGraficos.factory('graphService',['$http','$q','URL_BASE',
function($http, $q, URL_BASE) {
			return {
				requestPushData: function() {
					$http.get(URL_BASE+"/api/v1/graph/push");
				}
		} 
	}
]); //End graphService