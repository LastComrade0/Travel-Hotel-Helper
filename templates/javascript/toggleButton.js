function hideButton(){
    const reviewsElement = document.getElementById("showReviews");
    const button = document.getElementById("showReviewButton");
    button.style.display = 'none';

    reviewsElement.style.display = "block";

    const newButton = document.createElement("input");
    newButton.type = "submit";
    newButton.value = "Hide reviews"; // New button text

    newButton.onclick = function() {
        // Toggle the visibility of reviews
        if (reviewsElement.style.display === "block") {
            reviewsElement.style.display = "none";
            newButton.value = "Show reviews"; // Change button text back
        } else {
            reviewsElement.style.display = "block";
            newButton.value = "Hide reviews";
        }
    };


    button.parentNode.replaceChild(newButton, button);
}