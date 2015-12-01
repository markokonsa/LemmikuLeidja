Parse.Cloud.afterSave("Posts",function(request){
     
     var city= removeSpecialCharacters(request.object.get("city"));

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

function removeSpecialCharacters(string){
	var finalString = string;
	 finalString = finalString.replace("õ","o");
	 finalString = finalString.replace("ä","a");
	 finalString = finalString.replace("ö","o");
	 finalString = finalString.replace("ü","u");
	 return finalString;
}




















