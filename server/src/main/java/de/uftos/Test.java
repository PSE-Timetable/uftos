package de.uftos;

import de.uftos.dto.ResourceType;
import de.uftos.dto.ucdl.ast.AbstractSyntaxTreeDto;
import de.uftos.repositories.ucdl.parser.DefinitionParser;
import de.uftos.repositories.ucdl.parser.javacc.ParseException;
import java.util.LinkedHashMap;

public class Test {
  /**
   * Starts the UFTOS application.
   *
   * @param args the command line arguments.
   */
  public static void main(String[] args) throws ParseException {

    String ucdl =
        "3 in this.timeslots[this.day > 2].slot[[1,2,3], this > 2, this in [1,2,3,4,5,6][[1,2,3]]]\n " +
            " and\n" +
            " size(this.timeslots[this.lessons.timeslot]) > 5\n" +
            " or\n" +
            " size(this.subjects.teachers) > 3\n" +
            " or\n" +
            " 5 in [1,2,3,4,5,6,7,8,9,0][3,4,5,6,7]\n" +
            " and\n" +
            " isEmpty(this[this == this])";


    //String ucdl = "isEmpty(this[this == this])";

    //String ucdl = "3 in [1,2,3][lesson.index,2]";

    LinkedHashMap<String, ResourceType> params = new LinkedHashMap<>();
    params.put("this", ResourceType.TIMETABLE);
    params.put("lesson", ResourceType.LESSON);
    AbstractSyntaxTreeDto definition = DefinitionParser.parseDefinition(
        ucdl,
        params
    );

    System.out.println(ucdl);
    System.out.println(definition);
  }

}
