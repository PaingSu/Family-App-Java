angular.module('test')
.controller('WWTest', function($scope, $http) {
	$http.get('http://localhost:8080/Buzze_webApp/api/customer/terms_conditions').then(function(response){
		$scope.terms_conditions=response.data;
	});
});