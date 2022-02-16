angular.module('trabajoIntegrador').config(function ($routeProvider, $locationProvider, $httpProvider){ // $httpProvider lo uso para configurar las rutas
    $locationProvider.hashPrefix('!');  //se o coloco antes del # para que me lo indexen los robots de google, osea se de cuenta que esta en un servidor la pagina

    $httpProvider.interceptors.push('APIInterceptor');  //nombre del servicio de interpectores

    $routeProvider  //creamos las rutas
        .when('/login',{
            templateUrl : 'ui/vistas/login.html',//tengo que tener si o si este html
            controller: 'Login' //tengo que crear este controlador
        })

        .when('/perfil',{
        templateUrl : 'ui/vistas/perfil.html',
        controller: 'Perfil'
        })

        .when('/ordenes',{
        templateUrl : 'ui/modulos/ordenes/ordenes.html',
        controller: 'Ordenes'
        })

        .otherwise({
        redirectTo: '/perfil'
    });
});


