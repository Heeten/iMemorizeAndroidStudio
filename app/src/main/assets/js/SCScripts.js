// JavaScript Document

// load the JSON Data from the passed in url
// this allows us to also pass in a dataLoadedHandler once the data is loaded
function loadJSON(dataURL,dataLoadedHandler){
			if (document.getElementById) {
					var x = (window.ActiveXObject) ? new ActiveXObject("Microsoft.XMLHTTP") : new XMLHttpRequest();
			}
			if (x){
					x.onreadystatechange = function(){
							if (x.readyState == 4){							
									dataLoadedHandler(eval("(" + x.responseText + ")"));
							}
					}
			 x.open("GET", dataURL, true);
				x.send(null);
		}
}


// this lets us add two numbers (in either number or px format) and return the total in px format
function addPXValues(){
		var total = 0;
		for(var i=0;i<arguments.length;i++){
			total += getPxValue(arguments[i]);
		}
		return total + "px";
}

// this extract the number from a px value
function getPxValue(myVal){
	return Number(myVal.replace("px",""));
}


// shortcut to getElementById()
function getElement(divName){
	return document.getElementById(divName);
}

function preloadImages() { 
  var d=document; 
		if(d.images){ 
				if(!d.pl) d.pl=new Array();
    var i, j=d.pl.length, a=preloadImages.arguments; 
				for(i=0; i<a.length; i++){
    		if (a[i].indexOf("#")!=0){ 
						  d.pl[j]=new Image; 
						  d.pl[j++].src=a[i];
						}
				}
		}
}

function trace(s){
	//document.getElementById("trace").innerHTML += "<br />" + s;
}

function clearTrace(){
		//document.getElementById("trace").innerHTML = "";
}

// this returns the selected value from a dropdown
function getSelectedValueFromDropdown(dropdown){
	var tIndex = dropdown.selectedIndex;
	var tValue = dropdown[tIndex].value;
	return tValue;	
}

// trim string and add elipses. pass in string and the numbersof letters 
// to truncate to -- the function then moves back to the last space and adds elipses
function trimString(strText,numLetters,elipses){
	var truncString = strText.substring(0,numLetters);
	var indexOfSpace = truncString.lastIndexOf(" ");
	var strElipse;
	elipses?strElipse="...":strElipse="";				
	return truncString.substring(0,indexOfSpace) + strElipse;
}

/*
Object.prototype.setSize = function(w, h){
	this.style.width=w;
	this.style.height=h;
}
*/
