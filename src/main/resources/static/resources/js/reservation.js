// Reservation Page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Initialize page
    initializeReservationPage();
});

function initializeReservationPage() {
    // Category tab functionality
    setupCategoryTabs();
    
    // Procedure selection functionality
    setupProcedureSelection();
    
    // Time slot selection
    setupTimeSlotSelection();
    
    // Date picker functionality
    setupDatePicker();
    
    // Wishlist button functionality
    setupWishlistButton();
    
    // Price calculation
    updateTotalPrice();
}

// Category Tab Functionality
function setupCategoryTabs() {
    const categoryTabs = document.querySelectorAll('.category-tab');
    
    categoryTabs.forEach(tab => {
        tab.addEventListener('click', function() {
            // Remove active class from all tabs
            categoryTabs.forEach(t => t.classList.remove('active'));
            
            // Add active class to clicked tab
            this.classList.add('active');
            
            // Update procedure options based on category
            updateProcedureOptions(this.dataset.category);
        });
    });
}

function updateProcedureOptions(category) {
    // This would typically fetch data from server based on category
    // For now, we'll just show/hide existing options
    const procedureOptions = document.querySelectorAll('.procedure-option');
    
    procedureOptions.forEach(option => {
        // In a real implementation, you would filter based on category
        option.style.display = 'block';
    });
}

// Procedure Selection Functionality
function setupProcedureSelection() {
    const checkboxes = document.querySelectorAll('.procedure-checkbox');
    
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            updateTotalPrice();
        });
    });
}

function updateTotalPrice() {
    const checkboxes = document.querySelectorAll('.procedure-checkbox:checked');
    let totalPrice = 0;
    
    checkboxes.forEach(checkbox => {
        const priceText = checkbox.closest('.procedure-option').querySelector('.procedure-price').textContent;
        const price = parseInt(priceText.replace(/[^\d]/g, ''));
        totalPrice += price;
    });
    
    // Update total price display
    const priceAmount = document.querySelector('.price-amount');
    if (priceAmount) {
        priceAmount.textContent = totalPrice.toLocaleString();
    }
}

// Wishlist Button Functionality
function setupWishlistButton() {
    const wishlistBtn = document.querySelector('.wishlist-btn');
    if (wishlistBtn) {
        wishlistBtn.addEventListener('click', function() {
            toggleWishlist();
        });
    }
}

function toggleWishlist() {
    const wishlistBtn = document.querySelector('.wishlist-btn');
    const checkedBoxes = document.querySelectorAll('.procedure-checkbox:checked');
    
    if (checkedBoxes.length === 0) {
        alert('시술을 선택해주세요.');
        return;
    }
    
    if (wishlistBtn.textContent === '찜') {
        wishlistBtn.textContent = '찜 완료';
        wishlistBtn.style.backgroundColor = '#28a745';
        wishlistBtn.style.color = 'white';
        wishlistBtn.style.borderColor = '#28a745';
    } else {
        wishlistBtn.textContent = '찜';
        wishlistBtn.style.backgroundColor = '#e8f5e8';
        wishlistBtn.style.color = '#28a745';
        wishlistBtn.style.borderColor = '#28a745';
    }
}

// Time Slot Selection
function setupTimeSlotSelection() {
    const timeSlots = document.querySelectorAll('.time-slot');
    
    timeSlots.forEach(slot => {
        slot.addEventListener('click', function() {
            // Don't allow selection of unavailable slots
            if (this.classList.contains('unavailable')) {
                return;
            }
            
            // Remove selected class from all slots
            timeSlots.forEach(s => s.classList.remove('selected'));
            
            // Add selected class to clicked slot
            this.classList.add('selected');
            
            // Update reservation time in the modal
            updateReservationTime(this.textContent);
        });
    });
}

function updateReservationTime(time) {
    const reservationTimeElement = document.querySelector('.info-row .value');
    if (reservationTimeElement && reservationTimeElement.textContent.includes('15:00')) {
        reservationTimeElement.textContent = reservationTimeElement.textContent.replace('15:00', time);
    }
}

// Date Picker Functionality
function setupDatePicker() {
    const dateInput = document.getElementById('reservationDate');
    if (dateInput) {
        // Set minimum date to today
        const today = new Date();
        const todayString = today.toISOString().split('T')[0];
        dateInput.min = todayString;
        
        // Set maximum date to 1 year from today
        const maxDate = new Date();
        maxDate.setFullYear(maxDate.getFullYear() + 1);
        const maxDateString = maxDate.toISOString().split('T')[0];
        dateInput.max = maxDateString;
        
        // Add event listener for date change
        dateInput.addEventListener('change', function() {
            updateReservationDate(this.value);
        });
    }
}

