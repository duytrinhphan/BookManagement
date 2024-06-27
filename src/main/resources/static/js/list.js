$(document).ready(function () {
  $.ajax({
    url: 'http://localhost:8080/api/v1/role',
    type: 'GET',
    dataType: 'text',
    success: function (userRole) {
      $.ajax({
        url: 'http://localhost:8080/api/v1/books',
        type: 'GET',
        dataType: 'json',
        success: function (data) {
          let trHTML = '';
          $.each(data, function (i, item) {
            trHTML +=
              '<tr id="book-' + item.id + '">' +
              '<td>' + item.id + '</td>' +
              '<td>' + item.title + '</td>' +
              '<td>' + item.author + '</td>' +
              '<td>' + item.price + '</td>' +
              '<td>' + item.category + '</td>';
            if (userRole.includes('USER')) {
              trHTML += '<td>' + '<a href="#" class="text-primary">View</a>' + '</td>';
            } else {
              trHTML += '<td></td>';
            }
            if (userRole.includes('ADMIN')) {
              trHTML +=
                '<td>' +
                '<a href="#" class="text-primary">Edit</a> | ' +
                '<a href="#" class="text-danger" onclick="apiDeleteBook(' + item.id + '); return false;">Delete</a>' +
                '</td>';
            } else {
              trHTML += '<td></td>';
            }
            trHTML += '</tr>';
          });
          $('#book-table-body').html(trHTML);
        },
        error: function (xhr, status, error) {
          console.error('Error fetching books:', error);
        }
      });
    },
    error: function (xhr, status, error) {
      console.error('Error fetching role:', error);
    }
  });
});
function apiDeleteBook(id) {
  if (confirm('Are you sure you want to delete this book?')) {
    $.ajax({
      url: 'http://localhost:8080/api/v1/books/' + id,
      type: 'DELETE',
      success: function () {
        alert('Book deleted successfully!');
        $('#book-' + id).remove();
      },
      error: function (xhr, status, error) {
        console.error('Error deleting book:', error);
        console.log('Status:', status);
        console.log('XHR:', xhr);
        alert('Error deleting book: ' + xhr.responseText);
      }
    });
  }
}


