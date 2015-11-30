Parse.Cloud.afterSave("Posts",function(request){
     
     var city= request.object.get("city");

    var query = new Parse.Query(Parse.Installation);
    query.equalTo('channels', city);
            		 Parse.Push.send(
                 {
                			where: query,
                			data: 
                      {
                      		city: city,
                 			  alert: "Linna "+city+" lisati uus postitus"
               				}
              			}, 
                    { success: function() { 
                			console.log("Success");
                		}, error: function(err) { 
               	       console.log(err);
               	    }
            		});
});




















