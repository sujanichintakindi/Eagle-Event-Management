$('.eventTab').css({'display':' block'});
$('.addEvent').css({'display':' none'});

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
var customerData=[]
$.ajax({
	type: 'POST',
	url: 'EEM/CustomerManagement/GetCustomers',
	success: function (data) {
		if(data=='Login is required to access this Page'){
			window.location.href='index.html';
		}else{			
			var data = $.parseJSON(data);
			console.log(data);
			customerData=data;
			$('.customerName').html('').append('<option value="">Select Customer </option>');
			for (var i = 0; i < Object.keys(data).length; i++) {
				$('.customerName').append('<option value="'+data[i].customerId+'">'+data[i].name+'</option>');
			}
			 $.ajax({
				 	type: 'POST',
				 	url: 'EEM/EventManagement/GetEvents',
				 	success: function (data) {
				 		if(data=='Login is required to access this Page'){
				 			window.location.href='index.html';
				 		}else{			
				 			console.log(data);var data = $.parseJSON(data);
				 			
				 			for (var i = 0; i < Object.keys(data).length; i++) {
				 				for(var j = 0; j < Object.keys(customerData).length; j++){
				 					if(customerData[j].customerId==data[i].customerId){
				 						$('#customers tbody').append('<tr><td>'+(i+1)+'</td><td>'+data[i].eventName+'</td><td>'+customerData[j].name+'</td><td>'+data[i].eventSchedule+'</td><td>'+data[i].tableSize+'</td><td>'+data[i].venue+'</td><td><a href="javascript:void(0)" class="btn generateOutput">Guest List</a><input type="hidden" class="eventId" value="'+data[i].eventId+'" download><form action="EEM/EventManagement/FileDownload" method="GET" id="formRule"> <input type="hidden" value="'+data[i].fileLocation+'" name="filePath" class="btn rulesLink"><input type="submit" value="Rules" class="btn downloadRules"></form> <button type="button" class="btn deleteEvent">Delete</button></td></tr>');
				 					}				 					
				 				}				 				
				 			}
				 		}
				 	}        
				 });
		}
	}        
});

var today = new Date();
var dd = today.getDate();
var mm = today.getMonth()+1; //January is 0!
var yyyy = today.getFullYear();
 if(dd<10){
        dd='0'+dd
    } 
    if(mm<10){
        mm='0'+mm
    } 

today = yyyy+'-'+mm+'-'+dd+'T22:57';
$('.datetime').attr('min',today);

$(document).on('click','.deleteEvent',function(){
	var eventId = $(this).parent().find('.eventId').val();
	console.log(eventId);
	$.ajax({
        type: 'POST',
        url: 'EEM/EventManagement/DeleteEvent',
        data: {eventId:eventId},
        success: function (data) {
        	console.log(data)
            if($.trim(data)=='success'){
          	  location.reload();
           }else{
           	alert(data);
           }
        }
	});
});
	
$('.addEventBtn').click(function(){
	$('.addEvent').css({'display':' block'});
	$('.eventTab').css({'display':' none'});
	$('.addEventSubmit').css({'display':' inline'});
	$('.UpdateEvent').css({'display':' none'});
	$('.addEvent input').val('');
});

$('.backEvent').click(function(){
	$('.eventTab').css({'display':' block'});
	$('.addEvent').css({'display':' none'});
});

// $(document.body).on('click','.updateCus',function(){
// 	$('.cusUpdate').css({'display':' block'});
// 	$('.CusTab').css({'display':' none'});
// 	$('.addCus').css({'display':' none'});
// 	$('.UpdateCus').css({'display':' inline'});
// 	 $('.cusPhn').val($(this).parents('tr').find('td').eq(2).text());
// 	 $('.cusEmail').val($(this).parents('tr').find('td').eq(3).text());
// 	 $('.cusName').val($(this).parents('tr').find('td').eq(1).text());
// 	 $('.cusid').val($(this).parent().find('.customerId').val());
// });

$(document).on('click','.addEventSubmit',function(e){
	e.preventDefault();
	var eventName = $.trim($('.eventName').val());
	var customerName = $.trim($('.customerName').val());
	var date = $.trim($('.datetime').val());
	var venue = $.trim($('.venue').val());
	var tableSize = $.trim($('.tableSize').val());
	var file
	if(eventName == ''){
		alert('Event Name should not be empty');
		return false;
	}else if(customerName == ''){
		alert('Customer Name should not be empty');
		return false;
	}else if(date == ''){
		alert('Invalid Event Date');
		return false;
	}else if(venue == ''){
		alert('Venue should not be empty');
		return false;
	}else if(tableSize == ''){
		alert('No. of Seats in Table should not be empty');
		return false;
	}else{
		var formData = new FormData($("#addEvent")[0]);
		console.log(tableSize,customerName,date,venue,tableSize);
		$.ajax({
	        type: 'POST',
	        url: 'EEM/EventManagement/AddEvent',
	        data: formData,
	        success: function (data) {
	        	console.log(data)
	              if($.trim(data)=='success'){
	            	  location.reload();
	             }else{
	             	alert(data);
	             }
	        },
			cache: false,
			contentType: false,
			processData: false       
	      });	
	}
	
});



$('#addList').change(function(){
	var csv = $(this).val().slice($(this).val().lastIndexOf(".")+1);
	if(csv!="csv"){
		alert("Select Only CSV fromat file");
		$('#addList').val('');
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

$(document).on('click','.generateOutput',function(e){
	$.ajax({
	    type: 'POST',
	    url: 'EEM/EventManagement/GetResult',
	    data: {eventId:$('.eventId').val()},
	    success: function (data) {
	        json_to_csv(data, "Seating Plan Report", "Event Report");
	     } 
	  });
});

function json_to_csv(json_data, title, label) {
    var result = jQuery.parseJSON(json_data);
    var csv = '';     
    csv += title + '\r\n\n';
    csv += 'Sr.No,Guest Name,Table Number\r\n';
    var i = 1;
	for (var key in result) {
        csv += i + ',' +result[key].role +','+ result[key].tableNumber + '\r\n';
        i = i + 1;
    }
    if (csv == '') {        
        alert("No data found");
        return;
    }
    var file_name = "EventReport";  
    var uri = 'data:text/csv;charset=utf-8,' + escape(csv);    
    var link = document.createElement("a");    
    link.href = uri;
    link.style = "visibility:hidden";
    link.download = file_name + ".csv";    
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}