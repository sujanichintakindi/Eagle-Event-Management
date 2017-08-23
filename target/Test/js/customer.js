$('.CusTab').css({'display':' block'});
$('.cusUpdate').css({'display':' none'});

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
	url: 'EEM/CustomerManagement/GetCustomers',
	success: function (data) {
		if(data=='Login is required to access this Page'){
			window.location.href='index.html';
		}else{			
			var data = $.parseJSON(data);
			console.log(data);
			for (var i = 0; i < Object.keys(data).length; i++) {
				$('#customers tbody').append('<tr><td>'+(i+1)+'</td><td>'+data[i].name+'</td><td>'+data[i].contact+'</td><td>'+data[i].email+'</td><td><input class="hidden customerId" value="'+data[i].customerId+'"><button type="button" class="btn updateCus">Update</button> <button type="button" class="btn deleteCus">Delete</button></td></tr>');
			}
		}
	}        
});

function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

$('.addCustomer').click(function(){
	$('.cusUpdate').css({'display':' block'});
	$('.CusTab').css({'display':' none'});
	$('.addCus').css({'display':' inline'});
	$('.UpdateCus').css({'display':' none'});
	$('.cusUpdate input').val('');
});

$('.backCus').click(function(){
	$('.CusTab').css({'display':' block'});
	$('.cusUpdate').css({'display':' none'});
});

$(document.body).on('click','.updateCus',function(){
	$('.cusUpdate').css({'display':' block'});
	$('.CusTab').css({'display':' none'});
	$('.addCus').css({'display':' none'});
	$('.UpdateCus').css({'display':' inline'});
	 $('.cusPhn').val($(this).parents('tr').find('td').eq(2).text());
	 $('.cusEmail').val($(this).parents('tr').find('td').eq(3).text());
	 $('.cusName').val($(this).parents('tr').find('td').eq(1).text());
	 $('.cusid').val($(this).parent().find('.customerId').val());
});

$('.addCus').click(function(){
	var name = $('.cusName').val();
	var email = $('.cusEmail').val();
	var phone = $('.cusPhn').val();

	if(name == ''){
		alert('Name should not be empty');
		return false;
	}else if(email == ''){
		alert('Email should not be empty');
		return false;
	}else if(!validateEmail(email)){
		alert('Email not valid');
		return false;
	}else if(phone == ''){
		alert('Phone Number should not be empty');
		return false;
	}else if(phone.length != 10){
		alert('Phone Number Should be 10 length');
		return false;
	}else if(isNaN(phone)){
		alert('Phone Number Not valid');
		return false;
	}else{
		$.ajax({
	        type: 'POST',
	        url: 'EEM/CustomerManagement/AddCustomer',
	        data: {name:name,email:email,contact:phone},
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

$('.UpdateCus').click(function(){
	var name = $('.cusName').val();
	var email = $('.cusEmail').val();
	var phone = $('.cusPhn').val();
	
	if(name == ''){
		alert('Name should not be empty');
		return false;
	}else if(email == ''){
		alert('Email should not be empty');
		return false;
	}else if(!validateEmail(email)){
		alert('Email not valid');
		return false;
	}else if(phone == ''){
		alert('Phone Number should not be empty');
		return false;
	}else if(phone.length != 10){
		alert('Phone Number Should be 10 length');
		return false;
	}else if(isNaN(phone)){
		alert('Phone Number Not valid');
		return false;
	}else{
		$.ajax({
	        type: 'POST',
	        url: 'EEM/CustomerManagement/UpdateCustomer',
	        data: {name:name,email:email,contact:phone,employeeId:$('.cusid').val()},
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

$(document.body).on('click','.deleteCus',function(){
	
	$.ajax({
	    type: 'POST',
	    url: 'EEM/CustomerManagement/DeleteCustomer',
	    data: {customerId:$(this).parent().find('.customerId').val()},
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