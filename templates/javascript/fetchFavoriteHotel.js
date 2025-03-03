function showFavoriteHotels(userID){
    fetch('/favoriteHotel?userID=' + userID, {method:'get'}).
    then(res => res.json()).
    then(data => {
        if(Array.isArray(data) && data.length === 0){
            document.getElementById("showFavorite").innerHTML = 'No favorite hotel. Please go to hotel page and add.';
            return;
        }
        const outputElement = document.getElementById('showFavorite');
        outputElement.innerHTML = '';
        data.forEach(hotel => {
            const hotelID = hotel.hotelID;
            const hotelName = hotel.hotelName;
            const hotelNameH3 = document.createElement('h3');
            hotelNameH3.textContent = hotelName;


            const goToHotel = document.createElement("a");
            goToHotel.href = `/hotel?hotelID=${hotelID}`;
            goToHotel.innerHTML = "Go to hotel"
            const goToParagraph = document.createElement('p');
            goToParagraph.appendChild(goToHotel);

            outputElement.appendChild(hotelNameH3);
            outputElement.appendChild(goToParagraph);

        })

    }).
    catch(err => {
        console.log(err);
    });
};