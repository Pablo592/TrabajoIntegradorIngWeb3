angular.module('trabajoIntegrador').config(function ($routeProvider,$locationProvider){
    $locationProvider.hashPrefix('!');  //se o coloco antes del # para que me lo indexen los robots de google, osea se de cuenta que esta en un servidor la pagina
    $routeProvider  //creamos las rutas
        .when('/login',{
            templateUrl : 'ui/vistas/loguin.html',//tengo que tener si o si este html
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
        redirectTo: '/login'
    });
});


