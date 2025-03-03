function addExpediaHistory(userID, url){
    fetch('http://localhost:8080/expediaHistory/add?userID=' + userID + '&url=' + url ,
        {method:'post', headers: {'Content-Type': 'text/html'}}).
        then(res => {res.text(); console.log(res); alert(url); resolve()}).
        then(data => {
        console.log('Response:', data);
        })
        .catch(error => console.error('Error:', error));
}

//addExpediaHistory(10, "example.com");