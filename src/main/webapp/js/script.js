$.ajax({
	type: 'POST',
	url: 'EEM/SessionManagement/LoginProfileDetails',
	success: function (data) {
		if(data=='Login is required to access this Page'){
			window.location.href='index.html';	
		}else{
			console.log(data);
			var data = $.parseJSON(data);
			
			  $('.firstname').val(data.firstname);
		      $('.lastname').val(data.lastname);
		      $('.username').val(data.username);
		      $('.userRole').val(data.role);
		      $('.id').val(data.id);
		      if(data.role=='Manager'){
		    	  $('.navbar li').eq(1).remove();
		      }
		      
		}
	}        
});

$('.updateDetails').click(function(){
	var firstname = $('.firstname').val();
	var lastname = $('.lastname').val();
	var id = $('.id').val();
	if(firstname == ''){
		alert('First Name should not be empty');
		return false;
	}else if(lastname == ''){
		alert('Last Name should not be empty');
		return false;
	}else{
		$.ajax({
	        type: 'POST',
	        url: 'EEM/EmployeeManagement/UpdateEmployee',
	        data: {firstname:firstname,lastname:lastname,employeeId:id},
	        success: function (data) {
	              if($.trim(data)=='success'){
	            	  location.reload();
	             }else{
	             	alert(data);
	             }
	        }        
	      });	
	}
	
});

$('.updatePassword').click(function(){
	$('.passwordUpdate').css({'display':' block'});
	$('.details').css({'display':' none'});
});
$('.backProfile').click(function(){
	$('.details').css({'display':' block'});
	$('.passwordUpdate').css({'display':' none'});
});

$('.changePassword').click(function(){
	var oldpassword = $('.oldpassword').val();
	var newpassword = $('.newpassword').val();
	var id = $('.id').val();
	if(oldpassword == ''){
		alert('Old Password should not be empty');
		return false;
	}else if(newpassword == ''){
		alert('New Password should not be empty');
		return false;
	}else{
		$.ajax({
	        type: 'POST',
	        url: 'EEM/EmployeeManagement/UpdateEmployeePassword',
	        data: {oldPassword:oldpassword,newPassword:newpassword,employeeId:id},
	        success: function (data) {
	        	if($.trim(data)=='success'){
	            	  location.reload();
	             }else{
	             	alert(data);
	             }
	        }        
	      });	
	}
	
});

$('.login').click(function(){
	$.ajax({
	    type: 'POST',
	    url: 'EEM/SessionManagement/EmployeeLogout',
	    data: '',
	    success: function (data) {
	        if($.trim(data)=='Logged Out'){
	        	location.reload();
	        }else{
	        	alert(data);
	        }
	     } 
	  });
});


$('.details').css({'display':' block'});
$('.passwordUpdate').css({'display':' none'});