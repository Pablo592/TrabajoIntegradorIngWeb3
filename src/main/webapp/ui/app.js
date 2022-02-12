var app = angular.module('trabajoIntegrador',
    ['ngRoute','ordenes','ui.bootstrap','ngStorage','oitozero.ngSweetAlert']);
/*declaramos el modulo que tiene que estar escrito igual que en el tag del HTML
y las dependencias y nombres de los modulos creados dentro del arreglo
Cosas a tener en cuenta:
angular.module('trabajoIntegrador',['ngRoute']); ---> Crea
angular.module('trabajoIntegrador'); --> Solo obtenemos



*/

app.constant('URL_BASE','http://localhost:8080/test/api/v1')
