// custom.js

$(document).ready(function () {
    function addToCart(id, name, price) {
        $.ajax({
            url: '/books/add-to-cart',
            type: 'POST',
            data: {
                id: id,
                name: name,
                price: price
            },
            success: function (response) {
                showToast(response);
            },
            error: function (error) {
                console.error("Error adding to cart:", error);
            }
        });
    }

    function showToast(message) {
        $('#cart-toast .toast-body').text(message);
        $('#cart-toast').toast({ delay: 3000 });
        $('#cart-toast').toast('show');
    }

    // Gắn sự kiện vào nút add-to-cart
    $('.add-to-cart-btn').on('click', function () {
        const id = $(this).data('id');
        const name = $(this).data('name');
        const price = $(this).data('price');
        addToCart(id, name, price);
    });
});
