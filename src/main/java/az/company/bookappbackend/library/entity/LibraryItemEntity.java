package az.company.bookappbackend.library.entity;

import az.company.bookappbackend.book.entity.BookEntity;
import az.company.bookappbackend.common.enums.ReadingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "library_items",
        indexes = {
                @Index(name = "idx_library_items_library_id", columnList = "library_id"),
                @Index(name = "idx_library_items_book_id", columnList = "book_id"),
                @Index(name = "idx_library_items_reading_status", columnList = "reading_status")
        }
)
@EqualsAndHashCode(exclude = {"book", "library"})
public class LibraryItemEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 981318479946316947L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    private LibraryEntity library;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private BookEntity book;

    @Column(name = "reading_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReadingStatus readingStatus;
}
