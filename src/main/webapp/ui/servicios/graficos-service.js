angular.module('graficos').factory('graphService',['$http','$q','URL_BASE',
function($http, $q, URL_BASE) {
			return {
				requestPushData: function() {
					$http.get(URL_BASE+"/socket/graficos/push");
				}
		} 
	}
]); //End graphService