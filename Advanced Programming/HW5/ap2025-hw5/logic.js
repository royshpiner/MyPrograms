
function solution_js_query(){
    const maliciousL = document.querySelectorAll(".malicious");
    for (let link of maliciousL){
        link.style.display = 'none';   //hids all the malicious links
    }
    const hiddenL = document.querySelector(".hidden");
    if (hiddenL){
        hiddenL.style.display = 'block';  //show all links according to their display
    }

}

function solution_js_dynamic_elements(){
    const newD = document.createElement("div"); //create a new div and save it
    const newH = document.createElement("h2"); // create a new header and save it
    newH.textContent = "some text"  // the content of the header is some text
    const newP = document.createElement("p"); // create a new paragraph and save it
    newP.textContent = "some text also"
    newD.appendChild(newH);   // the header an paragraph will be inside the new divider we created/
    newD.appendChild(newP);   
    const questionDiv = document.getElementById("q6");  // the new div we created will be inside the div of the question
    questionDiv.appendChild(newD)
}

function solution_js_event_listeners(){
    const listenedButton = document.getElementById("div_btn");  //reciev the button we are waiting to be pressed
    listenedButton.addEventListener("click",clicked); // after it is pressed we waid until clicked
    document.body.addEventListener("keydown", pressed); // what until a key is pressed
    function clicked(){  // if clicked on the screen
    alert("click");
    }
    function pressed(event){ // if pressed on a key, print it
        alert(`The key '${event.key}' was pressed`);    
    }
}

function solution_js_unit_converter(){
    let convertionOutput = document.getElementById("convertion_output"); // get where the coverted will be
    let conversions = {cm: 1,meter: 100,inch: 2.54,foot: 30.48}; //keep the ratios of confersion to cm
    let inputValue = document.getElementById("convertion_input").value; // get the input value to convet
    let fromUnit = document.getElementById("convert_from_unit").value;  // get chosen in unit of input number
    let toUnit = document.getElementById("convert_to_unit").value;  // get chosen in the unit to convert
    let valueCm = inputValue * conversions[fromUnit];  // get it's value in cm
    let returnedValue = valueCm / conversions[toUnit];  // convert from cm to requested
    convertionOutput.value = returnedValue; // save the value
    
    
}

function checkValid(){    //for q4
    const username = document.getElementById("username").value;      //get the input recieved from the form 
    const password = document.getElementById("password").value;
    const email = document.getElementById("email").value;
    const age = document.getElementById("age").value;
    const unwantedExp = /^[a-zA-Z0-9-]{4,}$/;           //only letters numbers and - , minumum 4 chars
    const unwantedExpPas = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*\-_()])[a-zA-Z0-9!@#$%^&*\-_()]{8,}$/; //?= needs to have a letter , a special symbom,nuber more than 8 chars.
    const unwantedExpMailStart = /^[^.-].*/; //regexp for the user part not start with . or - 
    const unwantedExpMailEndU = /.*[^.-]$/;  // does not end with . ot -
    const fullEmail = email.split('@'); //split the email to parts and check vadility of each one

    if (!unwantedExp.test(username)){  //check username
        alert("The form is invalid");
        return;       
    }
    if (!unwantedExpPas.test(password)){  //check password
        alert("The form is invalid");
        return;
    }
    if (fullEmail.length !== 2) {
        alert("The form is invalid"); // no @ or more than 1
        return;
      }
    const userPart = fullEmail[0];
    const domainPart = fullEmail[1];
    if (!unwantedExpMailStart.test(userPart) || !unwantedExpMailEndU.test(userPart) ||userPart.includes("..") || userPart.includes("#")){ // check if meets regexp requirments and no .. or #
        alert("The form is invalid");
        return;
    }
    const domainSplit = domainPart.split('.');
    if (domainSplit.length < 2 || domainSplit[domainSplit.length-1].length < 2 || domainPart.includes("..") || domainPart.includes("#")){ // check if a . if not contains.. or # and more than 2 chars root
        alert("The form is invalid");
        return
    }
    
    if (age > 120 || age < 10){   //requirements for the age
        alert("The form is invalid");
        return;
    }
    alert("The form is valid");   //no problems found with the form
    return;
}