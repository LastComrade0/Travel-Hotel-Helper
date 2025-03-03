function showReviews(str, username, page){
    if(str === ""){
        document.getElementById("showReviews").innerHTML = "No reviews selected";
        return;
    }
    fetch('/reviews?hotelID=' + str + '&page=' + page, {method:'get'}).
    then(res => res.json()).
    then(data => {
        if(Array.isArray(data) && data.length === 0){
            document.getElementById("showReviews").innerHTML = 'No reviews to show';
            return;
        }
        const outputElement = document.getElementById('showReviews');
        outputElement.innerHTML = '';
        //const jsonReviewArray = JSON.parse(data);
        data.forEach(review =>{
            const title = "Title: " + "<strong>" +review.title + "</strong>";
            const titleH2 = document.createElement('h2');
            titleH2.innerHTML = title;

            const userNickname = review.nickname;
            const nameParagraph = document.createElement('h3');
            if(userNickname === ""){
                nameParagraph.textContent = `Author: Anonymous`;

            }
            else{
                nameParagraph.textContent = `Author: ${userNickname}`
            }

            const reviewID = "Review ID: " + review.reviewID;
            const reviewIDParagraph = document.createElement('h4');
            reviewIDParagraph.textContent = reviewID;


            const rating = review.rating;
            let coloredRating = ""
            if(rating < 2.5){
                coloredRating = "Rating: " + "<span style='font-weight: bold; color: red;'>" + review.rating.toFixed(1) + "</span>";
            }
            else{
                coloredRating = "Rating: " + "<span style='font-weight: bold; color: green;'>" + review.rating.toFixed(1) + "</span>";
            }

            const ratingH4 = document.createElement('h4');
            ratingH4.innerHTML = coloredRating;

            const reviewTextHeader = "<strong>" + "Review:" + "</strong>";
            const reviewTextHeaderH4 = document.createElement('h4');
            reviewTextHeaderH4.innerHTML = reviewTextHeader;

            const reviewText = review.reviewText;
            const reviewTextParagraph = document.createElement('p');
            reviewTextParagraph.textContent = reviewText;

            const submissionDate = "Submitted on: " + review.submissionDate;
            const submissionDateParagraph = document.createElement('p');
            submissionDateParagraph.textContent = submissionDate;


            outputElement.appendChild(titleH2);
            outputElement.appendChild(nameParagraph);
            outputElement.appendChild(reviewIDParagraph);
            outputElement.appendChild(ratingH4);
            outputElement.appendChild(reviewTextHeaderH4);
            outputElement.appendChild(reviewTextParagraph);
            outputElement.appendChild(submissionDateParagraph);
            if(userNickname.toLowerCase().trim() === username.toLowerCase().trim()){
                const hotelID = review.hotelID;
                const editLink = document.createElement("a");
                editLink.href = `/hotel/edit?hotelID=${hotelID}&reviewID=${review.reviewID}`;
                editLink.innerHTML = "Edit Review"
                const editParagraph = document.createElement('p');
                editParagraph.appendChild(editLink);
                outputElement.appendChild(editParagraph);


                const deleteLink = document.createElement("a");
                deleteLink.href = `/hotel/delete?hotelID=${hotelID}&reviewID=${review.reviewID}`;
                deleteLink.innerHTML = "Delete Review"
                const deleteParagraph = document.createElement('p');
                deleteParagraph.appendChild(deleteLink);
                outputElement.appendChild(deleteParagraph);


            }
            const reviewTextHeaderHR = document.createElement('hr');
            outputElement.appendChild(reviewTextHeaderHR);

        })

        //document.getElementById("showReviews").innerHTML = data;
    }).
    catch(err => {
        console.log(err);
    });

};

function getButtonValue(event) {
    // Get the value of the clicked button
    const clickedButton = event.submitter; // Using event.submitter to get the clicked button
    return clickedButton.value; // Return the value of the clicked button
}


