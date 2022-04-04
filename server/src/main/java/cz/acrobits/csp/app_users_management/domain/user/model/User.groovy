package cz.acrobits.csp.app_users_management.domain.user.model

import cz.acrobits.csp.core.ast.KeepTraitFieldNames
import cz.acrobits.csp.core.jpa.domain.CreatedAt
import cz.acrobits.csp.core.jpa.domain.CreatedBy
import cz.acrobits.csp.core.jpa.domain.LastModifiedAt
import cz.acrobits.csp.core.jpa.domain.SequenceIdEntity
import cz.acrobits.csp.core.jpa.domain.SoftDeletable
import lombok.Getter
import lombok.Setter
import org.hibernate.annotations.Where
import org.springframework.data.jpa.domain.support.AuditingEntityListener

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners

@Getter
@Setter
@Where(clause = "DELETED_AT IS NULL")
@EntityListeners([AuditingEntityListener.class])
@Entity(name = "APP_USER")
@KeepTraitFieldNames
class User extends SequenceIdEntity<Long> implements
        CreatedAt,
        LastModifiedAt,
        CreatedBy,
        SoftDeletable {

    @Column(name = "USERNAME", nullable = false)
    String username
    @Column(name = "PASSWORD")
    String password
    @Column(name = "CLOUD_ID", nullable = false)
    String cloudId
    @Column(name = "EMAIL", nullable = false)
    String email
    @Column(name = "APPLICATION_ID", nullable = false)
    Long applicationId
    @Column(name = "ORGANIZATION_ID", nullable = false)
    Long organizationId

}
