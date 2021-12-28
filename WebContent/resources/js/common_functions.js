




 /*   Family Profile */

	function fpFunction() {
	    document.getElementById("familyprofiletabmenu").style.backgroundColor = "#59c6ad";
	    location.href='http://localhost:8080/familyapplicationnewversion/views/family_profile.html';
	}
	
	function fmFunction() {
	    document.getElementById("familymembertabmenu").style.backgroundColor = "#59c6ad";
	    location.href='http://localhost:8080/familyapplicationnewversion/views/family_member.html';
	   
	}
	
	function leaveFunction() {
	    document.getElementById("leavetabmenu").style.backgroundColor = "#59c6ad";
	    location.href='http://localhost:8080/familyapplicationnewversion/views/leave_family.html';
	}

	function requestFunction(){
		document.getElementById("requesttabmenu").style.backgroundColor = "#59c6ad";
		location.href='http://localhost:8080/familyapplicationnewversion/views/request_noti.html';
	}

	function editprofilebtn(){
   		location.href='http://localhost:8080/familyapplicationnewversion/views/editprofile.html';
	}
	
	function profilebtn() {
   		document.getElementById("header_profile").style.backgroundColor = "#59c6ad";
   		/* document.getElementById("createtabmenu").disabled = true; */
   		location.href='http://localhost:8080/familyapplicationnewversion/views/edit_profile.html';
	}
	
	function familyprofilebtn() {
   		document.getElementById("header_familyprofile").style.backgroundColor = "#59c6ad";
   		/* document.getElementById("createtabmenu").disabled = true; */
   		location.href='http://localhost:8080/familyapplicationnewversion/views/family_profile.html';
	}







	/*  new Family Profile */

	function nfpFunction() {
   		document.getElementById("newfamilyprofiletabmenu").style.backgroundColor = "#59c6ad";
   		/* document.getElementById("createtabmenu").disabled = true; */
   		location.href='http://localhost:8080/familyapplicationusingbootstrap/views/new_familyprofile.html';
	}

	function nfmFunction() {
	    document.getElementById("newfamilymembertabmenu").style.backgroundColor = "#59c6ad";
	    /* document.getElementById("jointabmenu").disabled = true;
	    location.href='http://localhost:8080/FamilyApplication/views/create_family.html'; */
	    location.href='http://localhost:8080/familyapplicationusingbootstrap/views/new_familymember.html';
	}
	
	
	function familyprofilebtn(){
		document.getElementById("header_logout").style.backgroundColor = "#59c6ad";
   		/* document.getElementById("createtabmenu").disabled = true; */
   		location.href='http://localhost:8080/familyapplicationnewversion/views/family_profile.html';
	}

	
		
	

	function logoutbtn() {
   		document.getElementById("header_logout").style.backgroundColor = "#59c6ad";
   		/* document.getElementById("createtabmenu").disabled = true; */
   		location.href='http://localhost:8080/familyapplicationusingbootstrap/views/login.html';
	}

	
	
	/* discuss */
	function discussionbtn(){
		document.getElementById("header_discuss").style.backgroundColor = "#59c6ad";
   		/* document.getElementById("createtabmenu").disabled = true; */
   		location.href='http://localhost:8080/familyapplicationnewversion/views/common_discussion.html';
	}
	
	function createnewTopic(){
		document.getElementById("header_familyprofile").style.backgroundColor = "#59c6ad";
   		/* document.getElementById("createtabmenu").disabled = true; */
   		location.href='http://localhost:8080/familyapplicationnewversion/views/create_topic.html';
	}
	
	
	
	
