angular.module('trabajoIntegrador').config(function ($routeProvider,$locationProvider){
    $locationProvider.hashPrefix('!');
    $routeProvider
        .when('/login',{
            templateUrl : 'ui/vistas/crearUsuario.html',
            controller: 'Login'
        })	.otherwise({
        redirectTo: '/login'
    });
});


