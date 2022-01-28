angular.module('trabajoIntegrador').config(function ($routeProvider,$locationProvider){
    $locationProvider.hashPrefix('!');
    $routeProvider
        .when('/login',{
            templateUrl : 'ui/vistas/crearUsuario.html',
            controller: 'Login'
        }).when('/perfil',{
        templateUrl : 'ui/vistas/perfil.html',
        controller: 'Perfil'
    }).otherwise({
        redirectTo: '/login'
    });
});


