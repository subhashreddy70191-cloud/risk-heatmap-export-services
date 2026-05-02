package com.riskheatmap.repository;

import com.riskheatmap.entity.RiskItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RiskItemRepository extends JpaRepository<RiskItem, Long> {

    // ── Basic Finders ─────────────────────────────────────────

    Page<RiskItem> findByIsDeletedFalse(Pageable pageable);

    Optional<RiskItem> findByIdAndIsDeletedFalse(Long id);

    // ── Search ────────────────────────────────────────────────

    @Query("""
           SELECT r FROM RiskItem r
           WHERE r.isDeleted = false
             AND (LOWER(r.title)       LIKE LOWER(CONCAT('%',:q,'%'))
               OR LOWER(r.description) LIKE LOWER(CONCAT('%',:q,'%'))
               OR LOWER(r.category)    LIKE LOWER(CONCAT('%',:q,'%')))
           """)
    Page<RiskItem> searchByKeyword(
            @Param("q") String keyword,
            Pageable pageable);

    // ── Filters ───────────────────────────────────────────────

    Page<RiskItem> findByStatusAndIsDeletedFalse(
            String status,
            Pageable pageable);

    Page<RiskItem> findByCategoryAndIsDeletedFalse(
            String category,
            Pageable pageable);

    @Query("""
           SELECT r FROM RiskItem r
           WHERE r.isDeleted = false
             AND r.createdAt BETWEEN :start AND :end
           """)
    Page<RiskItem> findByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end,
            Pageable pageable);

    // ── Dashboard Stats ───────────────────────────────────────

    @Query("SELECT COUNT(r) FROM RiskItem r WHERE r.isDeleted = false")
    long countActive();

    @Query("SELECT COUNT(r) FROM RiskItem r " +
           "WHERE r.isDeleted = false AND r.status = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT AVG(CAST(r.riskScore AS double)) " +
           "FROM RiskItem r WHERE r.isDeleted = false")
    Double averageRiskScore();

    @Query("SELECT COUNT(r) FROM RiskItem r " +
           "WHERE r.isDeleted = false AND r.riskScore >= 15")
    long countHighRisk();

    // ── Scheduler — items due within 7 days ───────────────────

    @Query("""
           SELECT r FROM RiskItem r
           WHERE r.isDeleted = false
             AND r.dueDate IS NOT NULL
             AND r.dueDate BETWEEN :now AND :threshold
             AND r.status != 'CLOSED'
           """)
    List<RiskItem> findItemsDueSoon(
            @Param("now")       LocalDateTime now,
            @Param("threshold") LocalDateTime threshold);

    // ── CSV Export ────────────────────────────────────────────

    @Query("""
           SELECT r FROM RiskItem r
           WHERE r.isDeleted = false
           ORDER BY r.createdAt DESC
           """)
    List<RiskItem> findAllActiveForExport();
}