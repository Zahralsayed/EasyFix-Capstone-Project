package com.EasyFix.service;

import com.EasyFix.model.Appointment;
import com.EasyFix.model.Review;
import com.EasyFix.repository.AppointmentRepository;
import com.EasyFix.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;

    public ReviewService(ReviewRepository reviewRepository, AppointmentRepository appointmentRepository) {
        this.reviewRepository = reviewRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public Review submitReview(String customerEmail, Long appointmentId, Integer rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating metrics must scale strictly between 1 and 5 stars.");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Target appointment details not found."));

        if (!appointment.getCustomer().getEmail().equals(customerEmail)) {
            throw new RuntimeException("Access Denied: You cannot leave feedback on an appointment you did not book.");
        }

        if (appointment.getStatus() != com.EasyFix.enums.AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot review service until the work ticket status has been marked as COMPLETED.");
        }

        if (reviewRepository.existsByAppointmentId(appointmentId)) {
            throw new RuntimeException("Form submission conflict: Feedback has already been recorded for this job order.");
        }

        Review review = new Review();
        review.setCustomer(appointment.getCustomer());
        review.setProvider(appointment.getProvider());
        review.setAppointment(appointment);
        review.setRating(rating);
        review.setComment(comment);

        return reviewRepository.save(review);
    }

    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found with ID: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

}
