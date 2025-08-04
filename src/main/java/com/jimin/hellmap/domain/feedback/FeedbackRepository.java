package com.jimin.hellmap.domain.feedback;

import com.jimin.hellmap.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

}
