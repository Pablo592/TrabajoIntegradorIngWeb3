angular.module('trabajoIntegrador')   // el interceptor me sirce para que cada vez que se envie un request obtenga el tojen del localstore y lo agregue en la cabecera del request
.factory('APIInterceptor', function($q,$rootScope,$localStorage, $location) {
    return {
      request: function (config) {
            if($localStorage.logged && $localStorage.userdata){ // si estoy logueado y tengo el token en el localstore
                 var userdata=$localStorage.userdata;
                 config.headers['X-AUTH-TOKEN'] = userdata.authtoken;
            }else{
            	$rootScope.openLoginForm(); //lo abro el loguin para que se autentifique
            }
        	return config || $q.when(config);   //devuelvo la promesa config o la creo con $q
      },
 
     responseError: function(response) {
        if(response.status==401 || response.status==403){ //si el end-point me devuelve uno de estos status code entonces le abro el formulario
        	$rootScope.openLoginForm();
        }
        return $q.reject(response); // tras abrir el formulario de loguin devuelvo una promesa con la parte que devolvio un con error
      }
    };
  })