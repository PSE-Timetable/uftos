package de.uftos.repositories.database;

import de.uftos.entities.ConstraintInstance;
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
      SELECT instance.id, instance.type
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
               te.first_name ILIKE cast(:arg as varchar) || '%' OR te.last_name ILIKE cast(:arg as varchar) || '%' OR te.acronym ILIKE cast(:arg as varchar) || '%'
               OR st.first_name ILIKE cast(:arg as varchar) || '%' OR st.last_name ILIKE cast(:arg as varchar) || '%'
               OR st_g.name ILIKE cast(:arg as varchar) || '%'
               OR gr.name ILIKE cast(:arg as varchar) || '%'
               OR ro.name ILIKE cast(:arg as varchar) || '%'""")
  Page<ConstraintInstance> findByArguments(String arg, Pageable pageable);

}
