angular.module('trabajoIntegrador').config(function ($routeProvider, $locationProvider, $httpProvider) {
    $locationProvider.hashPrefix('!');

    $httpProvider.interceptors.push('APIInterceptor');

    $routeProvider
        .when('/login', {
            templateUrl: 'ui/vistas/login.html',
            controller: 'Login'
        })

        .when('/ordenes', {
            templateUrl: 'ui/vistas/ordenes.html',
            controller: 'Ordenes'
        })

        .when('/graficos', {
            templateUrl: 'ui/vistas/graficos.html',
            controller: 'GraficosController'
        })

        .when('/crearuser', {
            templateUrl: 'ui/vistas/crearUsuario.html',
            controller: 'CrearUsuario'
        })

        .otherwise({
            redirectTo: '/ordenes'
        });
});


