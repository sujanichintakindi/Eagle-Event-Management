$.ajax({
	type: 'POST',
	url: 'EEM/SessionManagement/LoginProfileDetails',
	success: function (data) {
		if(data=='Login is required to access this Page'){
			window.location.href='index.html';	
		}else{
			console.log(data);
			var data = $.parseJSON(data);
		      if(data.role=='Manager'){
		    	  $('.navbar li').eq(1).remove();
		      }
		      
		}
	}        
});


$.ajax({
	type: 'POST',
	url: 'EEM/EmployeeManagement/GetEmployees',
	success: function (data) {
		if(data=='Login is required to access this Page'){
			window.location.href='index.html';
		}else{			
			var data = $.parseJSON(data);
			console.log(data);
			for (var i = 0; i < Object.keys(data).length; i++) {
				if(data[i].role=='Admin'){
					$('#employees tbody').append('<tr><td>'+(i+1)+'</td><td>'+data[i].username+'</td><td>'+data[i].firstname+'</td><td>'+data[i].lastname+'</td><td>'+data[i].role+'</td><td><input class="hidden empId" value="'+data[i].employeeId+'"><button type="button" class="btn update">Update</button> <button type="button" class="btn reset">Reset</button></td></tr>');
				}else{
					$('#employees tbody').append('<tr><td>'+(i+1)+'</td><td>'+data[i].username+'</td><td>'+data[i].firstname+'</td><td>'+data[i].lastname+'</td><td>'+data[i].role+'</td><td><input class="hidden empId" value="'+data[i].employeeId+'"><button type="button" class="btn update">Update</button> <button type="button" class="btn deleteEmp">Delete</button> <button type="button" class="btn reset">Reset</button></td></tr>');
				}
				
			}
		}
	}        
});

$('.empDetails').css({'display':' block'});
$('.addemp').css({'display':' none'});
$('.passEmp').css({'display':' none'});

$('.addEmploye').click(function(){
	$('.addemp').css({'display':' block'});
	$('.empDetails').css({'display':' none'});
	$('.AddEmp').css({'display':' inline'});
	$('.updateEmp').css({'display':' none'});
	$('.Empusername').prop('readonly',false);
	$('.Emppassword').parent().parent().show();
	$('.addemp input').val('');
	$('.passEmp').css({'display':' none'});
});
$('.backEmp').click(function(){
	$('.empDetails').css({'display':' block'});
	$('.addemp').css({'display':' none'});
	$('.passEmp').css({'display':' none'});
});
$(document.body).on('click','.reset',function(){
	$('.empDetails').css({'display':' none'});
	$('.addemp').css({'display':' none'});
	$('.passEmp').css({'display':' block'});
	$('.usrnewEmppass').val($(this).parents('tr').find('td').eq(1).text());
	$('.restEmpid').val($(this).parent().find('.empId').val());
	var password = $('.newEmppass').val('');
	var match = $('.conEmppass').val('');
});

$(document.body).on('click','.update',function(){
	$('.addemp').css({'display':' block'});
	$('.empDetails').css({'display':' none'});
	$('.AddEmp').css({'display':' none'});
	$('.updateEmp').css({'display':' inline'});
	$('.Empusername').prop('readonly',true);
	$('.Emppassword').parent().parent().hide();
	 $('.Empfirstname').val($(this).parents('tr').find('td').eq(2).text());
	 $('.Emplastname').val($(this).parents('tr').find('td').eq(3).text());
	 $('.Empusername').val($(this).parents('tr').find('td').eq(1).text());
	 $('.Empid').val($(this).parent().find('.empId').val());
});

$('.restEmp').click(function(){
	var password = $('.newEmppass').val();
	var match = $('.conEmppass').val();
	var id = $('.restEmpid').val();
	if(password == ''){
		alert('password should not be empty');
		return false;
	}else if(match == ''){
		alert('Match password should not be empty');
		return false;
	}else if(password !== match){
		alert('password is not Matching');
		return false;
	}else{
		$.ajax({
	        type: 'POST',
	        url: 'EEM/EmployeeManagement/ResetEmployeePassword',
	        data: {password:password,employeeId:id},
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
$('.AddEmp').click(function(){
	var firstname = $('.Empfirstname').val();
	var lastname = $('.Emplastname').val();
	var username = $('.Empusername').val();
	var password = $('.Emppassword').val();

	if(firstname == ''){
		alert('First Name should not be empty');
		return false;
	}else if(lastname == ''){
		alert('Last Name should not be empty');
		return false;
	}else if(username == ''){
		alert('username should not be empty');
		return false;
	}else if(password == ''){
		alert('password should not be empty');
		return false;
	}else{
		$.ajax({
	        type: 'POST',
	        url: 'EEM/EmployeeManagement/AddEmployee',
	        data: {firstname:firstname,lastname:lastname,username:username,password:password},
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

$('.updateEmp').click(function(){
	var firstname = $('.Empfirstname').val();
	var lastname = $('.Emplastname').val();
	var id = $('.Empid').val();
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

$(document.body).on('click','.deleteEmp',function(){
	
	$.ajax({
	    type: 'POST',
	    url: 'EEM/EmployeeManagement/DeleteEmployee',
	    data: {employeeId:$(this).parent().find('.empId').val()},
	    success: function (data) {
	        if($.trim(data)=='success'){
	        	alert('Delete Successfully');
	        	location.reload();
	        }else{
	        	alert(data);
	        }
	     } 
	  });
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

