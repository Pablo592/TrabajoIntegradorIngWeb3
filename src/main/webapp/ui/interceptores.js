angular.module('trabajoIntegrador')
.factory('APIInterceptor', function($q,$rootScope,$localStorage, $location) {
    return {
      request: function (config) {
            if($localStorage.logged && $localStorage.userdata){
                 var userdata=$localStorage.userdata;
                 config.headers['X-AUTH-TOKEN'] = userdata.authtoken;
            }else{
            	$rootScope.openLoginForm();
            }
        	return config || $q.when(config);
      },
 
     responseError: function(response) {
        if(response.status==401 || response.status==403){
        	$rootScope.openLoginForm();
        }
        return $q.reject(response);
      }
    };
  })