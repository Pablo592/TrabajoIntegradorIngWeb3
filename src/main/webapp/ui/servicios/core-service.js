angular.module('trabajoIntegrador').factory('CoreService',function($http,URL_BASE,$log,$localStorage,URL_TOKEN){
	return {
		login: function(usuario) {
			const config={
				method:'POST',
				url: URL_TOKEN+'/login',
				headers : { 'Content-Type': 'application/x-www-form-urlencoded' },
				data: `username=${usuario.username}&password=${usuario.password}`
			};
			return $http(config);	
		},
		authInfo:function(){
			//$log.log()
			return $http.get(URL_BASE+"/auth-info");
		},
		logout: function() {
			delete $localStorage.userdata;
			$localStorage.logged=false;
			$http.get(URL_BASE+"/auth-info");
		}
	};
});