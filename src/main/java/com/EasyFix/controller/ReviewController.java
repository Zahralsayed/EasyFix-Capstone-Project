package com.EasyFix.controller;

import com.EasyFix.dto.ReviewResponseDTO;
import com.EasyFix.model.Review;
import com.EasyFix.repository.ReviewRepository;
import com.EasyFix.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewService reviewService, ReviewRepository reviewRepository) {
        this.reviewService = reviewService;
        this.reviewRepository = reviewRepository;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/submit")
    public ResponseEntity<?> leaveFeedback(
            @RequestParam Long appointmentId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment,
            @AuthenticationPrincipal UserDetails userDetails) {
        {
            try {
                Review review = reviewService.submitReview(userDetails.getUsername(), appointmentId, rating, comment);

                ReviewResponseDTO dto = new ReviewResponseDTO();
                dto.setReviewId(review.getId());
                dto.setRating(review.getRating());
                dto.setComment(review.getComment());
                dto.setCreatedAt(review.getCreatedAt());

                ReviewResponseDTO.AppointmentDetails appDetails = new ReviewResponseDTO.AppointmentDetails();
                appDetails.setId(review.getAppointment().getId());
                appDetails.setStartTime(review.getAppointment().getStartTime());
                appDetails.setEndTime(review.getAppointment().getEndTime());
                appDetails.setStatus(review.getAppointment().getStatus().name());
                appDetails.setProblemDescription(review.getAppointment().getProblemDescription());
                appDetails.setTotalPrice(review.getAppointment().getTotalPrice());

                dto.setAppointment(appDetails);

                return ResponseEntity.ok(dto);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<com.EasyFix.dto.PublicReviewDTO>> getProviderReviews(@PathVariable Long providerId) {
        List<com.EasyFix.model.Review> reviews = reviewRepository.findByProviderIdOrderByCreatedAtDesc(providerId);

        List<com.EasyFix.dto.PublicReviewDTO> dtoList = reviews.stream().map(review -> {
            com.EasyFix.dto.PublicReviewDTO dto = new com.EasyFix.dto.PublicReviewDTO();
            dto.setId(review.getId());
            dto.setCustomerName(review.getCustomer().getUsername());
            dto.setRating(review.getRating());
            dto.setComment(review.getComment());
            dto.setCreatedAt(review.getCreatedAt());

            if (review.getAppointment() != null) {
                dto.setProblemDescription(review.getAppointment().getProblemDescription());
            }
            return dto;
        }).collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> removeReview(@PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(reviewId);
            return ResponseEntity.ok("Review deleted successfully by Admin.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
