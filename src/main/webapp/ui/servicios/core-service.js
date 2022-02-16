angular.module('trabajoIntegrador').factory('CoreService',function($http,URL_BASE,$log,$localStorage,URL_TOKEN){
	return {
		login: function(usuario) {
			const config={
				method:'POST',
				url: URL_TOKEN+'/login-json',
				headers : { 'Content-Type': 'application/x-www-form-urlencoded' },
				data: `username=${usuario.username}&password=${usuario.password}`
			};
			return $http(config);	
		},
		authInfo:function(){
			//$log.log()
			return $http.get(URL_TOKEN+"/auth-info"); //metodo protegio, que sea protegido indica que si al tratar de consumirlo me devuelve un 403 y si estoy logueado me devuelve un 200
		},
		logout: function() {
			delete $localStorage.userdata;
			$localStorage.logged=false;
			$http.get(URL_BASE+"/auth-info");
		}
	};
});