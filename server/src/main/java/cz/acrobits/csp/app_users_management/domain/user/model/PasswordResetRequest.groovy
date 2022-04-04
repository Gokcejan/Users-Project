package cz.acrobits.csp.app_users_management.domain.user.model

import cz.acrobits.csp.core.ast.KeepTraitFieldNames
import cz.acrobits.csp.core.jpa.domain.CreatedAt
import cz.acrobits.csp.core.jpa.domain.LastModifiedAt
import cz.acrobits.csp.core.jpa.domain.SequenceIdEntity
import cz.acrobits.csp.core.jpa.domain.SoftDeletable
import org.hibernate.annotations.Where
import org.springframework.data.jpa.domain.support.AuditingEntityListener

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Where(clause = "DELETED_AT IS NULL")
@EntityListeners([AuditingEntityListener.class])
@Entity(name = "PASSWORD_RESET_REQUEST")
@KeepTraitFieldNames
class PasswordResetRequest extends SequenceIdEntity<Long> implements
        CreatedAt,
        LastModifiedAt,
        SoftDeletable {

    @Column(name = "EMAIL", nullable = false)
    String email
    @Column(name = "TOKEN", nullable = false)
    String token
    @JoinColumn(name = "APP_USER", nullable = false)
    @ManyToOne()
    User user
}
