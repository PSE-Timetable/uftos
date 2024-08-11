package de.uftos.repositories.database;

import de.uftos.entities.ConstraintInstance;
import de.uftos.entities.ConstraintSignature;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

/**
 * The repository for accessing the constraint instance database table.
 */
public interface ConstraintInstanceRepository
    extends ListPagingAndSortingRepository<ConstraintInstance, String>,
    ListCrudRepository<ConstraintInstance, String>, JpaSpecificationExecutor<ConstraintInstance> {

  @Query(nativeQuery = true, value = """
      SELECT instance.id, instance.signature_name, instance.type
           FROM constraint_instance instance
           LEFT OUTER JOIN constraint_instance_arguments constraint_instance_arguments
                   ON instance.id=constraint_instance_arguments.constraint_instance_id
           LEFT OUTER JOIN constraint_argument args
                   ON args.id=constraint_instance_arguments.arguments_id
           LEFT OUTER JOIN teachers te
                   ON te.id=args.value
           LEFT OUTER JOIN students st
                   ON st.id=args.value
           LEFT OUTER JOIN student_groups st_g
                   ON st_g.id=args.value
           LEFT OUTER JOIN grades_student_groups gr_st_g
                   ON st_g.id=gr_st_g.student_groups_id
                   AND gr_st_g.grades_id=args.value
           LEFT OUTER JOIN grades gr
                   ON gr.id=gr_st_g.grades_id
           LEFT OUTER JOIN rooms ro
                   ON ro.id=args.value
           LEFT OUTER JOIN tags ta
                   ON ta.id=args.value
           LEFT OUTER JOIN subjects su
                   ON su.id=args.value
           WHERE
               te.first_name ILIKE :arg OR te.last_name ILIKE :arg OR te.acronym ILIKE :arg
               OR st.first_name ILIKE :arg OR st.last_name ILIKE :arg
               OR st_g.name ILIKE :arg
               OR gr.name ILIKE :arg
               OR ro.name ILIKE :arg""")
  Page<ConstraintInstance> findByArguments(String arg, Pageable pageable);

  Page<ConstraintInstance> findBySignatureName(String signatureName, Pageable pageable);

  Optional<ConstraintInstance> findBySignatureAndId(ConstraintSignature signature, String id);
}
