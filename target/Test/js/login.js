$.ajax({
	type: 'POST',
	url: 'EEM/SessionManagement/CheckAdmin',
	success: function (data) {
		if($.trim(data)=='success'){
			console.log(data);
		}else{
			alert(data);
		}
	}        
});

$.ajax({
	type: 'POST',
	url: 'EEM/SessionManagement/LoginProfileDetails',
	success: function (data) {
		if(data!='Login is required to access this Page'){
			window.location.href='home.html';	
		}
	}        
});

$('#login').click(function(){
	var username = $('input[name="phoneNumber"]').val();
	var password = $('input[name="password"]').val();

	if(username == ''){
		alert('Username should not be empty');
		return false;
	}else if(password == ''){
		alert('Password should not be empty');
		return false;
	}else{
			$('#loginModal').modal('show');
			$.ajax({
	        type: 'POST',
	        url: 'EEM/SessionManagement/EmployeeLogin',
	        data: {username:username,password:password},
	        	success: function (data) {
		             if($.trim(data)=='success'){
		             	window.location.href='home.html';
		             }else{
		             	alert(data);
		             }
	        	}        
	    	});
	}
});