function updateReservationDate(selectedDate) {
    // Update the reservation date display in the modal
    const dateValue = new Date(selectedDate);
    const formattedDate = dateValue.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    }).replace(/\./g, '.').replace(/\s/g, '');
    
    // Update the date display in the service information
    const serviceInfoElements = document.querySelectorAll('.info-row .value');
    serviceInfoElements.forEach(element => {
        if (element.textContent.includes('2025.10.01')) {
            element.textContent = element.textContent.replace('2025.10.01', formattedDate);
        }
    });
    
    console.log('Selected date:', formattedDate);
}

// Modal Functions
function openReservationModal() {
    const modal = document.getElementById('reservationModal');
    if (modal) {
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden'; // Prevent background scrolling
        
        // Scroll modal to top
        modal.scrollTop = 0;
        
        // Update modal content with selected procedures
        updateModalContent();
    }
}

function closeReservationModal() {
    const modal = document.getElementById('reservationModal');
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto'; // Restore scrolling
    }
}

function updateModalContent() {
    const selectedProcedures = document.querySelectorAll('.procedure-checkbox:checked');
    
    // Update reservation name to "Yoyo"
    const reservationNameElement = document.querySelector('.info-card .info-row .value');
    if (reservationNameElement) {
        reservationNameElement.textContent = 'Yoyo';
    }
    
    // Update total amount in modal
    const checkedBoxes = document.querySelectorAll('.procedure-checkbox:checked');
    let totalPrice = 0;
    
    checkedBoxes.forEach(checkbox => {
        const priceText = checkbox.closest('.procedure-option').querySelector('.procedure-price').textContent;
        const price = parseInt(priceText.replace(/[^\d]/g, ''));
        totalPrice += price;
    });
    
    const modalTotalAmount = document.querySelector('.amount');
    if (modalTotalAmount) {
        modalTotalAmount.textContent = totalPrice.toLocaleString();
    }
}

// Close modal when clicking outside
window.addEventListener('click', function(event) {
    const modal = document.getElementById('reservationModal');
    if (event.target === modal) {
        closeReservationModal();
    }
});

// Confirm reservation
function confirmReservation() {
    // Get selected procedures
    const selectedProcedures = document.querySelectorAll('.procedure-checkbox:checked');
    const selectedTime = document.querySelector('.time-slot.selected');
    
    if (selectedProcedures.length === 0) {
        alert('시술을 선택해주세요.');
        return;
    }
    
    if (!selectedTime) {
        alert('예약 시간을 선택해주세요.');
        return;
    }
    
    // In a real application, this would send data to the server
    const reservationData = {
        procedures: Array.from(selectedProcedures).map(cb => ({
            name: cb.closest('.procedure-option').querySelector('.procedure-name').textContent,
            price: cb.closest('.procedure-option').querySelector('.procedure-price').textContent
        })),
        time: selectedTime.textContent,
        totalPrice: document.querySelector('.amount').textContent
    };
    
    console.log('Reservation Data:', reservationData);
    
    // Show success message
    alert('예약이 완료되었습니다.');
    closeReservationModal();
    
    // In a real application, redirect to confirmation page or show success message
}

// Add event listener for confirm button
document.addEventListener('DOMContentLoaded', function() {
    const confirmBtn = document.querySelector('.confirm-btn');
    if (confirmBtn) {
        confirmBtn.addEventListener('click', confirmReservation);
    }
});

// Utility functions
function formatPrice(price) {
    return price.toLocaleString() + ' 원';
}

function validateReservation() {
    const selectedProcedures = document.querySelectorAll('.procedure-checkbox:checked');
    const selectedTime = document.querySelector('.time-slot.selected');
    
    if (selectedProcedures.length === 0) {
        return { valid: false, message: '시술을 선택해주세요.' };
    }
    
    if (!selectedTime) {
        return { valid: false, message: '예약 시간을 선택해주세요.' };
    }
    
    return { valid: true, message: '' };
}

// Export functions for testing (if needed)
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        openReservationModal,
        closeReservationModal,
        updateTotalPrice,
        confirmReservation,
        validateReservation
    };
}
