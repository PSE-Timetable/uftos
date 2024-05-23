package de.uftos.timefold.domain;

import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.uftos.DataSourceConfig;
import lombok.Getter;

import javax.sql.DataSource;

@Getter
@JsonIdentityInfo(scope = CurriculumTimefoldInstance.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class CurriculumTimefoldInstance {
    @PlanningId
    private int id;
    private int subjectId;
    private int gradeId;
    private int amountOfLessonsPerWeek;

    //get instance from database
    public static CurriculumTimefoldInstance getInstance(int id) {
        DataSource dataSource = new DataSourceConfig().getDataSource();
        return null;
    }
}
