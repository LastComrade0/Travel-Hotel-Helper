function addFavoriteHotel(userID, hotelID){
    fetch('favoriteHotel/add?userID=' + userID + '&hotelID=' + hotelID,
        {method:'post', headers: {'Content-Type': 'text/html'}}).
        then(res => res.text()).
        then(data => {
            console.log('Response:', data);
            if(data.includes("Duplicate")){
                document.getElementById("addFavoriteMessage").innerHTML = "Hotel already in favorites";
                document.getElementById("addFavoriteMessage").style.color = "red";
            }
            else if(data.includes("Success")){
                document.getElementById("addFavoriteMessage").innerHTML = "Hotel added to favorites";
                document.getElementById("addFavoriteMessage").style.color = "green";
            }
        })
        .catch(error => console.error('Error:', error));
}


//addFavoriteHotel(5,5);