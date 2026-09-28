/* SeatPlan JavaScript Utility Functions */

document.addEventListener('DOMContentLoaded', function () {
    // Print Seating Chart Handler
    const printBtn = document.getElementById('printSeatingBtn');
    if (printBtn) {
        printBtn.addEventListener('click', function () {
            window.print();
        });
    }

    // Auto-dismiss alerts after 5 seconds if present
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.5s ease';
            setTimeout(function () {
                if (alert.parentNode) {
                    alert.parentNode.removeChild(alert);
                }
            }, 500);
        }, 5000);
    });
});

/**
 * Confirm delete action with standard prompt
 */
function confirmDelete(itemName) {
    return confirm('Are you sure you want to delete ' + (itemName || 'this item') + '?');
}